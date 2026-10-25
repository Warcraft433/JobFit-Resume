# REST API Specification — JobFit Resume

## 1. Global Standards & Conventions

All REST endpoints in JobFit Resume follow standardized conventions:
- **Base URL**: `/api/v1`
- **Payload Format**: `application/json; charset=UTF-8`
- **Authentication**: JWT Bearer token passed in the HTTP `Authorization` header:
  ```http
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
  ```
- **Envelope Standard**: All non-binary JSON responses are wrapped in a unified `ApiResponse<T>` envelope.

### 1.1. Success Response Envelope
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-10-24T12:30:00.000Z"
}
```

### 1.2. Error Response Envelope
```json
{
  "success": false,
  "message": "Validation failed",
  "error": {
    "status": 400,
    "error": "Bad Request",
    "message": "Email must be a valid email address",
    "details": ["email: must be a well-formed email address"]
  },
  "timestamp": "2026-10-24T12:30:00.000Z"
}
```

---

## 2. Authentication Endpoints (`/api/v1/auth`)

### 2.1. Register Account
- **Endpoint**: `POST /api/v1/auth/register`
- **Access**: Public
- **Request Body**:
  ```json
  {
    "fullName": "Alex Mercer",
    "email": "alex.mercer@example.com",
    "password": "Password123!"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "Bearer",
      "user": {
        "id": "e3067eb2-19e3-4623-bc70-4f59e863ea25",
        "fullName": "Alex Mercer",
        "email": "alex.mercer@example.com",
        "role": "ROLE_USER"
      }
    }
  }
  ```

### 2.2. Login
- **Endpoint**: `POST /api/v1/auth/login`
- **Access**: Public
- **Request Body**:
  ```json
  {
    "email": "alex.mercer@example.com",
    "password": "Password123!"
  }
  ```
- **Response**: `200 OK` (Returns same `AuthResponse` structure as register).

### 2.3. Current User Profile
- **Endpoint**: `GET /api/v1/auth/me`
- **Access**: Authenticated (`Bearer`)
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "e3067eb2-19e3-4623-bc70-4f59e863ea25",
      "fullName": "Alex Mercer",
      "email": "alex.mercer@example.com",
      "role": "ROLE_USER"
    }
  }
  ```

### 2.4. Change Password
- **Endpoint**: `POST /api/v1/auth/change-password`
- **Access**: Authenticated (`Bearer`)
- **Request Body**:
  ```json
  {
    "currentPassword": "Password123!",
    "newPassword": "NewSecurePassword456!"
  }
  ```
- **Response**: `200 OK`

---

## 3. Career Profile & Completeness (`/api/v1/profile`)

### 3.1. Get User Profile
- **Endpoint**: `GET /api/v1/profile`
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "060d4023-e5ef-4a11-b016-16f1cdaaa354",
      "userId": "e3067eb2-19e3-4623-bc70-4f59e863ea25",
      "fullName": "Alex Mercer",
      "professionalHeadline": "Senior Full-Stack Cloud Engineer",
      "contactEmail": "alex.mercer@example.com",
      "phone": "+1 (555) 234-5678",
      "city": "San Francisco",
      "country": "USA",
      "professionalSummary": "Results-driven engineer with 6+ years specializing in distributed Java microservices and high-scale React interfaces.",
      "linkedinUrl": "https://linkedin.com/in/alexmercer",
      "githubUrl": "https://github.com/alexmercer",
      "portfolioUrl": "https://alexmercer.dev"
    }
  }
  ```

### 3.2. Update Profile
- **Endpoint**: `PUT /api/v1/profile`
- **Request Body**: Same schema as profile response data.

### 3.3. Profile Completeness Score
- **Endpoint**: `GET /api/v1/profile/completeness`
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "score": 85,
      "breakdown": {
        "contactInfo": 20,
        "summary": 15,
        "experience": 25,
        "education": 15,
        "skills": 10,
        "projects": 0
      },
      "missingSections": ["projects"],
      "recommendations": ["Add at least one project with outcomes to reach 100%"]
    }
  }
  ```

