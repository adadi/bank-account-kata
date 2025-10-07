#!/usr/bin/env bash
set -euo pipefail

KEEP=${KEEP:-0}
ACCOUNT_ID="11111111-1111-1111-1111-111111111111"
COMPOSE="docker compose"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is required for this script" >&2
  exit 1
fi

cleanup() {
  if [[ "$KEEP" != "1" ]]; then
    echo "\nBringing down stack..."
    $COMPOSE down -v >/dev/null 2>&1 || true
  else
    echo "\nKeeping containers running (KEEP=1)."
  fi
}
trap cleanup EXIT

echo "Building and starting containers..."
$COMPOSE up -d --build

echo "Waiting for Postgres to be healthy..."
db_cid=$($COMPOSE ps -q db)
for i in $(seq 1 120); do
  status=$(docker inspect -f '{{.State.Health.Status}}' "$db_cid" 2>/dev/null || echo "starting")
  [[ "$status" == "healthy" ]] && break
  sleep 1
done
if [[ "$status" != "healthy" ]]; then
  echo "Database did not become healthy in time" >&2
  exit 1
fi

echo "Waiting for app actuator health..."
for i in $(seq 1 180); do
  if curl -fs http://localhost:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
    break
  fi
  sleep 1
done

if ! curl -fs http://localhost:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
  echo "App did not become healthy in time" >&2
  exit 1
fi

gen_uuid() {
  python3 - <<'PY'
import uuid; print(uuid.uuid4())
PY
}

echo "\nCalling GET /v1/accounts/${ACCOUNT_ID}..."
code=$(curl -sS -o /tmp/account.json -w '%{http_code}' \
  http://localhost:8080/v1/accounts/${ACCOUNT_ID})
if [[ "$code" != "200" ]]; then
  echo "Get account failed, status=$code, body=$(cat /tmp/account.json)" >&2
  exit 1
fi
if ! grep -q '"accountId":"'"$ACCOUNT_ID"'"' /tmp/account.json; then
  echo "Get account: unexpected accountId in body=$(cat /tmp/account.json)" >&2
  exit 1
fi
if ! grep -q '"balance":' /tmp/account.json; then
  echo "Get account: missing balance in body=$(cat /tmp/account.json)" >&2
  exit 1
fi
echo "Get account OK (200)"

echo "\nCalling /deposit..."
OP1=$(gen_uuid)
code=$(curl -sS -o /tmp/deposit.json -w '%{http_code}' -X POST \
  -H 'Content-Type: application/json' \
  -d '{"amount":"50.00","operationId":"'"$OP1"'"}' \
  http://localhost:8080/v1/accounts/${ACCOUNT_ID}/deposit)
if [[ "$code" != "201" ]]; then
  echo "Deposit failed, status=$code, body=$(cat /tmp/deposit.json)" >&2
  exit 1
fi
echo "Deposit OK (201)"

echo "Calling /withdraw..."
OP2=$(gen_uuid)
code=$(curl -sS -o /tmp/withdraw.json -w '%{http_code}' -X POST \
  -H 'Content-Type: application/json' \
  -d '{"amount":"20.00","operationId":"'"$OP2"'"}' \
  http://localhost:8080/v1/accounts/${ACCOUNT_ID}/withdraw)
if [[ "$code" != "200" ]]; then
  echo "Withdraw failed, status=$code, body=$(cat /tmp/withdraw.json)" >&2
  exit 1
fi
echo "Withdraw OK (200)"

echo "Calling /transactions..."
code=$(curl -sS -o /tmp/transactions.json -w '%{http_code}' \
  http://localhost:8080/v1/accounts/${ACCOUNT_ID}/transactions)
if [[ "$code" != "200" ]]; then
  echo "Transactions failed, status=$code, body=$(cat /tmp/transactions.json)" >&2
  exit 1
fi

if [[ ! -s /tmp/transactions.json ]]; then
  echo "Transactions response empty" >&2
  exit 1
fi

echo "Transactions OK (200)"

echo "\nAll checks passed: health, deposit, withdraw, transactions."
