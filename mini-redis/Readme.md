# ⚡ Day 3: The Mini-Redis (In-Memory Cache with TTL)

> **Core Concept:** Building a custom in-memory Key-Value store with Time-To-Live (TTL) eviction.
> **Constraint:** Strict "No-AI" coding policy. Built entirely from scratch using raw Java and Spring Boot.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)

---

## ❓ The What and The Why

* **What is it?** A thread-safe, in-memory caching system that stores data for a specific amount of time before automatically deleting it.
* **Why build it?** Fetching data from a database is slow. Storing frequently accessed data in RAM (Cache) makes APIs lightning fast. However, storing everything in RAM forever causes Out-Of-Memory (OOM) crashes.
* **The Solution:** We implement a **TTL (Time-To-Live)**. When we save data, we attach an expiration timestamp. A background worker constantly sweeps the memory and deletes expired data, mimicking the core behavior of enterprise tools like **Redis** or **Memcached**.

---

## The Core Concept: Redis



### What is Redis?

**Redis** (Remote Dictionary Server) is an open-source, **in-memory NoSQL data store**.

Unlike traditional databases (like PostgreSQL or MySQL) that save data on a physical hard drive (Disk), Redis stores everything directly in the server's **RAM (Memory)**. It primarily acts as a **Key-Value** store, but unlike basic caches, it natively supports complex data structures like Lists, Sets, Hashes, and Sorted Sets.

---

### Why is it Used? (The Problems it Solves)

**1. Blistering Speed (Sub-millisecond Latency)**
Reading from RAM is exponentially faster than reading from a physical disk. Because Redis bypasses the hard drive entirely, it can handle millions of requests per second with practically zero delay.

**2. Database Offloading (Caching)**
If your application goes viral and thousands of users request the exact same data (e.g., the homepage product catalog), querying the main SQL database every single time will overwhelm and crash it.
* **The Solution:** The backend queries the database *once*, saves the result in Redis, and serves the next 9,999 users directly from the lightning-fast Redis cache.

**3. Distributed Shared State**
If you have a scalable architecture with 5 different Spring Boot servers running your backend, in-memory Java tools like `ConcurrentHashMap` fail because Server A doesn't know what Server B is storing.
* **The Solution:** All 5 servers connect to a single, central Redis instance. This makes Redis the industry standard for **Session Management** (keeping a user logged in regardless of which server they hit) and **Distributed Rate Limiting**.

**4. Real-Time Leaderboards**
Running a `SELECT * ORDER BY points DESC` query on a SQL database with 10 million rows is incredibly slow.
* **The Solution:** Redis has a native `Sorted Set` data structure. It automatically keeps data sorted in memory the exact millisecond it is inserted, making real-time gaming or platform leaderboards effortless to pull.

### The Inner Workings of Redis



**1. The Single-Threaded Event Loop**
You might assume a system capable of handling millions of requests per second relies on hundreds of threads. Surprisingly, Redis operates its core command execution on a **single thread**.
* It uses a technique called **I/O Multiplexing** to listen to thousands of incoming network connections simultaneously.
* Because all data is stored in RAM, fetching or modifying it takes nanoseconds. The CPU is almost never the bottleneck; the network speed is.
* **Why this is genius:** By using only one thread to execute commands, Redis completely eliminates "context switching" (the CPU wasting time switching between threads) and avoids the need for complex "locks." There are no race conditions because every command is executed sequentially in a lightning-fast queue.

**2. Persistence (The Safety Net)**
RAM is volatile—if the server loses power, everything in RAM is wiped. Redis prevents catastrophic data loss by silently backing up data to the physical disk in the background:
* **RDB (Redis Database Backup):** Takes a compressed snapshot of the entire memory at specific intervals (e.g., every 5 minutes).
* **AOF (Append Only File):** Logs every single write operation to a file. If the server crashes, Redis simply replays this log on reboot to perfectly reconstruct the exact state of the RAM.

---

### The Real-Life Example: The E-Commerce "Flash Sale"

Imagine an e-commerce platform launching a highly anticipated phone at exactly 12:00 PM. 100,000 users refresh the page at the exact same second to see the price and stock availability.

* **Without Redis (The Crash):** 100,000 requests hit your Spring Boot API. The API sends 100,000 exact same `SELECT * FROM products WHERE id = 999` queries to the PostgreSQL database. The database tries to read the hard drive 100,000 times simultaneously, maxes out its CPU, and crashes. The site goes down.
* **With Redis (The Savior):** You implement the **"Cache-Aside Pattern."** Redis sits between your API and your database as a high-speed, in-memory shield.



---

### System Implementation Flow Diagram (Cache-Aside Pattern)

