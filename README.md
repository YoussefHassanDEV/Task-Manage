Task-Manage API
A clean Spring Boot REST API for authentication and task management with JWT-based security, BCrypt password hashing, H2 in-memory database, DTOs, and consistent JSON error handling, including refresh-token rotation and token blacklist on logout.

Features
Registration with hashed passwords via BCrypt, persisted with JPA and validated by Jakarta Bean Validation annotations on the User entity.

Login that issues short-lived access tokens and long-lived refresh tokens, with refresh rotation on each refresh call.

Stateless authentication enforced by a custom JWT filter and SecurityFilterChain, permitting /auth/** and /h2-console/** while protecting all other endpoints.

Global error responses via @RestControllerAdvice returning a structured ErrorResponse for common cases and validation failures.

H2 in-memory database for development with console access allowed by security configuration.

Tech stack
Spring Boot, Spring Web, Spring Security, Spring Data JPA for API, security, and persistence.

H2 Database for in-memory persistence during development and tests.

JJWT for token creation, parsing, and verification of access and refresh tokens.

Getting started
Prerequisites: Java 21+ and Maven.

Run: mvn spring-boot:run (server defaults to port 8080).

H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:todo, user: sa, password: blank).

Configuration
Update src/main/resources/application.properties with the following properties to align JWT and H2 settings with the code.

spring.application.name=Task-Manage

server.port=8080

spring.datasource.url=jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1;MODE=PostgreSQL

spring.datasource.username=sa

spring.datasource.password=

spring.datasource.driverClassName=org.h2.Driver

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true

spring.h2.console.path=/h2-console

app.jwt.secret=change-this-to-a-long-random-secret-string-at-least-32-bytes

app.jwt.accessExpirationMillis=900000

app.jwt.refreshExpirationMillis=604800000

These application properties drive JwtUtils construction, enabling distinct expirations for access and refresh tokens used by the API.

API
Base URL: http://localhost:8080.

Auth
POST /auth/register

Body: {"email":"user@example.com","password":"mypassword","name":"John Doe"}

Returns: 201 Created (no body)

POST /auth/login

Body: {"email":"user@example.com","password":"mypassword"}

200 OK: {"accessToken":"...","expiresInMillis":900000,"refreshToken":"...","refreshExpiresInMillis":604800000}

POST /auth/refresh

Body: {"refreshToken":"<REFRESH>"}

200 OK: same structure as login with rotated tokens

POST /auth/logout

Header: Authorization: Bearer <ACCESS>

200 OK; access token is blacklisted until expiration using an in-memory blacklist with expiry pruning.

Tasks (require Authorization: Bearer <ACCESS>)
POST /tasks

Body: {"title":"T","description":"D","status":"OPEN"}

201 Created: TaskResponse

GET /tasks

200 OK: list of tasks owned by the authenticated user

PUT /tasks/{id}

Body: {"status":"DONE"}

200 OK: updated TaskResponse

DELETE /tasks/{id}

204 No Content

Error responses
A global @RestControllerAdvice returns a structured ErrorResponse with fields timestamp, status, error, message, path, and validationErrors where applicable.

400 Bad Request: validation errors or invalid credentials via BadCredentialsException.

401 Unauthorized: missing/invalid access token via the authentication entry point.

403 Forbidden: attempting to access another user’s task triggers AccessDeniedException.

404 Not Found: task not found when the requested id does not exist for the owner.

Security
Security is stateless with SessionCreationPolicy.STATELESS, CSRF disabled, and a custom OncePerRequestFilter that parses Bearer tokens, sets Authentication with the user’s email subject, and skips filtering for /auth/** and /h2-console/**.
Passwords are hashed with BCrypt via a PasswordEncoder bean, and JWTs are generated and validated with JJWT using a symmetric HMAC key from app.jwt.secret properties.
Refresh tokens include a typ=refresh claim and are rotated on /auth/refresh, while logout blacklists the presented access token until its expiration time to prevent reuse.

Data model
User: id, email (unique, not null), passwordHash (not null), name (not null) with Bean Validation constraints and JPA mappings.

Task: id, title (not blank), description, status (OPEN/DONE), owner (ManyToOne required) persisted in table tasks.

Example requests
Register:
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"mypassword","name":"John Doe"}'

Login:
curl -s -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"mypassword"}'

Refresh:
curl -s -X POST http://localhost:8080/auth/refresh -H "Content-Type: application/json" -d '{"refreshToken":"<REFRESH>"}'

Logout:
curl -s -X POST http://localhost:8080/auth/logout -H "Authorization: Bearer <ACCESS>" -i

Create task:
curl -s -X POST http://localhost:8080/tasks -H "Authorization: Bearer <ACCESS>" -H "Content-Type: application/json" -d '{"title":"T","description":"D","status":"OPEN"}'

List tasks:
curl -s -H "Authorization: Bearer <ACCESS>" http://localhost:8080/tasks

Update task:
curl -s -X PUT http://localhost:8080/tasks/1 -H "Authorization: Bearer <ACCESS>" -H "Content-Type: application/json" -d '{"status":"DONE"}'

Delete task:
curl -s -X DELETE http://localhost:8080/tasks/1 -H "Authorization: Bearer <ACCESS>" -i

Project structure and wiring
Controllers, services, repositories, models, DTOs, security, exception, and config packages are organized under com.example.Task.Manage, with the JWT filter registered before UsernamePasswordAuthenticationFilter and CSRF disabled for stateless APIs.
SecurityConfig configures permitted paths, stateless session policy, custom entry point and access denied handler, and installs the JwtAuthenticationFilter with JwtUtils and a TokenBlacklistService.

