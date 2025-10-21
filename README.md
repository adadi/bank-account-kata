Bank Account Kata

This project is a simple bank account API. It has three main endpoints: deposit, withdraw, and list transactions. It uses PostgreSQL and Liquibase for the database, and Spring Boot for the API, with a Vue 3 + Vite frontend.

**Quick Start**
- Free ports: `80` (front), `8080` (app), `5432` (PostgreSQL).
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
- Front: `http://localhost:80` → make sure nothing else uses port 80.
- Backend: `http://localhost:8080` → make sure nothing else uses port 8080.
- PostgreSQL: `localhost:5432` → make sure nothing else uses port 5432.

**Seed Data**
- When the app starts with Docker, Liquibase creates tables and inserts one default account.
- Default account id: `11111111-1111-1111-1111-111111111111`.

**Endpoints**
- `GET /v1/accounts/{id}`
- `POST /v1/accounts/{id}/deposit`
- `POST /v1/accounts/{id}/withdraw`
- `GET /v1/accounts/{id}/transactions`
- `GET /v1/accounts/{id}/statement`
- Health: `GET /actuator/health`

**Swagger**
http://localhost:8080/swagger-ui/index.html

Example calls (use the default account):
- Account: `curl http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111`
- Deposit: `curl -X POST -H 'Content-Type: application/json' -d '{"amount":"50.00","operationId":"<uuid>"}' http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111/deposit`
- Withdraw: `curl -X POST -H 'Content-Type: application/json' -d '{"amount":"20.00","operationId":"<uuid>"}' http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111/withdraw`
- Transactions: `curl http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111/transactions`
- Statement: `curl http://localhost:8080/v1/accounts/11111111-1111-1111-1111-111111111111/statement`

**Check health**
 Open http://localhost:8080/actuator/health

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
  - `/v1/accounts/deposit` (expects 201), `/v1/accounts/withdraw` (expects 200), `/v1/accounts/transactions` (expects 200), `/v1/accounts/` (expects 200),`/v1/accounts/statement` (expects 200) .
- Set `KEEP=1` to keep containers running after the script: `KEEP=1 ./e2e.sh`

**Troubleshooting**
- If health does not become UP, check logs:
  - Frontend logs: `docker compose logs -f frontend`
  - Backend logs: `docker compose logs -f app`
  - DB logs: `docker compose logs -f db`
- If a port is in use, edit `docker-compose.yml` and change `8080:8080` or `5432:5432`or `80:80`.
- If you use `docker-compose` (with a dash), replace `docker compose` with `docker-compose` in commands (and in `e2e.sh` if needed).

**KATA**
Bank account kata  
Think of your personal bank account experience  
When in doubt, go for the simplest solution.  
Requirements  
• Deposit and Withdrawal  
• Account statement (date, amount, balance)  
• Statement printing  
User Stories  
US 1:  
In order to save money    
As a bank client  
I want to make a deposit in my account  
US 2:  
In order to retrieve some or all of my savings  
As a bank client  
I want to make a withdrawal from my account  

**Run project without docker compose**

Prerequisites

Backend: Java JDK 21, Maven  
Frontend: Node.js 18+ (ideally 20 LTS), npm  
Optional database: PostgreSQL (if you don’t want to use H2)  
Free ports:
8080 (backend), 5173 (frontend dev), 5432 (Postgres),4173 (frontend preview)

Backend — Quick Dev (in-memory H2)

macOS/Linux:  
```bash
cd backend  
SPRING_LIQUIBASE_CONTEXTS=e2e mvn spring-boot:run
```


Windows PowerShell:  
```bash
cd backend  
$env:SPRING_LIQUIBASE_CONTEXTS="e2e"; mvn spring-boot:run
```

Windows CMD:
```bash
cd backend  
set SPRING_LIQUIBASE_CONTEXTS=e2e; mvn spring-boot:run
```

Why SPRING_LIQUIBASE_CONTEXTS=e2e? It adds the default account
11111111-1111-1111-1111-111111111111.

Check: Health: http://localhost:8080/actuator/health
Swagger:http://localhost:8080/swagger-ui/index.html

Backend — With Local PostgreSQL (optional)

Make sure the database bank exists on localhost:5432.
Replace {YOUR_USERNAME}, and {YOUR_PASSWORD} with your own database information for the following commands:
Also modify SPRING_DATASOURCE_URL if your database is not local or uses a different port.

