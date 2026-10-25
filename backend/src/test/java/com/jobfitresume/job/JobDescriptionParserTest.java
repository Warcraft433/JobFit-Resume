package com.jobfitresume.job;

import com.jobfitresume.job.parser.JobDescriptionParser;
import com.jobfitresume.job.parser.SkillDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobDescriptionParserTest {

    private JobDescriptionParser parser;
    private SkillDictionary skillDictionary;

    @BeforeEach
    void setUp() {
        skillDictionary = new SkillDictionary();
        parser = new JobDescriptionParser(skillDictionary);
    }

    @Test
    void testParseFullStackJobDescription() {
        String jd = """
                Senior Full Stack Engineer
                Company: TechCorp Innovations
                
                About the role:
                We are seeking a seasoned Senior Full Stack Engineer to build next-generation web platforms.
                
                Responsibilities:
                • Architect scalable microservices in Java and Spring Boot.
                • Develop responsive user interfaces using React, TypeScript, and Tailwind CSS.
                • Optimize PostgreSQL database queries and manage Redis caching.
                • Deploy containerized applications with Docker and Kubernetes to AWS.
                
                Requirements:
                • 5+ years of experience with Java and backend development.
                • Strong proficiency in React, TypeScript, and modern CSS.
                • Deep knowledge of PostgreSQL, SQL, and database indexing.
                • Bachelor's degree in Computer Science or equivalent practical experience.
                
                Preferred / Nice to have:
                • Experience with Next.js and GraphQL.
                • Familiarity with CI/CD pipelines and GitHub Actions.
                """;

        JobDescriptionParser.ParsedJobData parsed = parser.parse(jd);

        assertNotNull(parsed);
        assertEquals(5, parsed.getMinExperienceYears());
        assertTrue(parsed.getDegreeRequirement().contains("Bachelor"));
        assertFalse(parsed.getResponsibilities().isEmpty());

        // Verify skills detected
        boolean hasJava = parsed.getRequiredSkills().stream().anyMatch(s -> "java".equalsIgnoreCase(s.getCanonicalName()));
        boolean hasSpringBoot = parsed.getRequiredSkills().stream().anyMatch(s -> "spring_boot".equalsIgnoreCase(s.getCanonicalName()));
        boolean hasReact = parsed.getRequiredSkills().stream().anyMatch(s -> "react".equalsIgnoreCase(s.getCanonicalName()));
        boolean hasPostgres = parsed.getRequiredSkills().stream().anyMatch(s -> "postgresql".equalsIgnoreCase(s.getCanonicalName()));

        assertTrue(hasJava, "Should detect Java in requirements");
        assertTrue(hasSpringBoot, "Should detect Spring Boot in requirements");
        assertTrue(hasReact, "Should detect React in requirements");
        assertTrue(hasPostgres, "Should detect PostgreSQL in requirements");
    }

    @Test
    void testSkillDictionaryAliases() {
        assertTrue(skillDictionary.findByAliasOrName("k8s").isPresent());
        assertEquals("kubernetes", skillDictionary.findByAliasOrName("k8s").get().getCanonicalName());

        assertTrue(skillDictionary.findByAliasOrName("postgres").isPresent());
        assertEquals("postgresql", skillDictionary.findByAliasOrName("postgres").get().getCanonicalName());

        assertTrue(skillDictionary.findByAliasOrName("reactjs").isPresent());
        assertEquals("react", skillDictionary.findByAliasOrName("reactjs").get().getCanonicalName());
    }
}
