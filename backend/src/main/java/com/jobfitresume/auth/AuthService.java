package com.jobfitresume.auth;

import com.jobfitresume.auth.dto.*;
import com.jobfitresume.common.BadRequestException;
import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.profile.UserProfile;
import com.jobfitresume.profile.UserProfileRepository;
import com.jobfitresume.security.JwtTokenProvider;
import com.jobfitresume.security.SecurityUtils;
import com.jobfitresume.security.UserPrincipal;
import com.jobfitresume.user.User;
import com.jobfitresume.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with this email address already exists.");
        }

        User user = new User(
                request.getFullName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.getPassword())
        );
        User savedUser = userRepository.save(user);

        // Automatically create an initial empty user profile
        UserProfile profile = new UserProfile(savedUser.getId());
        profile.setContactEmail(savedUser.getEmail());
        userProfileRepository.save(profile);

        String token = tokenProvider.generateTokenForUser(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName());
        return new AuthResponse(token, UserResponse.fromUser(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(token, UserResponse.fromUser(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromUser(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        userRepository.deleteById(currentUserId);
    }
}