macOS/Linux:  
```bash
cd backend  
SPRING_PROFILES_ACTIVE=postgres SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bank SPRING_DATASOURCE_USERNAME={YOUR_USERNAME} SPRING_DATASOURCE_PASSWORD={YOUR_PASSWORD} SPRING_LIQUIBASE_CONTEXTS=e2e mvn spring-boot:run
```

Windows PowerShell:  
```bash
cd backend  
$env:SPRING_PROFILES_ACTIVE="postgres"; $env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/bank"; $env:SPRING_DATASOURCE_USERNAME="{YOUR_USERNAME}"; $env:SPRING_DATASOURCE_PASSWORD="{YOUR_PASSWORD}"; $env:SPRING_LIQUIBASE_CONTEXTS="e2e"; mvn spring-boot:run
```
Windows CMD:  
```bash
cd backend  
set "SPRING_PROFILES_ACTIVE=postgres"; set "SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bank"; set "SPRING_DATASOURCE_USERNAME={YOUR_USERNAME}"; set "SPRING_DATASOURCE_PASSWORD={YOUR_PASSWORD}"; set "SPRING_LIQUIBASE_CONTEXTS=e2e"; mvn spring-boot:run
```

Backend — Packaged Mode (JAR)

Build:
```bash
cd backend
mvn -DskipTests package
```

Backend — Javadoc

The project is configured to generate Javadoc including private members.

Generate Javadoc HTML:
```bash
cd backend
mvn javadoc:javadoc
```
The documentation will be generated in `backend/target/apidocs/index.html`.

Generate Javadoc JAR:
```bash
cd backend
mvn javadoc:jar
```
The JAR will be created at `backend/target/bankaccount-0.0.1-SNAPSHOT-javadoc.jar`.

Note: The Javadoc JAR is automatically generated when running `mvn package`.

Run with H2: macOS/Linux:  
```bash
SPRING_LIQUIBASE_CONTEXTS=e2e java -jar target/bankaccount-0.0.1-SNAPSHOT.jar
```

Windows PowerShell:  
```bash
$env:SPRING_LIQUIBASE_CONTEXTS="e2e"; java -jar target/bankaccount-0.0.1-SNAPSHOT.jar
```

Windows CMD:  
```bash
set "SPRING_LIQUIBASE_CONTEXTS=e2e"; java -jar target/bankaccount-0.0.1-SNAPSHOT.jar
```

Run with PostgreSQL: macOS/Linux:  
```bash
SPRING_PROFILES_ACTIVE=postgres SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bank SPRING_DATASOURCE_USERNAME={YOUR_USERNAME} SPRING_DATASOURCE_PASSWORD={YOUR_PASSWORD} SPRING_LIQUIBASE_CONTEXTS=e2e java -jar target/bankaccount-0.0.1-SNAPSHOT.jar
```

Run with PostgreSQL: PowerShell:  
```bash
$env:SPRING_PROFILES_ACTIVE="postgres"; $env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/bank"; $env:SPRING_DATASOURCE_USERNAME="{YOUR_USERNAME}"; $env:SPRING_DATASOURCE_PASSWORD="{YOUR_PASSWORD}"; $env:SPRING_LIQUIBASE_CONTEXTS="e2e"; java -jar target/bankaccount-0.0.1-SNAPSHOT.jar
```

Windows CMD:  
```bash
set "SPRING_PROFILES_ACTIVE=postgres"; set "SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bank"; set "SPRING_DATASOURCE_USERNAME={YOUR_USERNAME}"; set "SPRING_DATASOURCE_PASSWORD={YOUR_PASSWORD}"; set "SPRING_LIQUIBASE_CONTEXTS=e2e"; java -jar target/bankaccount-0.0.1-SNAPSHOT.jar
```

Frontend — Dev Mode (recommended)

```bash
cd frontend  
npm ci 
npm run dev
```

Open: http://localhost:5173

The proxy sends all requests from /api to http://localhost:8080 (see
frontend/vite.config.ts:6) The base URL is /api (see frontend/.env:1).

Start the backend before running the frontend.

Frontend — Build

```bash
cd frontend 
npm ci  
npm run build  
npm run preview
```

Note: `vite preview` can also proxy `/api` if configured. By default, `preview.proxy` inherits `server.proxy`. 
For local use, `npm run dev` is still recommended (HMR, DX), but `npm run preview` works with the same proxy.

Open: http://localhost:4173