This diagram shows the step-by-step logical sequence of how a request is handled when Redis is implemented in your backend system.

```text
User Request (GET /product/999)
      │
      ▼
┌───────────────────────┐
│   Spring Boot API     │
└───────┬───────────────┘
        │
        │ 1. API asks Redis: "Do you have product 999?"
        ▼
┌───────────────────────┐
│      Redis Cache      │ (In-Memory RAM)
└───────┬────────┬──────┘
        │        │
   2a. YES       2b. NO (Cache Miss)
 (Cache Hit)     │
        │        │ 3. API queries the main database
        │        ▼
        │  ┌───────────────────────┐
        │  │    PostgreSQL DB      │ (Hard Drive)
        │  └─────┬─────────────────┘
        │        │ 4. DB returns product data to API
        │        ▼
        │  ┌───────────────────────┐
        │  │ 5. API saves a copy   │
        │  │    in Redis with a    │
        │  │    Time-To-Live (TTL) │
        │  └─────┬─────────────────┘
        │        │
        ▼        ▼
 6. API returns the JSON response to the User
 
 ```
```text
=============================================================================
|                  REDIS CACHE-ASIDE DATA FLOW DIAGRAM                      |
=============================================================================

                              [ External Entity ]
                              +-----------------+
                              |   Client App    | (Web/Mobile)
                              +--------+--------+
                                       | ^
               1. GET /product/999     | | 7. HTTP 200 OK
               (Request payload)       | | (JSON Response payload)
                                       v |
                              +-----------------+
                              |                 |
                              | Spring Boot API | (Process)
                              |                 |
                              +--+---+---+---+--+
                                 |   ^   |   ^
            2. GET product:999   |   |   |   | 6. SETEX product:999 60 <JSON>
            (Lookup by Key)      |   |   |   | (Write serialized Java object)
                                 v   |   v   |
      +-----------------+            |       |           +-----------------+
      |                 | -----------+       +---------> |                 |
      |   Redis Cache   |                            --> |  PostgreSQL DB  |
      |  (Data Store 1) |  3. Return <JSON>          |   |  (Data Store 2) |
      |                 |     OR Return NULL         |   |                 |
      +-----------------+                            |   +-----------------+
                                                     |             |
                                                     |             |
                         4. SELECT * FROM products   |             |
                            WHERE id = 999           |             |
                           (Only if Redis is NULL)   +-------------+
                                                     5. Return ResultSet
                                                        (Relational Data)

=============================================================================
```
---
## 📂 Project Structure

```text
backend-daily-labs/
├── README.md (Master Repo Docs)
├── day-01-rate-limiter/
├── day-02-task-queue/
└── day-03-mini-redis/
    ├── pom.xml
    ├── README.md (This File)
    └── src/main/java/com/Rohan/mini_redis/
        ├── MiniRedisApplication.java
        ├── CacheEntry.java
        └── CacheService.java
```
---
## 🏗️ Phase 1: The Core Data Structure

**Objective:** Set up the Spring Boot environment and build the custom data objects required to hold values alongside their expiration timestamps.

### Step 1: The Custom Wrapper (`CacheEntry.java`)
Standard maps only link a Key to a Value. To support TTL, I created a custom `CacheEntry` object that holds:
1. `Object value` (The actual data being stored)
2. `long expiryTime` (The exact future millisecond when this data becomes invalid)

### Step 2: The Memory Vault (`CacheService.java`)
* Created a `@Service` class to act as the centralized caching engine.
* Utilized a `ConcurrentHashMap<String, CacheEntry>` to safely handle concurrent read/write operations from multiple API threads.
* Implemented the `put(key, value, ttl)` logic:
    * Calculated the absolute expiration time: `System.currentTimeMillis() + ttlInMillis`.
    * Wrapped the value and expiration time into a new `CacheEntry` and stored it in the map.

---
## 🧹 Phase 2: The Eviction Engine (Background Cleanup)

**Objective:** Implement a background worker that automatically detects and deletes expired keys from memory to prevent Out-Of-Memory (OOM) leaks.

### Step 1: Enabling Spring Scheduling
* **What:** Added `@EnableScheduling` to the main application class.
* **Why:** By default, Spring Boot does not run background cron jobs. This annotation activates Spring's internal task scheduler, allowing us to run methods asynchronously without manually managing thread loops like we did in Day 2.

### Step 2: The Sweeper Process (`@Scheduled`)
* **What:** Created a `cleanupCache()` method inside the `CacheService` and annotated it with `@Scheduled(fixedRate = 5000)`.
* **Why:** Instead of checking if a key is expired *only* when a user asks for it (Passive Expiration), we want the system to actively clean itself up every 5 seconds (Active Expiration). This mirrors how real Redis handles key eviction.

