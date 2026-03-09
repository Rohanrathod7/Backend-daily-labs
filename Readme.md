# ⚡ 7-Day Hardcore Backend Engineering Challenge

> A strict, time-boxed sprint to build core, real-world backend infrastructure concepts from scratch.
> Designed to bridge the gap between basic API development and scalable system design.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

---

## 📜 The Rules of Engagement

This repository was built under a strict set of constraints to force deep, foundational learning rather than copy-pasting:
1. **The Time Limit:** Each project must be scoped, built, broken, fixed, and refactored within a **2-3 hour daily window**.
2. **The "No-AI" Protocol:** Zero AI-generated code was used to build the core logic. All debugging and implementation relied entirely on official documentation, source code reading, and Stack Overflow.
3. **No Fluff:** No frontend. No CSS. Just pure backend systems, APIs, concurrency, and architecture.

---

## 🗺️ The 7-Day Architecture Roadmap

This table tracks the daily progression of concepts and the systems built to understand them.

| Day | Project Name | Core Concept Mastered | Status |
| :---: | :--- | :--- | :---: |
| **0 & 1** | [The Gatekeeper](./day-01-rate-limiter) | API Rate Limiting, Thread-Safe Memory, HTTP Filters | 🟢 Completed |
| **2** | The Post Office | Distributed Message Queues, Asynchronous Processing | ⏳ Pending |
| **3** | The Live Wire | Real-Time WebSockets, Persistent TCP Connections | ⏳ Pending |
| **4** | The Vault | JWT Authentication, Cryptography, Stateless Security | ⏳ Pending |
| **5** | The Accelerator | The Cache-Aside Pattern, Redis Integration | ⏳ Pending |
| **6** | The Spider | Concurrent Web Crawling, Thread Pools, I/O Optimization | ⏳ Pending |
| **7** | The Assembly Line | Containerization (Docker) & Automated CI/CD Pipelines | ⏳ Pending |

---

## ⚙️ Tech Stack & Tooling

* **Language:** Java 17+
* **Framework:** Spring Boot (Spring Web, Spring Security)
* **Testing:** Postman / cURL
* **Infrastructure:** Docker, Redis (Introduced in later days)

---

## 🚀 How to Run Locally

Each day's project is completely isolated. To run a specific day:
1. Navigate into the specific day's directory: `cd day-XX-project-name`
2. Ensure you have Maven installed.
3. Run the Spring Boot application: `./mvnw spring-boot:run`
4. Check the individual folder's `README.md` for specific testing instructions and API endpoints.