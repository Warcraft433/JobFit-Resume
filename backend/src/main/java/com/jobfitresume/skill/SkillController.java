package com.jobfitresume.skill;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.skill.dto.UserSkillDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<Skill>>> getCanonicalSkills() {
        return ResponseEntity.ok(ApiResponse.ok(skillService.getAllCanonicalSkills()));
    }

    @GetMapping("/profile/skills")
    public ResponseEntity<ApiResponse<List<UserSkillDto>>> getUserSkills() {
        return ResponseEntity.ok(ApiResponse.ok(skillService.getUserSkills()));
    }

    @PostMapping("/profile/skills")
    public ResponseEntity<ApiResponse<UserSkillDto>> addUserSkill(@Valid @RequestBody UserSkillDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Skill added to profile", skillService.addUserSkill(dto)));
    }

    @PutMapping("/profile/skills/{id}")
    public ResponseEntity<ApiResponse<UserSkillDto>> updateUserSkill(
            @PathVariable UUID id,
            @Valid @RequestBody UserSkillDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Skill updated", skillService.updateUserSkill(id, dto)));
    }

    @DeleteMapping("/profile/skills/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserSkill(@PathVariable UUID id) {
        skillService.deleteUserSkill(id);
        return ResponseEntity.ok(ApiResponse.ok("Skill removed from profile", null));
    }
}