---

## 4. Career Profile Sub-Entities

All sub-entities enforce cross-user isolation. Attempts to read, edit, or delete another user's records return `404 Not Found` or `403 Forbidden`.

### 4.1. Education (`/api/v1/education`)
- `GET /api/v1/education` -> List candidate's education records
- `POST /api/v1/education` -> Create new education entry
- `GET /api/v1/education/{id}` -> Get entry
- `PUT /api/v1/education/{id}` -> Update entry
- `DELETE /api/v1/education/{id}` -> Delete entry

**Payload Schema**:
```json
{
  "institution": "University of California, Berkeley",
  "degree": "Bachelor of Science",
  "fieldOfStudy": "Computer Science",
  "startDate": "2018-09",
  "endDate": "2022-05",
  "currentlyStudying": false,
  "gradeType": "GPA",
  "gradeValue": "3.85 / 4.00",
  "coursework": "Data Structures, Algorithms, Distributed Systems, Database Architecture",
  "description": "Dean's Honor List all semesters, ACM Student Chapter Officer",
  "displayOrder": 1
}
```

### 4.2. Skills & Dictionary (`/api/v1`)
- `GET /api/v1/skills/dictionary` -> Catalog of 200+ canonical skills
- `GET /api/v1/skills` -> List user's claimed skills
- `POST /api/v1/skills` -> Add skill to profile
- `PUT /api/v1/skills/{id}` -> Update proficiency/experience years
- `DELETE /api/v1/skills/{id}` -> Remove skill

**User Skill Payload**:
```json
{
  "skillId": "00000000-0000-0000-0000-000000000017",
  "customSkillName": null,
  "category": "BACKEND",
  "proficiency": "EXPERT",
  "yearsExperience": 5,
  "notes": "Used daily for microservices architecture"
}
```

### 4.3. Work Experience (`/api/v1/experience`)
- `GET /api/v1/experience`
- `POST /api/v1/experience`
- `GET /api/v1/experience/{id}`
- `PUT /api/v1/experience/{id}`
- `DELETE /api/v1/experience/{id}`

**Experience Payload**:
```json
{
  "employer": "TechCorp Systems",
  "jobTitle": "Senior Software Engineer",
  "location": "San Francisco, CA (Hybrid)",
  "startDate": "2022-06",
  "endDate": null,
  "currentRole": true,
  "description": "Led backend engineering team of 6 building payment infrastructure.",
  "achievements": "Architected event-driven settlement pipeline processing $50M/month.\nReduced API p99 latency by 42% via Redis multi-level caching.",
  "technologies": "Java, Spring Boot, PostgreSQL, Docker, AWS, Kafka",
  "displayOrder": 1
}
```

### 4.4. Projects (`/api/v1/projects`)
- `GET /api/v1/projects`
- `POST /api/v1/projects`
- `PUT /api/v1/projects/{id}`
- `DELETE /api/v1/projects/{id}`

### 4.5. Certifications & Achievements
- `GET /api/v1/certifications`, `POST /api/v1/certifications`, `DELETE /api/v1/certifications/{id}`
- `GET /api/v1/achievements`, `POST /api/v1/achievements`, `DELETE /api/v1/achievements/{id}`

---

## 5. Job Vacancies & Match Engine (`/api/v1/jobs`)

### 5.1. List Saved Jobs
- **Endpoint**: `GET /api/v1/jobs`

### 5.2. Create Target Job
- **Endpoint**: `POST /api/v1/jobs`
- **Request Body**:
  ```json
  {
    "company": "Stripe",
    "jobTitle": "Staff Software Engineer, Platform",
    "location": "Remote - US",
    "jobUrl": "https://stripe.com/jobs/staff-platform",
    "salaryText": "$190,000 - $240,000 + Equity",
    "jobDescription": "We are looking for a Staff Software Engineer with 5+ years of experience in Java, Spring Boot, distributed databases, Docker, and Kubernetes..."
  }
  ```

