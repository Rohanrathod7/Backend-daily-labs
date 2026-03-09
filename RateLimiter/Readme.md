# 🚀 Day 0: The Foundation & Setup

> **Project Goal:** Establish a robust, thread-safe Spring Boot foundation to build a custom API Rate Limiter from scratch.
> **Constraint:** Strict "No-AI" coding policy. All implementations rely on official documentation, raw problem-solving, and manual debugging.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

---

## ⏱️ Execution Timeline (3 Hours)

This day was structured into four strict time blocks to simulate a fast-paced development sprint:

| Phase | Timeblock | Focus Area | Core Technologies |
| :--- | :--- | :--- | :--- |
| **Phase 1** | 0 - 45 mins | The Bare Server | Spring Initializr, `@RestController`, `@GetMapping` |
| **Phase 2** | 45 - 105 mins | The Middleman (Bouncer) | `OncePerRequestFilter`, `FilterChain`, `@Component` |
| **Phase 3** | 105 - 150 mins | Thread-Safe Memory | `ConcurrentHashMap`, `System.currentTimeMillis()` |
| **Phase 4** | 150 - 180 mins | API Testing & Version Control | Postman HTTP Client, Git/GitHub |

---

## 🛠️ Step-by-Step Implementation

### 1. The Bare Server
* Bootstrapped the project using **Spring Initializr** (Maven, Java, Spring Web dependency).
* Created a basic API endpoint using `@RestController`.
* Mapped the endpoint `http://localhost:8080/api/test` to return a successful `200 OK` text response.

### 2. The Middleman (Request Interception)
* Implemented a custom filter extending `OncePerRequestFilter` to intercept raw HTTP requests before they reach the controller.
* Registered the filter in the Spring application context using `@Component`.
* Successfully intercepted incoming requests, logged them to the terminal, and passed them down the `FilterChain`.

### 3. Thread-Safe Memory Simulation
* Created a standalone Java class (`TimeTest.java`) independent of the Spring context to test concurrency logic.
* Utilized `ConcurrentHashMap` to ensure thread safety (crucial for handling simultaneous API requests in Spring's multi-threaded environment).
* Implemented timestamp mathematics using `System.currentTimeMillis()` and `Thread.sleep()` to simulate tracking and expiring time windows.

### 4. The Testing Hammer
* Configured **Postman** to execute raw `GET` requests against the local development server.
* Verified that the server returns a `200 OK` status and the custom filter logs execute in the correct order.
* Initialized a local Git repository and pushed the foundational code to GitHub.

---

## 📚 Official Resources & Documentation Used

To adhere to the "No-AI" constraint, the following documentation was utilized:
* [Spring Web MVC Documentation](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
* [Baeldung: OncePerRequestFilter Example](https://www.baeldung.com/spring-onceperrequestfilter)
* [Java 17 ConcurrentHashMap Docs](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)

---

## ⏭️ Next Steps for Day 1
Transform this foundation into a functional **Fixed Window Rate Limiter**.
* **Target Logic:** Restrict incoming requests to **5 requests per 10-second window** per IP address.
* **Failure State:** Return HTTP Status `429 Too Many Requests`.