Bank Account Kata — Docker and E2E

This project is a simple bank account API. It has three main endpoints: deposit, withdraw, and list transactions. It uses PostgreSQL and Liquibase for the database, and Spring Boot for the API.

**Quick Start**
- Start everything with Docker: `./e2e.sh`
- Or run the stack: `docker compose up -d --build`
- Check health: `curl http://localhost:8080/actuator/health`

**Prerequisites**
- Docker Desktop with Compose plugin (or `docker-compose`).
- Bash and `curl` to run the `e2e.sh` script.
- Python 3 (the script uses it to create UUIDs).
- Free ports: `8080` (app) and `5432` (PostgreSQL).
  - If a port is busy, edit `docker-compose.yml` and change the port mapping.

**Ports**
- App: `http://localhost:8080` → make sure nothing else uses port 8080.
- PostgreSQL: `localhost:5432` → make sure nothing else uses port 5432.

**Seed Data**
- When the app starts with Docker, Liquibase creates tables and inserts one default account.
- Default account id: `11111111-1111-1111-1111-111111111111`.

**Endpoints**
- `POST /accounts/{id}/deposit`
- `POST /accounts/{id}/withdraw`
- `GET /accounts/{id}/transactions`
- Health: `GET /actuator/health`

Example calls (use the default account):
- Deposit: `curl -X POST -H 'Content-Type: application/json' -d '{"amount":"50.00","operationId":"<uuid>"}' http://localhost:8080/accounts/11111111-1111-1111-1111-111111111111/deposit`
- Withdraw: `curl -X POST -H 'Content-Type: application/json' -d '{"amount":"20.00","operationId":"<uuid>"}' http://localhost:8080/accounts/11111111-1111-1111-1111-111111111111/withdraw`
- Transactions: `curl http://localhost:8080/accounts/11111111-1111-1111-1111-111111111111/transactions`

**Why operationId?**
- We send an `operationId` in each write request (deposit or withdraw).
- This id makes the operation idempotent.
- If the user double-clicks or the network retries, the same `operationId` will not apply the change twice.
- This protects against duplicate updates and gives a safe user experience.

**Why lockById?**
- Many requests can hit the same account at the same time.
- `lockById` uses a database lock when loading the account.
- This prevents race conditions and keeps the balance correct.
- With this lock, two threads cannot update the same account at the same time.

**E2E Script**
- Script: `e2e.sh`
- It builds the image, starts Docker Compose, waits for health, then calls:
  - `/deposit` (expects 201), `/withdraw` (expects 200), `/transactions` (expects 200).
- Set `KEEP=1` to keep containers running after the script: `KEEP=1 ./e2e.sh`

**Troubleshooting**
- If health does not become UP, check logs:
  - App logs: `docker compose logs -f app`
  - DB logs: `docker compose logs -f db`
- If a port is in use, edit `docker-compose.yml` and change `8080:8080` or `5432:5432`.
- If you use `docker-compose` (with a dash), replace `docker compose` with `docker-compose` in commands (and in `e2e.sh` if needed).

