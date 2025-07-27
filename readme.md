sequenceDiagram
    participant User as User (Browser)
    participant React as React Frontend
    participant API as Spring Boot API
    participant Service as ToxicityService
    participant AIProvider as AI Provider Interface
    participant AI as External AI API<br/>(Cohere/OpenAI/HuggingFace)
    participant EmailSvc as EmailService
    participant SMTP as Email Provider<br/>(SMTP/SendGrid)
    participant DB as PostgreSQL Database
    participant Scheduler as Background Scheduler

    Note over User, Scheduler: User-Triggered Analysis Flow

    %% User initiates analysis
    User->>React: Click "Analyze Comments" or "Re-analyze"
    React->>React: Show loading spinner
    React->>API: POST /api/toxicity/analyze-batch
    
    Note over API: Controller validates request and delegates to service
    API->>Service: analyzeBatchComments(commentIds)
    
    %% Fetch comments from database
    Service->>DB: SELECT comments WHERE id IN (commentIds)
    DB-->>Service: Return comment entities
    
    %% Process each comment through AI
    loop For each comment
        Service->>AIProvider: analyzeContent(comment.text)
        AIProvider->>AI: HTTP POST with comment text
        
        Note over AI: AI model processes text<br/>Returns toxicity probability<br/>and classification details
        
        AI-->>AIProvider: {"isToxic": true, "confidence": 0.87, "categories": ["harassment"]}
        AIProvider-->>Service: AIAnalysisDTO object
        
        alt If toxicity detected (confidence > threshold)
            Service->>DB: UPDATE comments SET is_toxic=true, toxicity_score=0.87
            Service->>DB: INSERT INTO toxicity_logs (comment_id, ai_provider, score, result)
            
            Note over Service: Log the analysis result<br/>for audit trail and model comparison
            
            Service->>EmailSvc: sendToxicityAlert(comment, analysisResult)
            EmailSvc->>DB: SELECT user WHERE id = comment.author_id
            DB-->>EmailSvc: Return user with email address
            
            EmailSvc->>SMTP: Send email alert to comment author
            SMTP-->>EmailSvc: Email delivery confirmation
            
            Note over SMTP: Email contains:<br/>- Comment excerpt<br/>- Toxicity explanation<br/>- Appeal process info<br/>- Community guidelines link
            
        else If comment is clean
            Service->>DB: UPDATE comments SET is_toxic=false, toxicity_score=0.12
            Service->>DB: INSERT INTO toxicity_logs (comment_id, ai_provider, score, result)
        end
    end
    
    %% Return results to frontend
    Service-->>API: Return analysis summary (total processed, toxic found, etc.)
    API-->>React: HTTP 200 with ToxicityReportDTO
    
    React->>React: Hide loading spinner
    React->>React: Update comment list with toxicity flags
    React->>React: Show success notification with stats
    
    User->>React: View updated comments with toxic highlights
    
    Note over User, Scheduler: Background Scheduled Analysis Flow
    
    %% Scheduled analysis runs automatically
    Scheduler->>Service: scheduledAnalysis() - runs every 30 minutes
    Service->>DB: SELECT comments WHERE analyzed_at IS NULL OR analyzed_at < NOW() - INTERVAL '24 hours'
    DB-->>Service: Return unanalyzed or stale comments
    
    Note over Service: Same AI analysis process<br/>as user-triggered flow<br/>but runs automatically
    
    Service->>AIProvider: Batch analyze new comments
    AIProvider->>AI: Multiple API calls for efficiency
    AI-->>AIProvider: Batch analysis results
    
    Service->>DB: Bulk update comments with results
    Service->>EmailSvc: Send alerts for newly detected toxic content
    EmailSvc->>SMTP: Send email notifications
    
    Note over User, Scheduler: Real-time Dashboard Updates
    
    User->>React: Navigate to Analytics Dashboard
    React->>API: GET /api/reports/toxicity-stats
    API->>DB: Complex query for aggregated statistics
    DB-->>API: Return statistics (toxic %, trends, user patterns)
    API-->>React: ToxicityReportDTO with charts data
    React->>React: Render charts and statistics
    
    Note over User, React: User can see:<br/>- Total comments analyzed<br/>- Toxicity percentage by day<br/>- Most problematic users<br/>- AI model accuracy metrics<br/>- Recent email alerts sent
    
    %% Appeal process flow
    User->>React: Click "Appeal False Positive" on comment
    React->>API: POST /api/comments/{id}/appeal
    API->>Service: processAppeal(commentId, userReason)
    Service->>DB: INSERT INTO appeals table
    Service->>EmailSvc: notifyModerators(appealDetails)
    EmailSvc->>SMTP: Send email to moderators
    API-->>React: Appeal submitted confirmation
    React->>React: Show appeal status on comment
    
    Note over User, Scheduler: This comprehensive flow ensures:<br/>1. Real-time user-triggered analysis<br/>2. Automated background processing<br/>3. Immediate email notifications<br/>4. Audit trail for all decisions<br/>5. Appeal mechanism for false positives<br/>6. Rich dashboard analytics
