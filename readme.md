| Rank | Service Name                 | Priority Reasoning                                                                                                         |
| ---- | ---------------------------- | -------------------------------------------------------------------------------------------------------------------------- |
| 1️⃣  | **HuggingFaceService**       | Central to the AI-based detection. No cyberbullying analysis works without this. All comment analysis relies on it.        |
| 2️⃣  | **CommentAnalysisService**   | Business logic core that orchestrates detection using HuggingFaceService and stores results.                               |
| 3️⃣  | **CommentService**           | Without comments, there’s no data to analyze. It also links users and posts to content.                                    |
| 4️⃣  | **PostService**              | Posts are needed to place comments in context, making it important for content mapping.                                    |
| 5️⃣  | **UserService**              | Required for identifying who is commenting or being bullied. Manages registration and login.                               |
| 6️⃣  | **TargetService**            | Extracts victims from toxic comments, essential for pinpointing bullying targets.                                          |
| 7️⃣  | **BullyReportService**       | Supports admins by aggregating data for actionable insights. Needed after core analysis works.                             |
| 8️⃣  | **SocialProfileService**     | Connects users to real-world social media accounts. Enhances traceability but not essential to basic function.             |
| 9️⃣  | **AuthService** *(Optional)* | Needed for securing access but considered auxiliary for the MVP unless user security is a strict requirement from day one. |
