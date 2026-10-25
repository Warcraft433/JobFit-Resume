package com.jobfitresume.profile;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.profile.dto.ProfileCompletenessResponse;
import com.jobfitresume.profile.dto.UserProfileDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile() {
        return ResponseEntity.ok(ApiResponse.ok(userProfileService.getProfile()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@Valid @RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", userProfileService.updateProfile(dto)));
    }

    @GetMapping("/completeness")
    public ResponseEntity<ApiResponse<ProfileCompletenessResponse>> getCompleteness() {
        return ResponseEntity.ok(ApiResponse.ok(userProfileService.getCompleteness()));
    }
}
