package com.jobfitresume.auth;

import com.jobfitresume.auth.dto.*;
import com.jobfitresume.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(
            AuthService authService,
            @Value("${app.cookie.secure:false}") boolean cookieSecure,
            @Value("${app.cookie.same-site:Lax}") String cookieSameSite) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        attachTokenCookie(response, authResponse.getToken(), 86400);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        attachTokenCookie(response, authResponse.getToken(), 86400);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        attachTokenCookie(response, "", 0);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully", null));
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(HttpServletResponse response) {
        authService.deleteAccount();
        attachTokenCookie(response, "", 0);
        return ResponseEntity.ok(ApiResponse.ok("Account deleted successfully", null));
    }

    private void attachTokenCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from("jobfit_token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite(cookieSameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
