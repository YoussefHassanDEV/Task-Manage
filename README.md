

# 📌 Task-Manage API

A clean **Spring Boot REST API** for **user authentication** and **task management**, designed with best practices for **security, architecture, and maintainability**.

---

## 📑 Table of Contents

* [✨ Features](#-features)
* [🛠 Tech Stack](#-tech-stack)
* [📥 Installation](#-installation)
* [⚙️ Configuration](#️-configuration)
* [📡 API Endpoints](#-api-endpoints)

  * [🔐 Auth](#-auth)
  * [📋 Tasks](#-tasks-require-authorization-bearer-access)
* [⚠️ Error Handling](#️-error-handling)
* [🔐 Security Highlights](#-security-highlights)
* [🗂 Data Model](#-data-model)
* [📂 Project Structure](#-project-structure)
* [✅ Example Flow](#-example-flow)
* [🧪 Testing](#-testing)
* [📊 Evaluation Criteria (Assignment Goals)](#-evaluation-criteria-assignment-goals)

---

## ✨ Features

* 🔑 **Authentication & Authorization** (JWT, refresh, logout with blacklist)
* 🗂 **Task Management** (CRUD, owner-only access)
* ⚡ **Security** (custom JWT filter, global error handling)
* 🛠 **Developer-Friendly** (H2 DB, console at `/h2-console`)

---

## 🛠 Tech Stack

* **Spring Boot 3.3+**
* **Spring Security** with JWT
* **Spring Data JPA** + **H2 Database**
* **Jakarta Bean Validation**
* **Lombok**
* **JUnit + Spring Security Test**

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
app.jwt.secret=change-this-to-a-long-random-secret-string-at-least-32-bytes
```

### 4️⃣ Run the app

```bash
mvn spring-boot:run
```

* API → [http://localhost:8080](http://localhost:8080)
* H2 Console → [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

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

| Method | Endpoint         | Description        |
| ------ | ---------------- | ------------------ |
| `POST` | `/auth/register` | Register new user  |
| `POST` | `/auth/login`    | Login, get tokens  |
| `POST` | `/auth/refresh`  | Refresh tokens     |
| `POST` | `/auth/logout`   | Logout & blacklist |

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
-d '{"title":"My Task","description":"Details","status":"INPROGRESS"}'
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

* Stateless JWT authentication
* BCrypt password hashing
* Refresh token rotation
* Logout with blacklist service
* Custom `AuthenticationEntryPoint` & `AccessDeniedHandler`

---

## 🗂 Data Model

### 👤 User

* `id`
* `email` *(unique)*
* `passwordHash`
* `name`

### ✅ Task

* `id`
* `title` *(required)*
* `description`
* `status` *(INPROGRESS / DONE)*
* `owner` *(ManyToOne → User)*

---

## 📂 Project Structure

```
com.example.Task.Manage
 ├── config/          # Security config
 ├── controller/      # REST controllers
 ├── dto/             # DTOs
 ├── exception/       # Global exception handling
 ├── model/           # Entities
 ├── repository/      # JPA repositories
 ├── security/        # JWT, filters, blacklist
 └── service/         # Business logic
```

---

## ✅ Example Flow

1. **Register** → create user
2. **Login** → get `accessToken` + `refreshToken`
3. Use `accessToken` → access `/tasks`
4. **Refresh** → rotate tokens
5. **Logout** → blacklist token

---

## 🧪 Testing

Run:

```bash
mvn test
```

Includes:

* Unit tests for auth & task endpoints
* Security tests with `spring-security-test`

---

## 📊 Evaluation Criteria (Assignment Goals)

* ✅ RESTful endpoints with proper status codes
* ✅ Controller → Service → Repository architecture
* ✅ Authentication & Security with JWT + BCrypt
* ✅ Global error handling with `@RestControllerAdvice`
* ✅ DTOs & Entities separated
* ✅ In-memory H2 DB (no external setup)
* ✅ Example curl requests included
* ✅ Ready for GitHub evaluation

---

💡 *Built with ❤️ using Spring Boot*

---