### Step 3: The Eviction Logic
* **How:** 1. The scheduled task grabs the current millisecond: `System.currentTimeMillis()`.
    2. It iterates through every key-value pair currently sitting in the `ConcurrentHashMap`.
    3. If the `expiryTime` of a `CacheEntry` is less than the current time, the system physically removes the object from the map (`cache.remove(key)`), freeing up the RAM.

---
## 🌐 Phase 3: The REST API (Producer & Consumer)

**Objective:** Expose the caching engine to the outside world using standard HTTP methods, allowing clients to store and retrieve volatile data.

### Step 1: Passive Expiration (Lazy Deletion)
* **What:** Updated the `CacheService.get(key)` method to perform a strict time-check before returning data.
* **Why:** The `@Scheduled` sweeper only runs every 5 seconds. If a key expires at second 2, and a user requests it at second 3, the sweeper hasn't deleted it yet. To prevent returning stale/expired data, the `get()` method does a "Passive Expiration" check. If the requested key is expired, it deletes it on the spot and returns `null`. This is the exact dual-eviction strategy used by real Redis.

### Step 2: The Endpoints (`CacheController.java`)
* **What:** Created a standard Spring `@RestController` mapped to `/api/cache`.
* **The POST Method:** * Accepts a `key` as a Path Variable, and a `value` and `ttl` (Time-To-Live in milliseconds) as Query Parameters.
    * Injects the data into the `CacheService`.
* **The GET Method:**
    * Accepts a `key` as a Path Variable.
    * Queries the `CacheService`.
    * Returns `200 OK` with the value if it exists, or successfully returns `404 Not Found` if the key has expired or was never saved.

---

## 🧠 System Architecture & Data Flow

This diagram illustrates how the Mini-Redis engine handles incoming requests alongside the background memory-management thread. It highlights the dual-eviction strategy: **Passive Eviction** (on GET requests) and **Active Eviction** (via the `@Scheduled` sweeper).

```mermaid
graph TD
    %% Define external client
    Client[Postman / API Client]

    %% Define internal components
    subgraph Spring Boot Application
        Controller[CacheController <br> REST API]
        Service[CacheService <br> Core Logic]
        Vault[(ConcurrentHashMap <br> Memory Vault)]
        Sweeper((@Scheduled <br> Background Thread))
    end

    %% 1. The Insertion Flow (POST)
    Client -->|1. POST /api/cache/{key}| Controller
    Controller -->|2. put(key, value, ttl)| Service
    Service -->|3. Calculate Expiry & Store| Vault

    %% 2. The Retrieval Flow (GET / Passive Eviction)
    Client -->|4. GET /api/cache/{key}| Controller
    Controller -->|5. get(key)| Service
    Service -->|6. Fetch & Check TTL| Vault
    Vault -.->|7a. Valid: Return Data| Service
    Vault -.->|7b. Expired: Delete & Return 404| Service

    %% 3. The Cleanup Flow (Active Eviction)
    Sweeper -->|A. Wakes up every 10s| Service
    Service -->|B. Scans all entries| Vault
    Vault -.->|C. Physically removes expired keys| Sweeper

    %% Styling
    classDef storage fill:#ff9,stroke:#333,stroke-width:2px;
    classDef thread fill:#f9f,stroke:#333,stroke-width:2px;
    class Vault storage;
    class Sweeper thread;
```
---

![terminal](media/img.png)

---

## 🧪 How to Test Locally

![PostMan](media/img_1.png)

To prove that both the memory vault and the background eviction engine are working, we will perform the "Ghost Test."

### 1. Start the Server
* Run the `MiniRedisApplication.java` file in your IDE.
* Ensure Tomcat starts successfully on port `8080` and the `@Scheduled` worker begins running.

### 2. Insert Data into the Cache (POST)
* Open **Postman**.
* Set the request type to **POST**.
* Enter the URL: `http://localhost:8080/api/cache/my_test_key?value=Secret_Data&time=10000`
* Click **Send**.
* **Expected Result:** You will get a `200 OK` status with the message: `"Successfully cached data for key: my_test_key"`.

### 3. Verify Data Exists (GET)
* Immediately change the request type in Postman to **GET**.
* Change the URL to: `http://localhost:8080/api/cache/my_test_key`
* Click **Send**.
* **Expected Result:** You will get a `200 OK` status returning the exact string: `"Secret_Data"`.

