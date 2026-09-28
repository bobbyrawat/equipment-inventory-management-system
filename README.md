# Equipment Inventory Management System

A REST backend for equipment stock, branch transfers, assignments, purchases, expenditures, and audit history.

## Technology stack

- Java 17 and Spring Boot 3
- Maven, Spring Web, Spring Data JPA/Hibernate, MySQL
- Spring Security with stateless JWT authentication and BCrypt password hashing
- Jakarta Bean Validation, Lombok, and Springdoc OpenAPI/Swagger UI

## Database setup

Install and start MySQL, then create the database (or allow the JDBC URL option below to create it):

```sql
CREATE DATABASE equipment_inventory;
```

The explicit local `dev` profile uses Hibernate `ddl-auto=update` to create/update tables for learning. Without that profile, schema handling defaults to `validate`; use managed schema migrations in a production deployment.

## Environment variables

Copy `.env.example` to `.env` and provide your own values. Never commit `.env` (it is gitignored).

Required:

- `DB_URL` — e.g. `jdbc:mysql://localhost:3306/equipment_inventory?serverTimezone=UTC`
- `DB_USERNAME` and `DB_PASSWORD` — MySQL account credentials
- `JWT_SECRET` — unique random key material of at least 32 bytes (Base64 is supported)

Optional:

- `JWT_EXPIRATION_MS` — token lifetime; defaults to 3600000 (one hour)
- `CORS_ALLOWED_ORIGINS` — comma-separated exact origins; defaults to `http://localhost:3000`
- `LOW_STOCK_THRESHOLD` — dashboard low-stock boundary; defaults to 5

Spring Boot does not read `.env` automatically. The included VS Code Java launch configuration loads `${workspaceFolder}/.env`; for Maven in a terminal, export/set the same variables in that terminal first, including `SPRING_PROFILES_ACTIVE=dev` for first-time local schema creation.

## Run and test

Use a JDK 17 installation and Maven:

```text
mvn clean test
mvn spring-boot:run
```

Alternatively, run **Equipment Inventory Backend** from VS Code's Run and Debug view to use the local `.env` file. Swagger UI is available at `http://localhost:8080/swagger-ui.html`; OpenAPI JSON is at `http://localhost:8080/api-docs`.

## API overview

All APIs except authentication and OpenAPI documentation require `Authorization: Bearer <JWT>`.

| Area | Main routes | Access |
| --- | --- | --- |
| Authentication | `POST /api/auth/register`, `POST /api/auth/login` | Public; public registration creates a branch-bound logistics officer only |
| Users | `/api/users` | ADMIN |
| Branches | `/api/branches` | Read: all signed-in roles (branch-scoped); changes: ADMIN |
| Equipment | `/api/equipment`, `/api/equipment/search?q=...` | All roles, restricted to own branch except ADMIN |
| Purchases | `/api/purchases` | All roles, restricted to own branch except ADMIN |
| Transfers | `/api/transfers`, `PATCH /api/transfers/{id}/status` | All roles; branch scoped except ADMIN |
| Assignments | `/api/assignments` | All roles, restricted to own branch except ADMIN |
| Expenditures | `/api/expenditures` | All roles, restricted to own branch except ADMIN |
| Audit logs | `/api/audit-logs` | ADMIN |
| Dashboard | `GET /api/dashboard` | All roles; branch scoped except ADMIN |

Purchases add to stock. Assignment and expenditure subtract stock. Transfers start as `PENDING`; setting status to `COMPLETED` atomically moves stock from source to destination under a database row lock. Status may also be changed to `APPROVED` or `REJECTED`. Dashboard balances expose opening balance, purchase, transfer-in/out, assigned, expended, and closing balance values.

Error responses contain `timestamp`, `status`, `message`, and `path`. API request/response bodies are DTOs rather than JPA entities. Service methods enforce branch scope in addition to role checks.

## Project layers

`controller`, `service`, `repository`, `entity`, `dto`, `exception`, `config`, and `security` are separated under `com.example.inventory`. No frontend is included.
