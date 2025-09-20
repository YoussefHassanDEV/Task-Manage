
# Task-Manage API

A clean Spring Boot REST API for authentication and task management with:
- 🔑 JWT-based security
- 🔒 BCrypt password hashing
- 🗄 H2 in-memory database
- 📦 DTOs & structured JSON error handling
- 🔄 Refresh-token rotation
- 🚫 Token blacklist on logout

## Features

- User Registration  
  Passwords hashed via BCrypt, persisted with JPA, validated using Jakarta Bean Validation.

- Authentication  
  Login issues:
  - Short-lived access token
  - Long-lived refresh token  
  Refresh endpoint rotates refresh tokens.

- Stateless Security  
  - Custom JWT filter  
  - SecurityFilterChain allows /auth/** and /h2-console/**  
  - Protects all other endpoints

- Global Error Handling  
  Unified ErrorResponse for:
  - Validation errors  
  - Unauthorized & forbidden access  
  - Resource not found

- H2 Database for development  
  Console enabled at /h2-console.

## Tech Stack

- Spring Boot – Web, Security, JPA  
- H2 Database – in-memory persistence  
- JJWT – token creation & validation  
- Maven – build & dependency management  
- Java 17+

## Quickstart

1) Clone
```bash
git clone https://github.com/YoussefHassanDEV/Task-Manage.git
cd Task-Manage
```

2) Build
```bash
mvn clean install
```

3) Configure properties  
Edit src/main/resources/application.properties as needed:
- app.jwt.secret → a long, random string (32+ characters)
- app.jwt.accessExpirationMillis → access token lifetime in ms
- app.jwt.refreshExpirationMillis → refresh token lifetime in ms

4) Run
```bash
mvn spring-boot:run
```
Service: http://localhost:8080

5) H2 Console (dev only)  
- URL: http://localhost:8080/h2-console  
- JDBC URL: jdbc:h2:mem:todo  
- User: sa  
- Password: (blank)

## Configuration

src/main/resources/application.properties
```properties
spring.application.name=Task-Manage
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driverClassName=org.h2.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT
app.jwt.secret=change-this-to-a-long-random-secret-string-at-least-32-bytes
app.jwt.accessExpirationMillis=900000        # 15 minutes
app.jwt.refreshExpirationMillis=604800000    # 7 days

# JSON date output as ISO-8601
spring.jackson.serialization.write-dates-as-timestamps=false
```

Add Java 8 date/time support (already included if using the managed BOM):
```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

## API Endpoints

All JSON requests and responses use Content-Type: application/json.

### Auth

| Method | Endpoint         | Description              |
|-------|-------------------|--------------------------|
| POST  | /auth/register    | Register new user        |
| POST  | /auth/login       | Login & get tokens       |
| POST  | /auth/refresh     | Rotate refresh token     |
| POST  | /auth/logout      | Logout & blacklist token |

Register (201 Created, no body)
```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"mypassword","name":"John Doe"}'
```

Login (200 OK)
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"mypassword"}'
```
Response
```json
{
  "accessToken": "<ACCESS>",
  "expiresInMillis": 900000,
  "refreshToken": "<REFRESH>",
  "refreshExpiresInMillis": 604800000
}
```

Refresh (200 OK) — rotates refresh tokens
```bash
curl -X POST http://localhost:8080/auth/refresh \
-H "Content-Type: application/json" \
-d '{"refreshToken":"<REFRESH>"}'
```

Logout (200 OK, no body) — blacklists current access token until expiry
```bash
curl -X POST http://localhost:8080/auth/logout \
-H "Authorization: Bearer <ACCESS>"
```

### Tasks (requires Authorization: Bearer <ACCESS>)

| Method | Endpoint      | Description |
|--------|---------------|-------------|
| POST   | /tasks        | Create task |
| GET    | /tasks        | List tasks  |
| PUT    | /tasks/{id}   | Update task |
| DELETE | /tasks/{id}   | Delete task |

Create (201 Created)
```bash
curl -X POST http://localhost:8080/tasks \
-H "Authorization: Bearer <ACCESS>" \
-H "Content-Type: application/json" \
-d '{"title":"My Task","description":"Details","status":"OPEN"}'
```
Response
```json
{
  "id": 1,
  "title": "My Task",
  "description": "Details",
  "status": "OPEN"
}
```

List (200 OK)
```bash
curl -X GET http://localhost:8080/tasks \
-H "Authorization: Bearer <ACCESS>"
```

Update status (200 OK)
```bash
curl -X PUT http://localhost:8080/tasks/1 \
-H "Authorization: Bearer <ACCESS>" \
-H "Content-Type: application/json" \
-d '{"status":"DONE"}'
```

Delete (204 No Content)
```bash
curl -X DELETE http://localhost:8080/tasks/1 \
-H "Authorization: Bearer <ACCESS>"
```

## Error Handling

All errors return a structured JSON body like:
```json
{
  "timestamp": "2025-09-20T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/auth/register",
  "validationErrors": [
    "email: must be a well-formed email address",
    "password: must not be blank"
  ]
}
```

Common HTTP statuses:
- 400 — Validation errors, bad credentials, bad refresh token  
- 401 — Unauthorized (missing/invalid token)  
- 403 — Forbidden (accessing another user’s task)  
- 404 — Not Found (task not found)

Note: validationErrors is a list of strings in this implementation.

## Security

- Stateless: SessionCreationPolicy.STATELESS  
- CSRF disabled for API-only backend  
- Custom JWT filter reads Authorization: Bearer <TOKEN>  
- Access tokens are blacklisted on logout until expiration  
- Refresh tokens are rotated at /auth/refresh

## Data Model

User
- id
- email (unique, required)
- passwordHash (required)
- name (required)

Task
- id
- title (not blank)
- description
- status (OPEN/DONE)
- owner (ManyToOne → User)

## Project Structure

```
com.example.Task.Manage
 ├── config/          # Security configuration
 ├── controller/      # REST controllers
 ├── DTOs/            # Request/Response DTOs
 ├── exception/       # Global exception handling
 ├── model/           # JPA entities
 ├── repository/      # Spring Data JPA repositories
 ├── security/        # JWT utils, filter, blacklist
 └── service/         # Business logic
```

## Example Flow

1) Register user → 201  
2) Login → receive accessToken + refreshToken  
3) Use accessToken → call /tasks  
4) Refresh with refreshToken → get new tokens  
5) Logout → blacklist access token

## Testing

Run tests:
```bash
mvn test
```

Suggested test coverage (add/keep in src/test):
- AuthController login happy-path and invalid password  
- AuthController refresh with blank token returns 400  
- TaskController POST /tasks returns 201 with valid Authorization  
- Ownership checks: updating/deleting another user’s task returns 403  

## Troubleshooting

- Instant serialization error in errors  
  Ensure the Java 8 time module is on the classpath:
  ```xml
  <dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
  </dependency>
  ```
  And set:
  ```properties
  spring.jackson.serialization.write-dates-as-timestamps=false
  ```
  Security error writers use the app’s ObjectMapper, so Instant serializes correctly in 401/403 JSON.

- Parameter binding on /tasks/{id}  
  Controller uses @PathVariable("id") to avoid binding issues in environments that do not retain parameter names.
