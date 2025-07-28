## 🚀 Final Optimized & Scalable PostgreSQL Schema

---

### 🔐 `auth_users` — For Spring Security

```sql
CREATE TABLE auth_users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password TEXT NOT NULL, -- bcrypt hashed
    role VARCHAR(20) DEFAULT 'USER', -- USER, MODERATOR, ADMIN
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

> 🔒 Password & roles allow Spring Security integration.

---

### 👤 `social_profiles` — Social Media Profile Info

```sql
CREATE TABLE social_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_user_id UUID REFERENCES auth_users(user_id) ON DELETE CASCADE,
    platform VARCHAR(50), -- Instagram, X, etc.
    profile_url TEXT,
    bio TEXT,
    avatar_url TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

> 🔗 Links app user → social profile
> Allows users to connect multiple accounts.

---

### 📝 `posts` — Social Media Posts

```sql
CREATE TABLE posts (
    post_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID REFERENCES social_profiles(profile_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    media_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### 💬 `comments` — Comments on Posts

```sql
CREATE TABLE comments (
    comment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID REFERENCES posts(post_id) ON DELETE CASCADE,
    commenter_profile_id UUID REFERENCES social_profiles(profile_id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### 🧠 `comment_analysis` — Hugging Face AI Output

```sql
CREATE TABLE comment_analysis (
    analysis_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id UUID REFERENCES comments(comment_id) ON DELETE CASCADE,
    is_toxic BOOLEAN,
    label VARCHAR(50), -- toxic, insult, threat, etc.
    confidence FLOAT,
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### 🎯 `targets` — Who is Being Bullied (Optional but Valuable)

```sql
CREATE TABLE targets (
    target_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id UUID REFERENCES comment_analysis(analysis_id) ON DELETE CASCADE,
    target_profile_id UUID REFERENCES social_profiles(profile_id)
);
```

---

### 📊 `bully_reports` — Precomputed Insights for Dashboards

```sql
CREATE TABLE bully_reports (
    report_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bully_profile_id UUID REFERENCES social_profiles(profile_id),
    victim_profile_id UUID REFERENCES social_profiles(profile_id),
    platform VARCHAR(50),
    post_id UUID REFERENCES posts(post_id),
    comment_id UUID REFERENCES comments(comment_id),
    label VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### ✅ Indexes for Fast Lookups

```sql
CREATE INDEX idx_auth_username ON auth_users(username);
CREATE INDEX idx_auth_email ON auth_users(email);
CREATE INDEX idx_comment_label ON comment_analysis(label);
CREATE INDEX idx_post_profile_id ON posts(profile_id);
CREATE INDEX idx_comment_post_id ON comments(post_id);
```

---

## 🛡️ Authentication & JWT Integration (Planned)

With the above schema:

* `auth_users` works seamlessly with Spring Security’s `UserDetailsService`.
* You can later add refresh tokens, password resets, and 2FA support.

---

## 🧱 ER Diagram (Simplified)

```
auth_users ──< social_profiles ──< posts ──< comments ──< comment_analysis ──< targets
                  |                          |
                  └──────────────────> bully_reports <───────
```

---
