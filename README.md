# 📌 Task-Manage API

A clean **Spring Boot REST API** for authentication and task management with:

* 🔑 **JWT-based security**
* 🔒 **BCrypt password hashing**
* 🗄 **H2 in-memory database**
* 📦 **DTOs & structured JSON error handling**
* 🔄 **Refresh-token rotation**
* 🚫 **Token blacklist on logout**

---

## ✨ Features

* ✅ **User Registration**
  Passwords hashed via **BCrypt**, persisted with **JPA**, validated using **Jakarta Bean Validation**.

* ✅ **Authentication**
  Login issues:

  * Short-lived **Access Token**
  * Long-lived **Refresh Token**
    with **refresh rotation**.

* ✅ **Stateless Security**

  * Custom JWT filter
  * `SecurityFilterChain` allows `/auth/**` and `/h2-console/**`
  * Protects all other endpoints

* ✅ **Global Error Handling**
  Unified `ErrorResponse` for:

  * Validation errors
  * Unauthorized & forbidden access
  * Resource not found

* ✅ **H2 Database** for development
  Console enabled at `/h2-console`.

---

## 🛠 Tech Stack

* **Spring Boot** – Web, Security, JPA
* **H2 Database** – in-memory persistence
* **JJWT** – token creation & validation
* **Maven** – build & dependency management
* **Java 21+**

---

## 📥 Installation

Follow these steps to set up the project locally:

### 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/task-manage-api.git
cd task-manage-api
```

### 2️⃣ Install dependencies

Make sure you have **Maven** installed, then run:

```bash
mvn clean install
```

### 3️⃣ Configure application

Update `src/main/resources/application.properties` as needed:

* Set `app.jwt.secret` → a long, random string (32+ characters).
* Adjust database or server configs if needed.

### 4️⃣ Run the app

```bash
mvn spring-boot:run
```

The API will be available at:
👉 [http://localhost:8080](http://localhost:8080)

### 5️⃣ Access H2 console (for development)

👉 [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

* JDBC URL: `jdbc:h2:mem:todo`
* User: `sa`
* Password: *(blank)*

---

## ⚙️ Configuration

`src/main/resources/application.properties`

```properties
spring.application.name=Task-Manage
server.port=8080

# H2 Database
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
app.jwt.accessExpirationMillis=900000        # 15 minutes
app.jwt.refreshExpirationMillis=604800000    # 7 days
```

---

## 📡 API Endpoints

### 🔐 Auth

| Method | Endpoint         | Description              |
| ------ | ---------------- | ------------------------ |
| `POST` | `/auth/register` | Register new user        |
| `POST` | `/auth/login`    | Login & get tokens       |
| `POST` | `/auth/refresh`  | Rotate refresh token     |
| `POST` | `/auth/logout`   | Logout & blacklist token |

**Example: Register**

```bash
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"mypassword","name":"John Doe"}'
```

---

### 📋 Tasks (requires `Authorization: Bearer <ACCESS>`)

| Method   | Endpoint      | Description |
| -------- | ------------- | ----------- |
| `POST`   | `/tasks`      | Create task |
| `GET`    | `/tasks`      | List tasks  |
| `PUT`    | `/tasks/{id}` | Update task |
| `DELETE` | `/tasks/{id}` | Delete task |

**Example: Create Task**

```bash
curl -X POST http://localhost:8080/tasks \
-H "Authorization: Bearer <ACCESS>" \
-H "Content-Type: application/json" \
-d '{"title":"T","description":"D","status":"OPEN"}'
```

---

## ⚠️ Error Handling

All errors return a **structured JSON**:

```json
{
  "timestamp": "2025-09-20T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/auth/register",
  "validationErrors": {
    "email": "must be a valid email"
  }
}
```

**Common Errors:**

* `400` – Validation errors, bad credentials
* `401` – Unauthorized (invalid/missing token)
* `403` – Forbidden (accessing another user’s task)
* `404` – Not Found (task doesn’t exist)

---

## 🔐 Security

* **Stateless auth**: `SessionCreationPolicy.STATELESS`
* **CSRF disabled**
* **JWT filter** parses `Authorization: Bearer <TOKEN>`
* **Password hashing** with BCrypt
* **Access tokens blacklisted on logout**
* **Refresh tokens rotated** on every refresh

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
 ├── config/          # Security config
 ├── controller/      # REST controllers
 ├── dto/             # DTOs
 ├── exception/       # Global exception handling
 ├── model/           # Entities
 ├── repository/      # Spring Data JPA repos
 ├── security/        # JWT utils, filters, blacklist
 └── service/         # Business logic
```

---

## ✅ Example Flow

1. **Register** user → 201
2. **Login** → Get `accessToken` & `refreshToken`
3. Use `accessToken` → Access `/tasks`
4. **Refresh** with `refreshToken` → Get new tokens
5. **Logout** → Token blacklisted

---

💡 *Built with ❤️ using Spring Boot*

---
