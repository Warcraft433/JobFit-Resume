package com.jobfitresume;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class JobfitResumeApplicationTests {

    @Test
    void contextLoadsAndFlywayExecutes() {
        // Verifies Spring context, Flyway migration execution, and schema creation
    }
}
