package com.example.electronics_store.controller;

import com.example.electronics_store.dto.*;
import com.example.electronics_store.security.JwtTokenProvider;
import com.example.electronics_store.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(UserService userService,
                         JwtTokenProvider jwtTokenProvider,
                         UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", userService.registerUser(registrationDTO)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestParam String email) {
        try {
            userService.resetPassword(email);
            return ResponseEntity.ok(ApiResponse.success("Password reset email sent"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody PasswordResetRequestDTO request) {
        try {
            if (!userService.verifyPasswordResetToken(request.getToken())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            // Validate password
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("New password is required"));
            }

            userService.changePassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse<?>> verifyToken(@RequestParam String token) {
        boolean isValid = userService.verifyPasswordResetToken(token);
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("Token is valid"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid or expired token"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestParam String refreshToken) {
        try {
            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid refresh token"));
            }

            // Get username from refresh token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            // Load user details
            org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generate new access token
            String newAccessToken = jwtTokenProvider.generateTokenFromUsername(
                    userDetails.getUsername(),
                    userDetails.getAuthorities());

            // Return new tokens
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .token(newAccessToken)
                    .refreshToken(refreshToken) // Keep the same refresh token
                    .user(userService.getUserByUsername(username))
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestParam String token) {
        try {
            boolean verified = userService.verifyEmail(token);
            if (verified) {
                return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Email verification failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/oauth2/redirect")
    public ResponseEntity<ApiResponse<?>> handleOAuth2Redirect(@RequestParam String token, @RequestParam String refreshToken) {
        try {
            // Validate token
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid token"));
            }

            // Get username from token
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Return user info
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(userService.getUserByUsername(username))
                    .build();

            return ResponseEntity.ok(ApiResponse.success("OAuth2 login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
