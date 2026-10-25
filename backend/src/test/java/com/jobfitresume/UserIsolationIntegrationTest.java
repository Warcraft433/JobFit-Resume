package com.jobfitresume;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfitresume.auth.dto.LoginRequest;
import com.jobfitresume.auth.dto.RegisterRequest;
import com.jobfitresume.job.dto.JobDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class UserIsolationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String registerAndGetToken(String name, String email, String password) throws Exception {
        RegisterRequest req = new RegisterRequest(name, email, password);
        MvcResult res = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();
        String json = res.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data").path("token").asText();
    }

    @Test
    void testCrossUserDataIsolation() throws Exception {
        // User A creates a job
        String tokenA = registerAndGetToken("User A", "userA@test.com", "PasswordA123!");
        String tokenB = registerAndGetToken("User B", "userB@test.com", "PasswordB123!");

        JobDto jobDto = new JobDto();
        jobDto.setCompany("Stark Industries");
        jobDto.setJobTitle("Lead AI Architect");
        jobDto.setJobDescription("Build autonomous iron defense systems in Java and Python.");

        MvcResult createJobResult = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andReturn();

        String jobJson = createJobResult.getResponse().getContentAsString();
        String jobId = objectMapper.readTree(jobJson).path("data").path("id").asText();

        // User A can access the job
        mockMvc.perform(get("/api/v1/jobs/" + jobId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company").value("Stark Industries"));

        // User B cannot access User A's job (must return 404 Not Found)
        mockMvc.perform(get("/api/v1/jobs/" + jobId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // User B cannot delete User A's job
        mockMvc.perform(delete("/api/v1/jobs/" + jobId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // User B's job list does not contain User A's job
        mockMvc.perform(get("/api/v1/jobs")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
