| # | Service Name             | Purpose                                                                                                                                                   |
| - | ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | `CommentAnalysisService` | Handles the business logic for analyzing comments using Hugging Face API. Stores the analysis results (label, toxicity, confidence) in the database.      |
| 2 | `UserService`            | Manages all user-related actions: registration, login, fetching user details, password updates, and linking user accounts to social profiles.             |
| 3 | `SocialProfileService`   | Allows users to add, edit, and delete their linked social media profiles (Instagram, X, etc.). Manages connection between app users and their accounts.   |
| 4 | `PostService`            | Provides CRUD (Create, Read, Update, Delete) operations for social media posts. Ensures posts are correctly linked to profiles and stored securely.       |
| 5 | `CommentService`         | Manages user comments on posts. Responsible for storing, retrieving, and updating comment content while linking them to posts and commenters.             |
| 6 | `TargetService`          | Detects and stores the victim (target) of bullying in a comment. Helps associate toxic behavior with specific users being affected.                       |
| 7 | `BullyReportService`     | Aggregates data from posts, comments, and analyses to generate bullying reports for dashboard insights and analytics. Supports admin monitoring features. |
| 8 | `HuggingFaceService`     | Sends comment text to Hugging Face for AI-based toxicity detection. Handles API requests/responses and maps the results to your application's schema.     |
| 9 | (Optional) `AuthService` | Manages authentication logic such as JWT generation, validation, refresh tokens, secure password hashing, and login authorization workflows.              |
