package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.UserDTO;
import com.example.electronics_store.model.User;
import com.example.electronics_store.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService,PasswordEncoder passwordEncoder ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    //get info user by id by pathvariable
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    //get userProfile from requestparam
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getUserProfile(@RequestParam Integer userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    //change in4 user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO userDTO) {
        try {
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", userService.updateUser(id, userDTO)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    //change avt by user
    @PostMapping("/{id}/upload-avatar")
    public ResponseEntity<ApiResponse<?>> uploadAvatar(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.uploadAvatar(id, file);
            return ResponseEntity.ok(ApiResponse.success("Avatar uploaded successfully", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

  @PostMapping("/{id}/change-password")
public ResponseEntity<ApiResponse<?>> initiatePasswordChange(
        @PathVariable Integer id,
        @RequestBody Map<String, String> request) {
    try {
        Optional<User> userOpt = userService.getUserEntityById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }

        String currentPassword = request.get("currentPassword");
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Current password is required"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Mật khẩu hiện tại không đúng"));
        }

        userService.resetPassword(user.getEmail());

        return ResponseEntity.ok(ApiResponse.success(
                "Password change verification email sent. Please check your email."
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
}



}