### 4. The Expiration Test (The Ghost Test)
* **Wait exactly 10 seconds** (this matches the `time=10000` milliseconds we set in step 2).
* Check your IDE terminal. You should see your custom `System.out.println` trigger, indicating the sweeper or the passive check deleted the data.
* Go back to Postman and hit **Send** on the exact same GET request.
* **Expected Result:** You will receive a red `404 Not Found` status. The data was successfully and permanently evicted from your server's RAM.

---

## ⚠️ Edge Cases & Production Improvements

While this Mini-Redis implementation perfectly demonstrates the core concept of TTL caching, deploying an in-memory `ConcurrentHashMap` to production introduces several critical edge cases. Here is how we would scale this for an enterprise environment:

### 1. The Memory Exhaustion Crash (No Capacity Limit)
* **The Edge Case:** Right now, the cache accepts unlimited entries. If a malicious user writes a script to insert 10 million large objects with a 5-hour TTL, the server will run out of RAM and crash (OOM - Out of Memory) long before the sweeper thread can delete them.
* **The Solution:** Implement a **Max Capacity Limit** combined with an **Eviction Policy** like **LRU (Least Recently Used)**. If the cache is capped at 10,000 items and a new item arrives, the system must physically delete the oldest, least-accessed item to make room, regardless of its TTL.

### 2. The Sweeper Bottleneck (O(N) Time Complexity)
* **The Edge Case:** The `@Scheduled` worker iterates through *every single key* in the map every 10 seconds. If the cache holds 5 million items, this `forEach` loop will completely monopolize the CPU and freeze the server.
* **The Solution:** Instead of scanning the entire map, we would store the expiration timestamps in a **Min-Heap (Priority Queue)**. The sweeper thread would only need to check the top of the heap (O(1) time complexity). If the top item hasn't expired, the thread instantly goes back to sleep, saving massive amounts of CPU power.

### 3. The Distributed Coherence Problem
* **The Edge Case:** If this Spring Boot application is deployed across 4 different servers behind an AWS Load Balancer, the caches are isolated. If a user updates their profile on Server A, Server B still holds the old, stale profile in its local RAM.
* **The Solution:** We must decouple the cache from the application servers. Instead of storing data inside the Java instance's RAM, all 4 servers must connect over the network to a centralized, dedicated caching cluster like **Redis** or **Memcached**.

### 4. Volatility and Server Restarts
* **The Edge Case:** RAM is volatile. If the server is restarted for a patch or crashes due to a bug, 100% of the cached data is permanently lost. Upon reboot, the database will be instantly overwhelmed by a "Cache Stampede" as all users simultaneously request data that is no longer in memory.
* **The Solution:** Implement periodic **Disk Snapshots** or a **Write-Ahead Log (WAL)**. This saves the RAM state to a hard drive every few minutes so the cache can quickly rebuild itself upon startup.
---
## 🔮 Future Enhancements

If I were to expand this specific codebase, I would add:
1. **LRU Eviction Policy:** Implement a Doubly Linked List alongside the `ConcurrentHashMap` to track the exact order in which keys are accessed. This would allow the system to evict the Least Recently Used items when a maximum capacity limit is reached.
2. **Cache Analytics API:** Create a new endpoint (`GET /api/cache/stats`) to return telemetry data like the cache hit rate, miss rate, total active items, and total evicted items.
3. **Graceful Persistence:** Add a `@PreDestroy` hook to serialize the `ConcurrentHashMap` to a local JSON file on disk when the Spring Boot server shuts down, and load it back into memory on startup.

---

## 🛑 The "No Time Constraint" Architecture

If I were building this without the strict 3-hour time constraint, I would architect a fully distributed, highly available caching cluster to handle enterprise-level web traffic. Here is what the real-world version looks like:



1. **Distributed Cluster with Consistent Hashing**
   Instead of relying on a single server's RAM, I would deploy multiple isolated Cache Node microservices. I would implement a **Consistent Hashing** algorithm so the API Gateway knows exactly which specific node holds which key. This prevents massive data shuffling if a node crashes or a new node is added.

2. **Automated Read-Through & Write-Through Strategies**
   Right now, the client manually pushes data to the cache via POST requests. In a real system, the cache sits invisibly between the API and the primary database (like PostgreSQL).
  * **Read-Through:** The API asks the cache for a user profile. If there is a cache miss, the cache *automatically* queries PostgreSQL, saves the result in RAM, and returns it to the API.
  * **Write-Through:** When a user updates their profile, the API updates the cache, which then synchronously updates PostgreSQL, guaranteeing absolute data consistency without the client managing it.

3. **Gossip Protocol for Node Health**
   To manage multiple cache nodes, I would implement a lightweight UDP-based gossip protocol (similar to the one used by the actual Redis Cluster). This allows the cache nodes to continuously ping each other, monitor cluster health, and automatically failover if one of the memory nodes goes offline.
