-- ==============================================================================
-- JobFit Resume — V1 Initial Schema
-- PostgreSQL & H2 compatible syntax
-- ==============================================================================

-- 1. Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_users_email ON users(email);

-- 2. User Profiles Table
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    professional_headline VARCHAR(255),
    contact_email VARCHAR(255),
    phone VARCHAR(50),
    city VARCHAR(100),
    country VARCHAR(100),
    professional_summary TEXT,
    linkedin_url VARCHAR(500),
    github_url VARCHAR(500),
    portfolio_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);

-- 3. Education Table
CREATE TABLE education (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    institution VARCHAR(255) NOT NULL,
    degree VARCHAR(255) NOT NULL,
    field_of_study VARCHAR(255),
    start_date VARCHAR(50),
    end_date VARCHAR(50),
    currently_studying BOOLEAN NOT NULL DEFAULT FALSE,
    grade_type VARCHAR(50),
    grade_value VARCHAR(50),
    coursework TEXT,
    description TEXT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_education_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_education_user_id ON education(user_id);

-- 4. Skills Dictionary Table
CREATE TABLE skills (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    canonical_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL
);

CREATE INDEX idx_skills_canonical_name ON skills(canonical_name);
CREATE INDEX idx_skills_category ON skills(category);

-- 5. User Skills Table
CREATE TABLE user_skills (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    skill_id UUID,
    custom_skill_name VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    proficiency VARCHAR(50),
    years_experience INT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_user_skills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE SET NULL
);

CREATE INDEX idx_user_skills_user_id ON user_skills(user_id);

-- 6. Experience Table
CREATE TABLE experience (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    employer VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    start_date VARCHAR(50),
    end_date VARCHAR(50),
    "current_role" BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    achievements TEXT,
    technologies TEXT,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_experience_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_experience_user_id ON experience(user_id);

-- 7. Projects Table
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    problem_statement TEXT,
    technical_stack TEXT,
    responsibilities TEXT,
    outcomes TEXT,
    github_url VARCHAR(500),
    demo_url VARCHAR(500),
    start_date VARCHAR(50),
    end_date VARCHAR(50),
    category VARCHAR(100),
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_projects_user_id ON projects(user_id);

-- 8. Certifications Table
CREATE TABLE certifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    issuer VARCHAR(255) NOT NULL,
    issue_date VARCHAR(50),
    expiration_date VARCHAR(50),
    credential_id VARCHAR(255),
    credential_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_certifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_certifications_user_id ON certifications(user_id);

-- 9. Achievements Table
CREATE TABLE achievements (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    issuer VARCHAR(255),
    achievement_date VARCHAR(50),
    description TEXT,
    url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_achievements_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_achievements_user_id ON achievements(user_id);

-- 10. Jobs Table
CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    company VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    job_description TEXT NOT NULL,
    job_url VARCHAR(500),
    salary_text VARCHAR(255),
    saved_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_jobs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_jobs_user_id ON jobs(user_id);

-- 11. Job Analyses Table
CREATE TABLE job_analyses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    job_id UUID NOT NULL,
    parser_version VARCHAR(50) NOT NULL,
    analysis_json TEXT NOT NULL,
    match_score DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_job_analyses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_analyses_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

CREATE INDEX idx_job_analyses_user_id ON job_analyses(user_id);
CREATE INDEX idx_job_analyses_job_id ON job_analyses(job_id);

-- 12. Job Requirements Table
CREATE TABLE job_requirements (
    id UUID PRIMARY KEY,
    job_analysis_id UUID NOT NULL,
    requirement_text TEXT NOT NULL,
    canonical_skill_id UUID,
    requirement_type VARCHAR(50) NOT NULL,
    importance VARCHAR(50) NOT NULL,
    matched BOOLEAN NOT NULL DEFAULT FALSE,
    evidence_json TEXT,
    CONSTRAINT fk_job_requirements_analysis FOREIGN KEY (job_analysis_id) REFERENCES job_analyses(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_requirements_skill FOREIGN KEY (canonical_skill_id) REFERENCES skills(id) ON DELETE SET NULL
);

CREATE INDEX idx_job_requirements_analysis_id ON job_requirements(job_analysis_id);

-- 13. Resumes Table
CREATE TABLE resumes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    job_id UUID,
    title VARCHAR(255) NOT NULL,
    template_key VARCHAR(50) NOT NULL DEFAULT 'classic',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_resumes_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL
);

CREATE INDEX idx_resumes_user_id ON resumes(user_id);

-- 14. Resume Versions Table
CREATE TABLE resume_versions (
    id UUID PRIMARY KEY,
    resume_id UUID NOT NULL,
    version_number INT NOT NULL,
    content_json TEXT NOT NULL,
    profile_snapshot_json TEXT,
    analysis_snapshot_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_resume_versions_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

CREATE INDEX idx_resume_versions_resume_id ON resume_versions(resume_id);

-- 15. Applications Table
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    job_id UUID NOT NULL,
    resume_version_id UUID,
    status VARCHAR(50) NOT NULL DEFAULT 'SAVED',
    applied_at TIMESTAMP WITH TIME ZONE,
    follow_up_at TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_resume_version FOREIGN KEY (resume_version_id) REFERENCES resume_versions(id) ON DELETE SET NULL
);

CREATE INDEX idx_applications_user_id ON applications(user_id);
CREATE INDEX idx_applications_status ON applications(status);

-- Seed Essential Canonical Skills
INSERT INTO skills (id, name, canonical_name, category) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Java', 'java', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000002', 'Python', 'python', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000003', 'JavaScript', 'javascript', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000004', 'TypeScript', 'typescript', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000005', 'C++', 'cpp', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000006', 'C#', 'csharp', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000007', 'Go', 'go', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000008', 'Rust', 'rust', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000009', 'SQL', 'sql', 'PROGRAMMING_LANGUAGE'),
    ('00000000-0000-0000-0000-000000000010', 'HTML5', 'html5', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000011', 'CSS3', 'css3', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000012', 'React', 'react', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000013', 'Next.js', 'nextjs', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000014', 'Vue.js', 'vue', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000015', 'Angular', 'angular', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000016', 'Tailwind CSS', 'tailwindcss', 'FRONTEND'),
    ('00000000-0000-0000-0000-000000000017', 'Spring Boot', 'spring_boot', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000018', 'Spring Framework', 'spring_framework', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000019', 'Spring Security', 'spring_security', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000020', 'Hibernate / JPA', 'hibernate_jpa', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000021', 'Node.js', 'nodejs', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000022', 'Express.js', 'express', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000023', 'Django', 'django', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000024', 'FastAPI', 'fastapi', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000025', 'REST APIs', 'rest_api', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000026', 'GraphQL', 'graphql', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000027', 'Microservices', 'microservices', 'BACKEND'),
    ('00000000-0000-0000-0000-000000000028', 'PostgreSQL', 'postgresql', 'DATABASE'),
    ('00000000-0000-0000-0000-000000000029', 'MySQL', 'mysql', 'DATABASE'),
    ('00000000-0000-0000-0000-000000000030', 'MongoDB', 'mongodb', 'DATABASE'),
    ('00000000-0000-0000-0000-000000000031', 'Redis', 'redis', 'DATABASE'),
    ('00000000-0000-0000-0000-000000000032', 'Docker', 'docker', 'CLOUD_DEVOPS'),
    ('00000000-0000-0000-0000-000000000033', 'Kubernetes', 'kubernetes', 'CLOUD_DEVOPS'),
    ('00000000-0000-0000-0000-000000000034', 'AWS', 'aws', 'CLOUD_DEVOPS'),
    ('00000000-0000-0000-0000-000000000035', 'Azure', 'azure', 'CLOUD_DEVOPS'),
    ('00000000-0000-0000-0000-000000000036', 'GCP', 'gcp', 'CLOUD_DEVOPS'),
    ('00000000-0000-0000-0000-000000000037', 'CI/CD Pipelines', 'cicd', 'CLOUD_DEVOPS'),
    ('00000000-0000-0000-0000-000000000038', 'Git / GitHub', 'git', 'DEV_TOOLS'),
    ('00000000-0000-0000-0000-000000000039', 'Linux', 'linux', 'DEV_TOOLS'),
    ('00000000-0000-0000-0000-000000000040', 'Maven', 'maven', 'DEV_TOOLS'),
    ('00000000-0000-0000-0000-000000000041', 'Unit Testing / JUnit', 'junit', 'DEV_TOOLS'),
    ('00000000-0000-0000-0000-000000000042', 'Problem Solving', 'problem_solving', 'SOFT_SKILL'),
    ('00000000-0000-0000-0000-000000000043', 'Team Leadership', 'team_leadership', 'SOFT_SKILL'),
    ('00000000-0000-0000-0000-000000000044', 'Communication', 'communication', 'SOFT_SKILL'),
    ('00000000-0000-0000-0000-000000000045', 'Agile / Scrum', 'agile_scrum', 'SOFT_SKILL');
