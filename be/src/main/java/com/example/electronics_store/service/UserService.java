package com.example.electronics_store.service;

import com.example.electronics_store.dto.LoginRequestDTO;
import com.example.electronics_store.dto.LoginResponseDTO;
import com.example.electronics_store.dto.UserDTO;
import com.example.electronics_store.dto.UserRegistrationDTO;
import com.example.electronics_store.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(UserRegistrationDTO registrationDTO);
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserDTO getUserById(Integer id);
    
    UserDTO getUserByUsername(String username);

    Page<UserDTO> getUsersWithFilters(Boolean role, String search, Pageable pageable);

    List<UserDTO> getAllUsers();
    
    List<UserDTO> getAllAdmins();
    
    List<UserDTO> getAllCustomers();
    
    UserDTO updateUser(Integer id, UserDTO userDTO);

    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    void resetPassword(String email);
    
    boolean verifyPasswordResetToken(String token);

    boolean verifyEmail(String token);

    void changePassword(String token, String newPassword);
    String uploadAvatar(Integer id, MultipartFile file);
    Optional<User> getUserEntityById(Integer id);
}
