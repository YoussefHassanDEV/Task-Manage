Task-Manage API
A clean Spring Boot REST API for user authentication and task management, featuring JWT-based security, BCrypt password hashing, H2 in-memory database, DTOs, and global error handling. It includes optional refresh-token support and unit/controller tests to demonstrate structure and behavior.

Features
Registration with hashed passwords (BCrypt).

Login issuing JWT access and refresh tokens.

Stateless auth via a JWT filter; protected tasks CRUD.

Global error responses with consistent JSON.

H2 in-memory DB; no external setup required.

Tests for controller and services.

Tech Stack
Spring Boot, Spring Web, Spring Security, Spring Data JPA.

H2 Database for persistence during development/tests.

JJWT for token creation/validation.

JUnit 5, Mockito, Spring Test for testing.

Getting Started
Prerequisites:

Java 21+ and Maven.

Run:

mvn spring-boot:run

H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:todo, user: sa, password: blank)

Configuration
Update src/main/resources/application.properties:

text
spring.application.name=Task-Manage
server.port=8080

# H2 datasource (in-memory)
spring.datasource.url=jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driverClassName=org.h2.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 web console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# App JWT
app.jwt.secret=change-this-to-a-long-random-secret-string-at-least-32-bytes
app.jwt.accessExpirationMillis=900000
app.jwt.refreshExpirationMillis=604800000
These properties enable distinct access/refresh expirations used by JwtUtils.

API
Base URL: http://localhost:8080

Auth

POST /auth/register

Body: {"email":"user@example.com","password":"mypassword","name":"John Doe"}

Returns: 201 Created (no body)

POST /auth/login

Body: {"email":"user@example.com","password":"mypassword"}

200 OK: {"accessToken":"...","expiresInMillis":900000,"refreshToken":"...","refreshExpiresInMillis":604800000}

POST /auth/refresh

Body: {"refreshToken":"<REFRESH>"}

200 OK: same structure as login with new tokens

POST /auth/logout

Header: Authorization: Bearer <ACCESS>

200 OK; access token is blacklisted until expiration.

Tasks (require Authorization: Bearer <ACCESS>)

POST /tasks

Body: {"title":"T","description":"D","status":"OPEN"}

201 Created: TaskResponse

GET /tasks

200 OK: list of tasks owned by the authenticated user.

PUT /tasks/{id}

Body: {"status":"DONE"}

200 OK: updated TaskResponse

DELETE /tasks/{id}

204 No Content

Error Responses
Handled by a global @RestControllerAdvice with structured JSON fields (timestamp, status, error, message, path, validationErrors). Typical cases:

400 Bad Request: validation errors, invalid credentials

401 Unauthorized: missing/invalid access token

403 Forbidden: accessing another user’s task

404 Not Found: task not found

Security
Stateless JWT auth; filter extracts, validates, and sets authentication.

/auth/** and /h2-console/** are publicly accessible; all other endpoints require a valid token.

Passwords hashed with BCrypt.

Access tokens short-lived; refresh tokens carry typ=refresh and longer expiry; refresh rotates tokens.

Data Model
User: id, email (unique), passwordHash, name.

Task: id, title, description, status (OPEN/DONE), owner.

Example Requests
Auth

Register:

curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"mypassword","name":"John Doe"}'

Login:

curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"mypassword"}'

Refresh:

curl -s -X POST http://localhost:8080/auth/refresh -H "Content-Type: application/json" -d '{"refreshToken":"<REFRESH>"}'

Logout:

curl -s -X POST http://localhost:8080/auth/logout -H "Authorization: Bearer <ACCESS>" -i

Tasks

Create:

curl -s -X POST http://localhost:8080/tasks -H "Authorization: Bearer <ACCESS>" -H "Content-Type: application/json" -d '{"title":"T","description":"D","status":"OPEN"}'

List:

curl -s -H "Authorization: Bearer <ACCESS>" http://localhost:8080/tasks

Update:

curl -s -X PUT http://localhost:8080/tasks/1 -H "Authorization: Bearer <ACCESS>" -H "Content-Type: application/json" -d '{"status":"DONE"}'

Delete:

curl -s -X DELETE http://localhost:8080/tasks/1 -H "Authorization: Bearer <ACCESS>" -i

Testing
Run: mvn test

Controller test shows POST /tasks returns 201 with proper JSON after authentication.

AuthService tests cover register, login success, and invalid password paths. Ensure tests stub generateAccessToken/generateRefreshToken and assert LoginResponse fields.

Add the three JWT properties to allow the SpringBootTest context to load (resolves JwtUtils property placeholders).

Project Structure
controller, service, repository, model, DTOs, security, exception, config packages.

JwtAuthenticationFilter registered before UsernamePasswordAuthenticationFilter; CSRF disabled.

Notes
This implementation meets Must-Haves and includes the Nice-to-Have refresh token flow. If refresh is not desired, omit the /auth/refresh endpoint and revert LoginResponse to access-only.

