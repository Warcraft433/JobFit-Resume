package com.jobfitresume.job.parser;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class SkillDictionary {

    public static class SkillMeta {
        private final String canonicalName;
        private final String displayName;
        private final String category;
        private final String documentationUrl;
        private final String estimatedLearnTime;
        private final String keyConcepts;
        private final List<String> projectIdeas;

        public SkillMeta(
                String canonicalName,
                String displayName,
                String category,
                String documentationUrl,
                String estimatedLearnTime,
                String keyConcepts,
                List<String> projectIdeas) {
            this.canonicalName = canonicalName;
            this.displayName = displayName;
            this.category = category;
            this.documentationUrl = documentationUrl;
            this.estimatedLearnTime = estimatedLearnTime;
            this.keyConcepts = keyConcepts;
            this.projectIdeas = projectIdeas;
        }

        public String getCanonicalName() {
            return canonicalName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCategory() {
            return category;
        }

        public String getDocumentationUrl() {
            return documentationUrl;
        }

        public String getEstimatedLearnTime() {
            return estimatedLearnTime;
        }

        public String getKeyConcepts() {
            return keyConcepts;
        }

        public List<String> getProjectIdeas() {
            return projectIdeas;
        }
    }

    private final Map<String, SkillMeta> canonicalSkills = new LinkedHashMap<>();
    private final Map<String, String> aliasToCanonical = new HashMap<>();

    public SkillDictionary() {
        initDictionary();
    }

    private void register(SkillMeta meta, String... aliases) {
        canonicalSkills.put(meta.getCanonicalName().toLowerCase(), meta);
        aliasToCanonical.put(meta.getCanonicalName().toLowerCase(), meta.getCanonicalName().toLowerCase());
        aliasToCanonical.put(meta.getDisplayName().toLowerCase(), meta.getCanonicalName().toLowerCase());
        for (String alias : aliases) {
            aliasToCanonical.put(alias.toLowerCase(), meta.getCanonicalName().toLowerCase());
        }
    }

    private void initDictionary() {
        // Backend & Java
        register(new SkillMeta("java", "Java", "BACKEND", "https://docs.oracle.com/en/java/", "2-3 weeks",
                "OOP, Streams, Concurrency, Virtual Threads, JVM Internals",
                List.of("Multi-threaded web scraper", "In-memory key-value cache")),
                "java 17", "java 21", "java 11", "java 8", "core java", "modern java");

        register(new SkillMeta("spring_boot", "Spring Boot", "BACKEND", "https://spring.io/projects/spring-boot", "2 weeks",
                "Dependency Injection, Auto-configuration, Spring Data JPA, Actuator",
                List.of("RESTful E-Commerce Microservice", "Inventory Management API")),
                "spring boot 3", "spring boot 2", "springboot", "spring-boot", "spring");

        register(new SkillMeta("spring_security", "Spring Security", "BACKEND", "https://spring.io/projects/spring-security", "1-2 weeks",
                "SecurityFilterChain, JWT, OAuth2, RBAC, CSRF protection",
                List.of("Role-based Identity Provider", "Stateless API Gateway with JWT")),
                "spring security 6", "spring-security", "springsecurity");

        register(new SkillMeta("hibernate_jpa", "Hibernate / JPA", "BACKEND", "https://hibernate.org/orm/", "1-2 weeks",
                "Entity lifecycle, lazy vs eager loading, N+1 query optimization, criteria API",
                List.of("High-throughput transaction logging service")),
                "hibernate", "jpa", "spring data jpa");

        register(new SkillMeta("python", "Python", "PROGRAMMING_LANGUAGE", "https://docs.python.org/3/", "2 weeks",
                "Data structures, decorators, asyncio, packaging, PyPI",
                List.of("Automated ETL pipeline", "CLI tool with Click/Typer")),
                "python3", "python 3", "py");

        register(new SkillMeta("fastapi", "FastAPI", "BACKEND", "https://fastapi.tiangolo.com/", "1 week",
                "Pydantic schemas, dependency injection, async endpoints, OpenAPI",
                List.of("AI Model Serving Microservice")),
                "fast api", "fast-api");

        register(new SkillMeta("django", "Django", "BACKEND", "https://www.djangoproject.com/", "2 weeks",
                "MTV pattern, ORM, Django REST framework, middleware",
                List.of("Content management system with DRF")),
                "django rest framework", "drf");

        register(new SkillMeta("nodejs", "Node.js", "BACKEND", "https://nodejs.org/", "2 weeks",
                "Event loop, streams, buffer, cluster module, npm packages",
                List.of("Real-time collaborative chat server with WebSockets")),
                "node", "node.js", "nodejs runtime");

        register(new SkillMeta("express", "Express.js", "BACKEND", "https://expressjs.com/", "1 week",
                "Middleware chaining, routing, error handling, CORS",
                List.of("Authentication microservice with rate limiting")),
                "express", "expressjs", "express.js");

        // Frontend
        register(new SkillMeta("javascript", "JavaScript", "FRONTEND", "https://developer.mozilla.org/en-US/docs/Web/JavaScript", "2-3 weeks",
                "ES6+, Promises, Async/Await, Closures, DOM manipulation, Event loop",
                List.of("Interactive Kanban board", "Audio visualizer with Web Audio API")),
                "js", "es6", "vanilla js", "modern javascript");

        register(new SkillMeta("typescript", "TypeScript", "FRONTEND", "https://www.typescriptlang.org/", "1-2 weeks",
                "Generics, utility types, discriminating unions, type narrowing, tsconfig",
                List.of("Type-safe state management store", "Strictly-typed component library")),
                "ts", "typescipt");

        register(new SkillMeta("react", "React", "FRONTEND", "https://react.dev/", "2-3 weeks",
                "Hooks (useState, useEffect, useMemo), Context API, Custom hooks, Virtual DOM",
                List.of("Complex multi-step form wizard", "Live financial dashboard with charts")),
                "reactjs", "react.js", "react 18", "react 19");

        register(new SkillMeta("nextjs", "Next.js", "FRONTEND", "https://nextjs.org/", "2 weeks",
                "App Router, Server Components (RSC), SSR/SSG/ISR, API routes",
                List.of("Full-stack blog platform with dynamic SEO")),
                "next.js", "next");

        register(new SkillMeta("vue", "Vue.js", "FRONTEND", "https://vuejs.org/", "2 weeks",
                "Composition API, Pinia state management, Vue Router, Reactivity system",
                List.of("Task manager with drag-and-drop")),
                "vuejs", "vue.js", "vue 3");

        register(new SkillMeta("angular", "Angular", "FRONTEND", "https://angular.dev/", "3 weeks",
                "RxJS Observables, Dependency Injection, Components & Signals, NgRx",
                List.of("Enterprise ERP portal with role-based routing")),
                "angularjs", "angular 17", "angular 18");

        register(new SkillMeta("html5", "HTML5", "FRONTEND", "https://developer.mozilla.org/en-US/docs/Web/HTML", "1 week",
                "Semantic elements, canvas, web storage, accessibility (ARIA)",
                List.of("Accessible interactive portfolio")),
                "html", "semantic html");

        register(new SkillMeta("css3", "CSS3", "FRONTEND", "https://developer.mozilla.org/en-US/docs/Web/CSS", "1-2 weeks",
                "Flexbox, Grid, CSS Variables, Animations, Responsive Media Queries",
                List.of("Responsive mobile-first landing page")),
                "css", "modern css");

        register(new SkillMeta("tailwindcss", "Tailwind CSS", "FRONTEND", "https://tailwindcss.com/", "1 week",
                "Utility-first classes, responsive variants, dark mode, JIT compiler",
                List.of("Modern SaaS design system with dark/light theme")),
                "tailwind", "tailwind-css");

        // Databases
        register(new SkillMeta("postgresql", "PostgreSQL", "DATABASE", "https://www.postgresql.org/docs/", "2 weeks",
                "Indexing (B-Tree, GIN), ACID transactions, window functions, CTEs, JSONB",
                List.of("Analytics warehouse with complex reporting queries")),
                "postgres", "psql", "postgre");

        register(new SkillMeta("mysql", "MySQL", "DATABASE", "https://dev.mysql.com/doc/", "2 weeks",
                "InnoDB storage engine, replication, indexing strategies, query profiling",
                List.of("High-concurrency ordering system")),
                "mariadb");

        register(new SkillMeta("mongodb", "MongoDB", "DATABASE", "https://www.mongodb.com/docs/", "1-2 weeks",
                "Aggregation pipeline, document modeling, sharding, indexes",
                List.of("User activity and event tracking store")),
                "mongo", "nosql");

        register(new SkillMeta("redis", "Redis", "DATABASE", "https://redis.io/documentation", "1 week",
                "Data structures (hashes, sorted sets), caching patterns, pub/sub, TTL eviction",
                List.of("Distributed rate limiter and session cache")),
                "redis cache");

        register(new SkillMeta("sql", "SQL", "DATABASE", "https://www.w3schools.com/sql/", "1-2 weeks",
                "Joins, Subqueries, Group By, Indexes, Schema Design, Foreign Keys",
                List.of("Relational database schema for an online marketplace")),
                "ansi sql", "relational database", "rdbms");

        // DevOps & Cloud
        register(new SkillMeta("docker", "Docker", "CLOUD_DEVOPS", "https://docs.docker.com/", "1 week",
                "Multi-stage Dockerfiles, caching, compose services, network bridges, volume mounts",
                List.of("Containerized multi-service full stack app")),
                "containerization", "containers", "docker-compose", "docker compose");

        register(new SkillMeta("kubernetes", "Kubernetes", "CLOUD_DEVOPS", "https://kubernetes.io/docs/", "3-4 weeks",
                "Pods, Deployments, Services, Ingress, ConfigMaps, Secrets, Horizontal Pod Autoscaling",
                List.of("Zero-downtime rolling update deployment cluster")),
                "k8s", "kubectl");

        register(new SkillMeta("aws", "AWS", "CLOUD_DEVOPS", "https://aws.amazon.com/documentation/", "3-4 weeks",
                "EC2, S3, RDS, Lambda, API Gateway, IAM security policies, CloudWatch",
                List.of("Serverless image processing pipeline")),
                "amazon web services", "aws cloud", "ec2", "s3", "lambda");

        register(new SkillMeta("cicd", "CI/CD Pipelines", "CLOUD_DEVOPS", "https://docs.github.com/en/actions", "1 week",
                "GitHub Actions workflows, automated testing, artifact bundling, environment secrets",
                List.of("Automated test, lint, and deploy pipeline on pull request")),
                "ci/cd", "continuous integration", "continuous delivery", "github actions", "gitlab ci", "jenkins");

        register(new SkillMeta("git", "Git / GitHub", "DEV_TOOLS", "https://git-scm.com/doc", "1 week",
                "Branching strategies (GitFlow), rebase, interactive staging, resolving conflicts",
                List.of("Open source contribution workflow with automated branch protection")),
                "git", "github", "gitlab", "version control");

        register(new SkillMeta("linux", "Linux", "DEV_TOOLS", "https://www.kernel.org/doc/html/latest/", "1-2 weeks",
                "Shell scripting (bash), systemd service management, permissions (chmod/chown), networking",
                List.of("Server health check and backup bash daemon")),
                "linux/unix", "unix", "bash", "shell scripting");

        register(new SkillMeta("maven", "Maven", "DEV_TOOLS", "https://maven.apache.org/guides/", "1 week",
                "POM inheritance, build lifecycle phases, dependency management, plugins",
                List.of("Multi-module Java enterprise build configuration")),
                "apache maven");

        register(new SkillMeta("junit", "Unit Testing / JUnit", "DEV_TOOLS", "https://junit.org/junit5/docs/current/user-guide/", "1 week",
                "JUnit 5, Mockito mocking, assertions, parameterized tests, test-driven development",
                List.of("100% test coverage suite for core business rules")),
                "unit testing", "junit 5", "mockito", "tdd", "test driven development");

        register(new SkillMeta("rest_api", "REST APIs", "BACKEND", "https://restfulapi.net/", "1 week",
                "HTTP methods, idempotency, status codes, payload design, pagination, error responses",
                List.of("Standard REST API with RFC 7807 problem details")),
                "rest", "restful", "restful api", "restful apis");

        register(new SkillMeta("microservices", "Microservices", "BACKEND", "https://microservices.io/", "2-3 weeks",
                "Service discovery, circuit breakers, event-driven architecture, distributed tracing",
                List.of("Event-driven order & payment microservices with RabbitMQ/Kafka")),
                "microservice architecture", "distributed systems");

        register(new SkillMeta("graphql", "GraphQL", "BACKEND", "https://graphql.org/learn/", "1-2 weeks",
                "Schema definition, queries, mutations, subscriptions, dataloader pattern",
                List.of("Federated social feed GraphQL endpoint")),
                "apollo graphql", "graphql api");

        // Soft Skills
        register(new SkillMeta("problem_solving", "Problem Solving", "SOFT_SKILL", "https://en.wikipedia.org/wiki/Problem_solving", "Ongoing",
                "Analytical thinking, debugging under pressure, root cause analysis",
                List.of("Document post-mortem analysis of production bug")),
                "analytical thinking", "critical thinking");

        register(new SkillMeta("team_leadership", "Team Leadership", "SOFT_SKILL", "https://en.wikipedia.org/wiki/Leadership", "Ongoing",
                "Mentorship, code reviews, technical architecture guidance, sprint management",
                List.of("Lead engineering design review sessions")),
                "leadership", "mentoring", "technical leadership");

        register(new SkillMeta("communication", "Communication", "SOFT_SKILL", "https://en.wikipedia.org/wiki/Communication", "Ongoing",
                "Technical writing, stakeholder presentations, cross-functional collaboration",
                List.of("Author clear RFCs and architecture decision records (ADRs)")),
                "collaboration", "cross-functional collaboration", "interpersonal skills");

        register(new SkillMeta("agile_scrum", "Agile / Scrum", "SOFT_SKILL", "https://scrumguides.org/", "Ongoing",
                "Sprint planning, daily standups, backlog refinement, sprint retrospectives",
                List.of("Facilitate sprint planning and velocity tracking")),
                "agile", "scrum", "kanban", "sprint planning");
    }

    public Optional<SkillMeta> findByAliasOrName(String term) {
        if (term == null) return Optional.empty();
        String normalized = term.trim().toLowerCase();
        String canonicalKey = aliasToCanonical.get(normalized);
        if (canonicalKey != null) {
            return Optional.ofNullable(canonicalSkills.get(canonicalKey));
        }
        return Optional.empty();
    }

    public Map<String, SkillMeta> getAllCanonicalSkills() {
        return Collections.unmodifiableMap(canonicalSkills);
    }

    public Set<SkillMeta> extractSkillsFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptySet();
        }

        Set<SkillMeta> detected = new LinkedHashSet<>();
        String lowerText = " " + text.toLowerCase().replaceAll("[^a-z0-9#+./\\-_]", " ") + " ";

        // Check each alias with word boundary awareness
        for (Map.Entry<String, String> entry : aliasToCanonical.entrySet()) {
            String alias = entry.getKey();
            String patternStr = "(?:^|[^a-zA-Z0-9#+])" + Pattern.quote(alias) + "(?:$|[^a-zA-Z0-9#+])";
            Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            if (p.matcher(lowerText).find()) {
                SkillMeta meta = canonicalSkills.get(entry.getValue());
                if (meta != null) {
                    detected.add(meta);
                }
            }
        }

        return detected;
    }
}
