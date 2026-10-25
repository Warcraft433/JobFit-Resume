# JobFit Resume — Enterprise Java Full Stack Application

[![Java 21 LTS](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React 19](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![TypeScript 5.6](https://img.shields.io/badge/TypeScript-5.6-blue.svg)](https://www.typescriptlang.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

JobFit Resume is a production-grade, multi-user web application designed to help job seekers maintain **one master career profile** and generate customized, ATS-friendly resumes for different job vacancies without repeatedly entering their information.

---

## Key Features

1. **Comprehensive Career Profile (Single Source of Truth)**
   - Maintain master records for personal contact details, work history, degrees, technical skills, projects, and certifications.
   - Dynamic profile completeness engine calculating real-time readiness scores with actionable suggestions.

2. **Rule-Based Job Description Analyzer & Matcher**
   - Deterministic NLP parser extracting technical & soft skills, years of experience, and degree requirements from raw job descriptions.
   - **Explainable Weighted Scoring**:
     - **50%** Technical & Soft Skills Match
     - **25%** Experience Requirements Match
     - **15%** Education Degree Match
     - **10%** Domain Keywords & Project Relevance
   - Clear visual breakdown of matching skills vs. missing requirements.

3. **Curated Skills Gap & Learning Recommendations**
   - Automatically identifies missing skills from the target vacancy.
   - Provides rule-based learning paths with official documentation, core concepts, estimated study times, and practice project ideas.

4. **Truth-Preserving Resume Tailoring Engine**
   - Ranks and selects the candidate's most relevant projects and experience bullets for each vacancy.
   - Formulates tailored professional summaries aligned with the target job title.
   - Strictly truth-preserving: never hallucinates or fabricates fake credentials.

5. **Dual High-Fidelity Document Generation**
   - **A4 PDF Export**: Pure Java layout engine using **OpenHTMLtoPDF** supporting 3 distinct templates:
     - *Classic ATS*: Clean single-column, standard serif/sans fonts, zero graphics.
     - *Modern Professional*: Contemporary accent bar, clean dividers, sleek typography.
     - *Compact Technical*: High information density, categorized skill matrix, project focus.
   - **Word DOCX Export**: Native Microsoft Word document generation using **Apache POI**.

6. **Interactive Job Application Pipeline Tracker**
   - Kanban board and list views with stages: `SAVED`, `PREPARING`, `APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`.
   - Direct linkage between applications, target vacancies, and generated resume version snapshots.
   - Pipeline funnel analytics and response rate metrics.

7. **Enterprise Security & Data Isolation**
   - Stateless JWT authentication with Bearer token and HttpOnly cookie support.
   - BCrypt password hashing.
   - Strict multi-tenant user scoping queries preventing cross-user data leakage.

---

## Architecture Overview

```
                      +-----------------------------+
                      |   React 19 + TypeScript     |
                      |   Tailwind CSS + Lucide     |
                      +--------------+--------------+
                                     |
                                REST API (JWT)
                                     v
                      +-----------------------------+
                      |    Spring Security 6        |
                      +--------------+--------------+
                                     |
       +-----------------------------+-----------------------------+
       |                             |                             |
       v                             v                             v
+--------------+              +--------------+              +--------------+
| Auth & User  |              | Job Analyzer |              | Resume & Doc |
| Controllers  |              | & Matcher    |              | Generation   |
+-------+------+              +-------+------+              +-------+------+
        |                             |                             |
        |                             v                             |
        |                     +--------------+                      |
        |                     | Skill Dict & |                      |
        |                     | Token Parser |                      |
        |                     +--------------+                      |
        |                                                           v
        |                                                   +--------------+
        |                                                   | OpenHTMLtoPDF|
        |                                                   |  Apache POI  |
        |                                                   +--------------+
        |                                                           |
        +-----------------------------+-----------------------------+
                                      |
                                      v
                      +-----------------------------+
                      |   Flyway Database Migrations|
                      |   PostgreSQL / H2 In-Memory |
                      +-----------------------------+
```

---

## Quick Start (Local Development)

### Prerequisites
- **Java 21 LTS** (`java -version`)
- **Maven 3.9+** (`mvn -v`)
- **Node.js 20+** and **npm** (`node -v`)

### 1. Run Backend (Zero-Setup In-Memory Mode)
The backend comes pre-configured with an in-memory PostgreSQL-compatible H2 database and Flyway migrations, requiring zero external database installations for instant testing.

```bash
cd backend
mvn spring-boot:run
```
Backend API will start on `http://localhost:8080`.
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

### 2. Run Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend will be available at `http://localhost:5173`.

---

## Automated Test Suites

### Backend Tests
```bash
cd backend
mvn test
```
The test suite includes:
- **`AuthIntegrationTest`**: Registration, login, validation, and JWT verification.
- **`UserIsolationIntegrationTest`**: Multi-user tenancy and data isolation verification.
- **`JobDescriptionParserTest`**: Regex extraction of skills, experience years, and degrees.
- **`ResumeExportTest`**: Validates PDF rendering via OpenHTMLtoPDF and DOCX generation via Apache POI.

### Frontend Production Build
```bash
cd frontend
npm run build
```

---

## Docker Deployment

To launch the full production stack with PostgreSQL:

```bash
docker-compose up --build -d
```

---

## Documentation

Full architectural and technical references are maintained in the [`docs/`](docs/) directory:
- [Architecture & Design System](docs/ARCHITECTURE.md)
- [Database Schema & Migrations](docs/DATABASE.md)
- [REST API Specification](docs/API.md)
- [Deployment Guide (Render / Railway / Neon / Vercel)](docs/DEPLOYMENT.md)
- [Security & OWASP Compliance](docs/SECURITY.md)
- [Automated Testing Strategy](docs/TESTING.md)
- [Product Roadmap & Future Work](docs/ROADMAP.md)
