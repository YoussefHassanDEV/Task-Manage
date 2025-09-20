
# 📌 Task-Manage API

A clean **Spring Boot REST API** for **user authentication** and **task management**, designed with best practices for **security, architecture, and maintainability**.

---

## ✨ Features

* 🔑 **Authentication & Authorization**

  * Register new users with **BCrypt password hashing**
  * Login with **JWT tokens** (access + refresh)
  * Refresh token rotation
  * Logout with **token blacklist**

* 🗂 **Task Management**

  * Create, list, update, and delete tasks
  * Each task belongs to its authenticated owner
  * Secure access control: only the owner can manage their tasks

* ⚡ **Security**

  * Stateless authentication with JWT
  * Custom filter for token validation
  * Structured error handling with `@RestControllerAdvice`

* 🛠 **Developer-Friendly**

  * **H2 in-memory database** for easy development
  * H2 Console available at `/h2-console`
  * DTO separation, global exception handling, and clean project structure

---

## 🛠 Tech Stack

* **Spring Boot 3.3+**
* **Spring Security** with JWT
* **Spring Data JPA** + **H2 Database**
* **Jakarta Bean Validation**
* **Lombok** (for boilerplate reduction)
* **JUnit + Spring Security Test** (for testing)

---

## 📥 Installation

### 1️⃣ Clone the repository

```bash
git clone https://github.com/YoussefHassanDEV/Task-Manage.git
cd Task-Manage
```

### 2️⃣ Build the project

```bash
mvn clean install
```

### 3️⃣ Configure application

Edit `src/main/resources/application.properties`:

```properties
# App JWT secret
app.jwt.secret=change-this-to-a-long-random-secret-string-at-least-32-bytes
```

*(Change it to a long, random string for security.)*

### 4️⃣ Run the app

```bash
mvn spring-boot:run
```

* API: [http://localhost:8080](http://localhost:8080)
* H2 Console: [http://localhost:8080/h2-console](http://localhost:8080)

  * JDBC URL: `jdbc:h2:mem:todo`
  * User: `sa`
  * Password: *(blank)*

---

## ⚙️ Configuration

`src/main/resources/application.properties`

```properties
spring.application.name=Task-Manage
server.port=8080

# H2
spring.datasource.url=jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driverClassName=org.h2.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT
app.jwt.secret=change-this-to-a-long-random-secret-string-at-least-32-bytes
app.jwt.accessExpirationMillis=900000      # 15 minutes
app.jwt.refreshExpirationMillis=604800000  # 7 days
```

---

## 📡 API Endpoints

### 🔐 Auth

| Method | Endpoint         | Description         |
| ------ | ---------------- | ------------------- |
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login`    | Login, get tokens   |
| `POST` | `/auth/refresh`  | Refresh tokens      |
| `POST` | `/auth/logout`   | Logout + blacklist  |

#### Example: Register

```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"mypassword","name":"John Doe"}'
```

#### Example: Login

```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"mypassword"}'
```

---

### 📋 Tasks (Require `Authorization: Bearer <ACCESS>`)

| Method   | Endpoint      | Description        |
| -------- | ------------- | ------------------ |
| `POST`   | `/tasks`      | Create a new task  |
| `GET`    | `/tasks`      | List user’s tasks  |
| `PUT`    | `/tasks/{id}` | Update task status |
| `DELETE` | `/tasks/{id}` | Delete a task      |

#### Example: Create Task

```bash
curl -X POST http://localhost:8080/tasks \
-H "Authorization: Bearer <ACCESS>" \
-H "Content-Type: application/json" \
-d '{"title":"My Task","description":"Details","status":"OPEN"}'
```

---

## ⚠️ Error Handling

Errors return structured JSON:

```json
{
  "timestamp": "2025-09-20T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/auth/register",
  "validationErrors": ["email: must be a valid email"]
}
```

### Common Status Codes

* `400` → Validation errors, bad credentials
* `401` → Unauthorized (invalid/missing token)
* `403` → Forbidden (accessing another user’s task)
* `404` → Task not found

---

## 🔐 Security Highlights

* **Stateless**: `SessionCreationPolicy.STATELESS`
* **BCrypt password hashing**
* **JWT access & refresh tokens**
* **Blacklist service** invalidates tokens on logout
* **Custom filters** for authentication & error handling

---

## 🗂 Data Model

### 👤 User

* `id`
* `email` *(unique, required)*
* `passwordHash` *(required)*
* `name` *(required)*

### ✅ Task

* `id`
* `title` *(not blank)*
* `description`
* `status` *(OPEN / DONE)*
* `owner` *(ManyToOne → User)*

---

## 📂 Project Structure

```
com.example.Task.Manage
 ├── config/          # Security configuration
 ├── controller/      # REST controllers
 ├── dto/             # DTOs
 ├── exception/       # Global exception handling
 ├── model/           # Entities (User, Task)
 ├── repository/      # Spring Data JPA repositories
 ├── security/        # JWT, filters, blacklist
 └── service/         # Business logic
```

---

## ✅ Example Flow

1. **Register** a new user
2. **Login** → get `accessToken` & `refreshToken`
3. Use `accessToken` for `/tasks`
4. **Refresh** tokens using `/auth/refresh`
5. **Logout** → access token blacklisted

---

## 🧪 Testing

Run tests with:

```bash
mvn test
```

Includes:

* Unit tests for authentication & task APIs
* Security tests with `spring-security-test`

---

## 📊 Evaluation Criteria (Assignment Goals)

* RESTful API with correct HTTP status codes
* Clean Spring Boot layers: Controller → Service → Repository
* Authentication & Security with JWT and BCrypt
* Global Exception Handling with `@RestControllerAdvice`
* In-memory H2 DB, no external setup required
* DTO separation from entities
* Example requests provided with `curl`

---

💡 *Built with ❤️ using Spring Boot*

