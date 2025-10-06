Bank Account Kata — Persistence & Migrations

What’s included
- Liquibase migrations at `src/main/resources/db/changelog` (master + schema).
- PostgreSQL driver and Testcontainers for integration tests.
- JPA entities mapped to the Liquibase schema with NUMERIC(19,4).
- Pessimistic locking for `AccountJpaRepository.lockById(...)` (SELECT FOR UPDATE).

Run tests
- Uses in-memory H2 and Liquibase.
- Command: `mvn -f backend/pom.xml test`

Run locally (H2 in-memory)
- Default profile uses H2 and Liquibase to create the schema.
- Start the app: `mvn -f backend/pom.xml spring-boot:run`

Run against PostgreSQL
- Provide datasource via env vars or properties at startup:
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bank`
  - `SPRING_DATASOURCE_USERNAME=postgres`
  - `SPRING_DATASOURCE_PASSWORD=postgres`
  - Ensure `spring.jpa.hibernate.ddl-auto=none` (already default).
- Liquibase runs automatically and creates `accounts`, `transactions`, `operations` with constraints.
- Or use the dedicated profile:
  - `mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=postgres`
  - Config file: `src/main/resources/application-postgres.yml`

Schema highlights
- `accounts.balance`, `transactions.amount`, `transactions.resulting_balance` are `NUMERIC(19,4)`.
- `transactions.operation_id` has a UNIQUE constraint (nullable for compatibility with current domain).
- `AccountJpaRepository.lockById` uses `PESSIMISTIC_WRITE`, yielding `SELECT ... FOR UPDATE`.
