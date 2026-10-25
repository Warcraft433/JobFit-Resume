package com.jobfitresume.achievement;

import com.jobfitresume.achievement.dto.AchievementDto;
import com.jobfitresume.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AchievementDto>>> getAchievements() {
        return ResponseEntity.ok(ApiResponse.ok(achievementService.getAchievements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AchievementDto>> getAchievementById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(achievementService.getAchievementById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AchievementDto>> createAchievement(@Valid @RequestBody AchievementDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Achievement added", achievementService.createAchievement(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AchievementDto>> updateAchievement(
            @PathVariable UUID id,
            @Valid @RequestBody AchievementDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Achievement updated", achievementService.updateAchievement(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAchievement(@PathVariable UUID id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.ok(ApiResponse.ok("Achievement deleted", null));
    }
}