### 5.3. Analyze Vacancy & Calculate Match Score
- **Endpoint**: `POST /api/v1/jobs/{id}/analyze`
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Job analyzed successfully",
    "data": {
      "analysisId": "8a01f600-410a-475a-bc36-bb1234567890",
      "jobId": "3491f600-410a-475a-bc36-aa1234567890",
      "overallScore": 88.5,
      "breakdown": {
        "skillsScore": 92.0,
        "experienceScore": 100.0,
        "educationScore": 85.0,
        "domainKeywordsScore": 75.0
      },
      "requiredSkillsMatched": ["java", "spring_boot", "docker", "postgresql"],
      "missingSkills": ["kubernetes"],
      "requirements": [
        {
          "text": "5+ years experience building distributed backend systems in Java or Go",
          "matched": true,
          "importance": "MUST_HAVE",
          "evidence": "Candidate has 6 years verified experience"
        }
      ]
    }
  }
  ```

### 5.4. Skills Gap & Learning Recommendations
- **Endpoint**: `GET /api/v1/jobs/{id}/skills-gap`
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "jobTitle": "Staff Software Engineer, Platform",
      "company": "Stripe",
      "missingSkillsCount": 1,
      "recommendations": [
        {
          "skill": "Kubernetes",
          "category": "CLOUD_DEVOPS",
          "importance": "HIGH",
          "documentationUrl": "https://kubernetes.io/docs/home/",
          "projectIdea": "Deploy a multi-tier microservice with Helm charts, Ingress, and horizontal pod autoscalers."
        }
      ]
    }
  }
  ```

---

## 6. Resume Tailoring & Document Exports

### 6.1. Generate Tailored Resume
- **Endpoint**: `POST /api/v1/jobs/{id}/generate-resume`
- **Request Body** *(Optional)*:
  ```json
  {
    "templateKey": "modern",
    "maxExperienceItems": 3,
    "maxProjects": 2
  }
  ```
- **Response**: `200 OK` (Returns synthesized `ResumeVersionDto` with scored and prioritized sections).

### 6.2. Download A4 High-Fidelity PDF
- **Endpoint**: `GET /api/v1/resume-versions/{id}/export/pdf`
- **Response**: Binary stream (`application/pdf`)
- **Headers**:
  ```http
  Content-Disposition: attachment; filename="JobFit_Resume_{id}.pdf"
  Content-Type: application/pdf
  ```

### 6.3. Download Microsoft Word DOCX
- **Endpoint**: `GET /api/v1/resume-versions/{id}/export/docx`
- **Response**: Binary stream (`application/vnd.openxmlformats-officedocument.wordprocessingml.document`)
- **Headers**:
  ```http
  Content-Disposition: attachment; filename="JobFit_Resume_{id}.docx"
  ```

---

## 7. Application Pipeline Tracker (`/api/v1/applications`)

### 7.1. List Applications
- **Endpoint**: `GET /api/v1/applications`

### 7.2. Create Application
- **Endpoint**: `POST /api/v1/applications`
- **Request Body**:
  ```json
  {
    "jobId": "3491f600-410a-475a-bc36-aa1234567890",
    "resumeVersionId": "7d91f600-410a-475a-bc36-cc1234567890",
    "status": "APPLIED",
    "appliedAt": "2026-10-24T10:00:00Z",
    "notes": "Applied via company referral from Jane Doe"
  }
  ```

### 7.3. Quick Status Update (Kanban Drag-and-Drop)
- **Endpoint**: `PATCH /api/v1/applications/{id}/status`
- **Request Body**:
  ```json
  {
    "status": "INTERVIEW"
  }
  ```

### 7.4. Pipeline Analytics
- **Endpoint**: `GET /api/v1/applications/analytics`
- **Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "totalApplications": 12,
      "activeApplications": 7,
      "byStatus": {
        "SAVED": 2,
        "APPLIED": 4,
        "SCREENING": 1,
        "INTERVIEW": 2,
        "OFFER": 1,
        "REJECTED": 2
      },
      "interviewConversionRate": 25.0,
      "offerConversionRate": 8.3
    }
  }
  ```
