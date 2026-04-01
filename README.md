# 💬 Messenger Backend API (Spring Boot)

A scalable and production-ready backend system for a real-time chat application (Messenger-like), built with modern backend engineering practices.

---

## 🚀 Overview

This project is a **RESTful + WebSocket-based backend API** that powers a real-time messaging platform.
It follows clean architecture principles and includes advanced features such as authentication, real-time communication, media handling, and AI-assisted chat formatting.

---

## 🧱 Tech Stack

* **Backend Framework:** Spring Boot
* **Database:** PostgreSQL
* **ORM:** Spring Data JPA (Hibernate)
* **Security:** Spring Security + JWT (JSON Web Token)
* **Real-time Communication:** WebSocket (STOMP protocol)
* **Email Service:** SMTP (OTP, verification, password reset)
* **Cloud Storage:** Cloudinary (user avatar upload & management)
* **AI Integration:** LLM API (for chat formatting assistance)

---

## 🏗️ Architecture & Design

The project follows a **layered architecture** for maintainability and scalability:

```text
controller → service → repository → database
            ↓
           dto
```

### Key Principles

* Separation of concerns via layered structure
* DTO pattern for data transfer and API abstraction
* Centralized error handling
* Clean and modular code organization

---

## ✨ Features

### 🔐 Authentication & Authorization

* Spring Security configuration with JWT
* Stateless authentication (no session)
* Role-based access control:

  * `USER`
  * `ADMIN`

---

### 💬 Real-time Chat

* WebSocket + STOMP for bidirectional communication
* Instant message delivery
* Supports chat rooms / private messaging

---

### 🤖 AI-assisted Chat (LLM Integration)

* Integrates with an LLM API to:

  * Format user messages
  * Improve clarity and readability
* Enhances chat experience

---

### 📧 Email Service

* Account registration with email verification
* OTP-based authentication
* Password reset via email

---

### ☁️ Media Storage (Cloudinary)

* Upload and store user avatars
* Secure URL-based media access
* Offloads file storage from backend server

---

### 🛡️ Security & Validation

* JWT token validation filter
* Secure password hashing (e.g., BCrypt)
* Input validation using DTOs

---

### ⚠️ Global Exception Handling

* Centralized exception handler (`@ControllerAdvice`)
* Consistent API error responses
* Cleaner controller logic

---

## 📂 Project Structure

```text
src/main/java/com/yourapp
│
├── controller        # REST & WebSocket endpoints
├── service           # Business logic
├── repository        # Data access layer (Spring Data JPA)
├── entity            # Database entities
├── dto               # Data Transfer Objects
├── config            # Security, WebSocket, Cloudinary configs
├── exception         # Global exception handling
├── security          # JWT, authentication filters
├── websocket
└── llm
```

---

## ⚙️ Setup & Run

### 1. Clone the repository

```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
```

---

### 2. Configure environment

Copy the example config:

```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```

Update the following:

* Database credentials (PostgreSQL)
* JWT secret key
* Email configuration
* Cloudinary credentials
* LLM API key

---

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Or run directly from your IDE.

---

## 🔌 API & WebSocket

### REST API

* Authentication (login, register, refresh token)
* User management
* Messaging APIs
* File upload (avatar)

### WebSocket Endpoint

* STOMP endpoint for real-time messaging
* Subscribe/send channels for chat rooms

---

## 🔒 Environment Variables (Recommended)

Avoid hardcoding sensitive data:

```properties
spring.datasource.password=${DB_PASSWORD}
spring.mail.password=${MAIL_PASSWORD}
groq.api.key=${GROQ_API_KEY}
cloudinary.api.secret=${CLOUDINARY_SECRET}
```

---

## 🧪 Future Improvements

* Redis caching for sessions & messages
* Kafka for event-driven messaging
* Message indexing & search
* Rate limiting & abuse protection
* Microservices architecture (user, chat, notification)
* Docker & CI/CD pipeline

---

## 📌 Notes

* Do **not** commit sensitive files:

  * `application.properties`
  * `.env`
  * `target/`

* Always provide:

  * `application-example.properties`

---

## 👨‍💻 Author

Developed as a backend engineering project focusing on:

* System design
* Clean architecture
* Real-time systems
* Secure API development

---

## ⭐ Final Thoughts

This project demonstrates a **production-ready backend system** for a real-time chat application, combining:

* Spring Security + JWT authentication
* Spring Data JPA for persistence
* WebSocket for real-time communication
* Cloudinary for media storage
* AI-powered chat enhancements

---

Feel free to fork, contribute, or use it as a base for your own systems 🚀
