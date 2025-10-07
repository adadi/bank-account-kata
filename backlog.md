
US1 — Spring Boot project ready

Goal: project compiles and starts.

Tests to write
	•	API: GET /actuator/health → 200 OK.
	•	Integration: Spring context loads without errors.

Given/When/Then
	•	Given the application starts
	•	When I call /actuator/health
	•	Then I get status: UP.

⸻

US2 — Account model

Rules: deposit > 0, withdrawal > 0 and ≤ balance.

Tests to write (unit)
	•	deposit(100) → balance = 100.
	•	deposit(-1) → IllegalArgumentException.
	•	withdraw(30) after deposit(100) → balance = 70.
	•	withdraw(200) with balance 100 → IllegalStateException.
	•	Precision: deposit(0.10) ten times → balance = 1.00.

Given/When/Then
	•	Given balance = 100
	•	When I withdraw 30
	•	Then balance = 70.

⸻


US3 — AccountRepository port + domain service

Goal: separate domain and infrastructure.

Tests to write
	•	Unit tests (with mocks) for AccountService:
	•	Calls repository.lockById(id) (pessimistic lock) on withdrawal.
	•	Saves transaction and updates balance.
	•	Propagates domain exceptions.

Given/When/Then
	•	Given an account with balance 100
	•	When withdraw(40)
	•	Then service calls repo, balance becomes 60, transaction created.

⸻

US4 — Endpoint POST /accounts/{id}/deposit

Body: { amount, operationId }.

Tests to write
	•	Unit: validate amount > 0, idempotency (see US8).
	•	REST API (MockMvc):
	•	201/200 for valid deposit.
	•	400 if amount <= 0 or missing.
	•	Integration (DB):
	•	After call, balance increases.
	•	One transactions row added.

Given/When/Then
	•	Given account balance = 0
	•	When I deposit 50
	•	Then balance = 50 and transaction type = “deposit”.

⸻

US5 — Endpoint POST /accounts/{id}/withdraw

Body: { amount, operationId }.

Tests to write
	•	Unit: amount > 0; if balance < amount → business exception.
	•	REST API:
	•	200 if OK.
	•	409 if insufficient funds.
	•	400 if invalid amount.
	•	Integration (DB + lock):
	•	Correct balance update.
	•	Transaction “withdrawal” added.

Given/When/Then
	•	Given balance = 100
	•	When I withdraw 120
	•	Then response = 409 and balance remains 100.

⸻

US6 — Liquibase migrations + Postgres SQL

Goal: connect the PostgreSQL database after validating the domain.

Tables: accounts, transactions.

Tests to write (integration via PostgreSQL Testcontainers or H2)
	•	Tables exist.
	•	UNIQUE constraint on transactions.operation_id.
	•	Correct column types (NUMERIC(19,4)).
	•	JPA adapter implements the AccountRepository port.
	•	Method findByIdForUpdate() applies SELECT … FOR UPDATE.

Given / When / Then:
	•	Given the app starts,
	•	When Liquibase runs,
	•	Then tables and constraints exist.

⸻

US7 — Operation(Idempotency) by operationId

Goal: prevent double processing (double-click, network retry).

Tests to write
	•	Integration (DB):
	•	Two deposits with same operationId → one transaction only, balance +1 time.
	•	Two withdrawals with same operationId → one execution only.
	•	REST API:
	•	First call → 200/201, second → 200/201 with same result (operation(idempotency)), never 500.

Given/When/Then
	•	Given operationId=ABC, deposit 50
	•	When I send the same deposit again
	•	Then total balance +50, not +100.

⸻

US8 — Prevent concurrent withdrawals

Goal: no double concurrent withdrawals.

Tests to write
	•	Concurrent integration (2 threads):
	•	Balance 100, two withdrawals of 80 at the same time → final result: balance = 20 + 1 success, 1 failure (409).
	•	Verify repo uses SELECT … FOR UPDATE (or optimistic versioning + retry).

Given/When/Then
	•	Given balance = 100
	•	When two concurrent withdrawals of 80
	•	Then only one succeeds, the other fails.

⸻

US9 — Endpoint GET /accounts/{id}/transactions?from&to

Tests to write
	•	REST API:
	•	Returns list sorted by date desc.
	•	Filters by period (from, to).
	•	Integration (DB):
	•	After several operations, statement contains correct rows (types, amounts, post-operation balances).

Given/When/Then
	•	Given 3 operations (Deposit 100, Withdraw 30, Deposit 10)
	•	When I request the statement
	•	Then I get these 3 rows in order with correct post-balances.

⸻

**US10 — Logs, errors, Swagger**
Goal: provide clear error messages, and automatic API documentation.

Tests to write
	•	REST API:
		•	Clear JSON error format (code, message, operationId).
		•	404 for account not found.
		•	Swagger UI available at /swagger-ui.html or /api-docs.
		•	All endpoints documented (deposit, withdraw, transactions).
	•	Unit:
		•	Exception mapping → correct HTTP statuses.
		•	Verify OpenAPI configuration loads without errors.
Given/When/Then
	•	Given non-existent account
	•	When I withdraw
	•	Then 404 JSON {"code":"ACCOUNT_NOT_FOUND"}.
	Given/When/Then
	•	Given a non-existent account,
	•	When I withdraw,
	•	Then response = 404 JSON {"code":"ACCOUNT_NOT_FOUND"}.
	•	Given the application runs,
	•	When I open /swagger-ui.html,
	•	Then I can browse and test all endpoints with their descriptions.

⸻

**US11 — Docker & Compose**

Tests to write
	•	Light e2e script:
	•	Run docker compose up (app + PostgreSQL).
	•	Call /deposit, /withdraw, /transactions → all OK.
	•	Healthchecks OK.

⸻

**US12  —  Start Vue 3 Project**

Context: create the foundation of the frontend.
Goal: Vue 3 app ready with routing and global state management.

API: ping GET /actuator/health.
UI/UX: homepage titled “Bank Account Kata”.

Rules / Validation:
	•	Environment variable VITE_API_BASE_URL must be defined.

Errors:
	•	If health ≠ UP, display banner “API unavailable”.

Given / When / Then
	•	Given the app starts,
	•	When I open /,
	•	Then I see “Bank Account Kata” and the API status.

Sub-tasks
	•	Create project: npm create vite@latest → choose Vue + TypeScript.
	•	Install dependencies: vue-router, pinia, axios.
	•	Add routes: /, /settings, /deposit, /withdraw, /transactions.
	•	Create src/services/http.ts (axios baseURL + JSON headers).
	•	Add HealthBadge component (calls /actuator/health).

Definition of Done (DoD):
	•	npm run dev, npm run build, npm run lint, npm run test all succeed.
	•	API health status displayed on the homepage.
