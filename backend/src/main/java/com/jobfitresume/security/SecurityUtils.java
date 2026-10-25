package com.jobfitresume.security;

import com.jobfitresume.common.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return Optional.empty();
        }
        return Optional.of((UserPrincipal) authentication.getPrincipal());
    }

    public static UUID getCurrentUserId() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getId)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user context is required"));
    }

    public static String getCurrentUserEmail() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getUsername)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user context is required"));
    }
}
