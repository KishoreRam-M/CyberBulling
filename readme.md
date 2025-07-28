## ✅ 1. **System Architecture Overview**

### 🔧 **Components & Flow**

```
[ Social Media API / Data Source ]
            |
            v
   [Comment Ingestion Service]
            |
            v
   [PostgreSQL Database] <-----> [Toxicity Detection Service]
            |
            v
   [Analysis & Reporting Service]
            |
            v
     [REST API / Admin Panel]
```

### 📌 **Component Responsibilities**

| Component                | Responsibility                                                                               |
| ------------------------ | -------------------------------------------------------------------------------------------- |
| **Comment Ingestion**    | Store raw comments into PostgreSQL with metadata (user, post, platform, timestamp).          |
| **Toxicity Detection**   | Fetch unprocessed comments → send to Hugging Face → store results (toxic/targeted user etc). |
| **Analysis & Reporting** | Aggregate bully behavior: who bullied whom, where, when.                                     |
| **Scheduler / Worker**   | Batch or scheduled background jobs for analysis (preferred over real-time for Hugging Face). |
| **REST API**             | Admin endpoints for reports, stats, and user-wise filtering.                                 |

---

## ✅ 2. **Normalized Database Schema**

### 📊 ER Diagram (Text-Based)

```sql
User (user_id PK)
Post (post_id PK, platform, user_id FK)
Comment (comment_id PK, post_id FK, commenter_id FK, content, timestamp)
Analysis (analysis_id PK, comment_id FK, is_toxic, label, confidence, detected_at)
Target (target_id PK, analysis_id FK, target_user_id FK)
```

### 🧩 Table Structures

#### `user`

| Field         | Type    |
| ------------- | ------- |
| user\_id (PK) | UUID    |
| username      | VARCHAR |
| email         | VARCHAR |
| platform      | VARCHAR |

#### `post`

| Field         | Type      |
| ------------- | --------- |
| post\_id (PK) | UUID      |
| platform      | VARCHAR   |
| user\_id (FK) | UUID      |
| created\_at   | TIMESTAMP |

#### `comment`

| Field              | Type      |
| ------------------ | --------- |
| comment\_id (PK)   | UUID      |
| post\_id (FK)      | UUID      |
| commenter\_id (FK) | UUID      |
| content            | TEXT      |
| timestamp          | TIMESTAMP |

#### `analysis`

| Field             | Type      |                                     |
| ----------------- | --------- | ----------------------------------- |
| analysis\_id (PK) | UUID      |                                     |
| comment\_id (FK)  | UUID      |                                     |
| is\_toxic         | BOOLEAN   |                                     |
| label             | VARCHAR   | (`toxic`, `insult`, `threat`, etc.) |
| confidence        | FLOAT     |                                     |
| detected\_at      | TIMESTAMP |                                     |

#### `target` (who is being bullied — optional but useful)

| Field                | Type |
| -------------------- | ---- |
| target\_id (PK)      | UUID |
| analysis\_id (FK)    | UUID |
| target\_user\_id(FK) | UUID |

---

## ✅ 3. **Optional Tables**

### `bully_reports`

Precomputed reports for quick access.

| Field            | Type      |
| ---------------- | --------- |
| report\_id       | UUID      |
| bully\_user\_id  | UUID      |
| victim\_user\_id | UUID      |
| platform         | VARCHAR   |
| post\_id         | UUID      |
| comment\_id      | UUID      |
| label            | VARCHAR   |
| created\_at      | TIMESTAMP |

> These are useful for audit logs and quick visualization in the frontend.

---

## ✅ 4. **Backend Folder Structure**

```bash
src/main/java/com/yourname/cyberbullydetection/
├── controller/
│   └── ReportController.java
├── dto/
│   └── AnalysisRequestDTO.java
├── model/
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
│   ├── Analysis.java
│   └── Target.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── CommentRepository.java
│   ├── AnalysisRepository.java
│   └── TargetRepository.java
├── service/
│   ├── CommentIngestionService.java
│   ├── ToxicityDetectionService.java
│   ├── ReportingService.java
│   └── HuggingFaceClient.java
├── scheduler/
│   └── CommentAnalysisScheduler.java
└── CyberBullyDetectionApplication.java
```

> 💡 **Modular, service-driven design** ensures ease of testing, deployment, and scaling.

---

## ✅ 5. **Real-time vs Background Job**

| Strategy                         | Use When                                                                                             |
| -------------------------------- | ---------------------------------------------------------------------------------------------------- |
| ✅ **Background Job (Scheduled)** | Batch analyze new/unprocessed comments (e.g. every 5 mins).                                          |
| ⚠️ Real-time API calls           | Only for live moderation (comment before post). Not ideal with Hugging Face latency and rate limits. |

> **Use a Spring `@Scheduled` task** to batch-pull unprocessed comments and call Hugging Face’s API.

---

## ✅ 6. **Avoiding Duplicate Processing / False Positives**

* ✅ In `analysis` table, use a **UNIQUE constraint on `comment_id`**.
* ✅ Maintain a `status` field in comments (`PENDING`, `PROCESSED`, `ERROR`) to prevent reprocessing.
* ✅ Apply **threshold-based confidence filtering** (`confidence > 0.7`) to reduce false positives.
* ✅ Optionally implement a **feedback loop** or admin override for correction.

---

## ✅ 7. **Performance, Scaling & Developer Experience Tips**

### ⚙️ Performance & Scaling

* ✅ Use **connection pooling** (e.g., HikariCP).
* ✅ Paginate queries when analyzing large comment sets.
* ✅ Cache user and post data if accessed frequently.
* ✅ Deploy on **Render + PostgreSQL (ElephantSQL or Supabase)** for smooth DevOps.

### 🛠 Developer Experience

* Use **Lombok** to reduce boilerplate.
* Define **DTOs** for clean controller-service separation.
* Add **OpenAPI/Swagger** for API documentation.
* Use **Spring Profiles** for dev/test/prod environments.

---

## ✅ Sample Workflow (Simplified)

1. Comment is added to `comment` table via ingestion service or API.
2. Scheduler runs every 5 minutes:

   * Finds unprocessed comments
   * Sends content to Hugging Face `toxic-bert`
   * Stores result in `analysis` and `target` tables
3. ReportController provides:

   * `/api/reports/bully-summary`
   * `/api/reports/user/{id}`
   * `/api/reports/platform/{platform}`
