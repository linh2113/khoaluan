package com.example.electronics_store.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.electronics_store.dto.LoginRequestDTO;
import com.example.electronics_store.dto.LoginResponseDTO;
import com.example.electronics_store.dto.UserDTO;
import com.example.electronics_store.dto.UserRegistrationDTO;
import com.example.electronics_store.model.User;
import com.example.electronics_store.repository.UserRepository;
import com.example.electronics_store.security.JwtTokenProvider;
import com.example.electronics_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;
    @Autowired
    private Cloudinary cloudinary;
    @Value("${spring.mail.username:noreply@example.com}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if username, email, or phone already exists
        if (userRepository.existsByUserName(registrationDTO.getUserName())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhone(registrationDTO.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setUserName(registrationDTO.getUserName());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmail(registrationDTO.getEmail());
        user.setPhone(registrationDTO.getPhone());
        user.setSurName(registrationDTO.getSurName());
        user.setLastName(registrationDTO.getLastName());
        user.setAddress(registrationDTO.getAddress());
        user.setDateOfBirth(registrationDTO.getDateOfBirth());
        user.setGender(registrationDTO.getGender());
        user.setRole(false); // mặc định là user
        user.setActive(0); // Inactive until email verification
        user.setLoginTimes(0);
        user.setLoginBy(0);
        user.setLockFail(0);

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setHash(verificationToken);

        User savedUser = userRepository.save(user);

        // Send verification email
        sendVerificationEmail(savedUser);

        return mapUserToDTO(savedUser);
    }
    @Transactional
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // Find user by username
            User user = userRepository.findByUserName(loginRequest.getUserName())
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));

            // Check if user is active
            if (user.getActive() != 1) {
                throw new RuntimeException("Account is not active");
            }

            // Authenticate user, if fall -> throw exception
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // reset lockFail
            userRepository.resetLockFail(user.getId());

            // Increment login times
            userRepository.incrementLoginTimes(user.getId());

            // Generate JWT tokens
            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            return LoginResponseDTO.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(mapUserToDTO(user))
                    .build();
        } catch (Exception e) {
            // Handle authentication failure
            // Find username
            User user = userRepository.findByUserName(loginRequest.getUserName()).orElse(null);
            if (user != null) {
                // Increment lock fail count
                userRepository.incrementLockFail(user.getId());
                // Check if account should be locked
                if (user.getLockFail() + 1 >= 5) {
                    userRepository.updateActiveStatus(user.getId(), 0);
                    throw new RuntimeException("Account has been locked due to too many failed login attempts");
                }
            }

            // Throw the original exception with its message
            if (e instanceof RuntimeException) {
                throw e;
            }
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllAdmins() {
        return userRepository.findAllAdmins().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllCustomers() {
        return userRepository.findAllCustomers().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user fields only if they are not null
        if (userDTO.getSurName() != null) {
            user.setSurName(userDTO.getSurName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userDTO.getDateOfBirth());
        }
        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }

        // Only admin can update role and active status
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getActive() != null) {
            user.setActive(userDTO.getActive());
        }

        User updatedUser = userRepository.save(user);
        return mapUserToDTO(updatedUser);
    }



    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        userRepository.updateHash(user.getId(), resetToken);

        // Send email with reset link (implementation depends on your email service)
        sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    @Override
    public boolean verifyPasswordResetToken(String token) {
        return userRepository.findByHash(token).isPresent();
    }

    @Transactional
    @Override
    public boolean verifyEmail(String token) {
        User user = userRepository.findByHash(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        // Check if user is already verified
        if (user.getActive() == 1) {
            return true; // Already verified
        }

        // Activate user account
        user.setActive(1);
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public void changePassword(String token, String newPassword) {
        User user = userRepository.findByHash(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        // Update password
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(newPassword));

        // Clear reset token
        userRepository.updateHash(user.getId(), null);
    }

    @Override
    @Transactional
    public String uploadAvatar(Integer userId, MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "avatars"));
            String imageUrl = uploadResult.get("secure_url").toString();
            user.setPicture(imageUrl);
            userRepository.save(user);

            return imageUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserEntityById(Integer id) {
        return userRepository.findById(id);
    }

    // Helper methods
    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .surName(user.getSurName())
                .lastName(user.getLastName())
                .active(user.getActive())
                .picture(user.getPicture())
                .createAt(user.getCreateAt())
                .loginTimes(user.getLoginTimes())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .build();
    }

    private String generateJwtToken(User user) {
        // Create authorities based on user role
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null && user.getRole()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Generate token
        return jwtTokenProvider.generateTokenFromUsername(user.getUserName(), authorities);
    }

    private String generateRefreshToken(User user) {
        return jwtTokenProvider.generateRefreshTokenFromUsername(user.getUserName());
    }

    private void sendPasswordResetEmail(String email, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            String emailContent = "<html><body>" +
                    "<h2>Password Change Request</h2>" +
                    "<p>Please click the link below to reset your password:</p>" +
                    "<a href='" + resetLink + "'>Change Password</a>" +
                    "<p>If you didn't request a password reset, please ignore this email.</p>" +
                    "</body></html>";

            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private void sendVerificationEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Verify Your Email Address");

            String verificationLink = frontendUrl + "/verify-email?token=" + user.getHash();

            String emailContent = "<html><body>" +
                    "<h2>Email Verification</h2>" +
                    "<p>Thank you for registering! Please click the link below to verify your email address:</p>" +
                    "<a href='" + verificationLink + "'>Verify Email</a>" +
                    "<p>If you didn't create an account, please ignore this email.</p>" +
                    "</body></html>";

            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersWithFilters(Boolean role, String search, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        // Add role filter if provided
        if (role != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("role"), role));
        }

        // Add search filter if provided
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("userName")), searchTerm),
                            cb.like(cb.lower(root.get("email")), searchTerm),
                            cb.like(cb.lower(root.get("phone")), searchTerm),
                            cb.like(cb.lower(root.get("address")), searchTerm),
                            cb.like(cb.lower(root.get("surName")), searchTerm),
                            cb.like(cb.lower(root.get("lastName")), searchTerm),
                            cb.like(cb.lower(root.get("gender")), searchTerm),
                            cb.like(cb.function("DATE_FORMAT", String.class, root.get("dateOfBirth"), cb.literal("%Y-%m-%d")), "%" + search + "%"),
                            cb.like(cb.function("DATE_FORMAT", String.class, root.get("createAt"), cb.literal("%Y-%m-%d %H:%i:%s")), "%" + search + "%"),
                            cb.like(cb.lower(root.get("loginTimes").as(String.class)), searchTerm),
                            cb.like(cb.lower(root.get("active").as(String.class)), searchTerm)
                    )
            );
        }

        // Execute the query with the specification
        Page<User> userPage = userRepository.findAll(spec, pageable);

        // Map to DTOs
        return userPage.map(this::mapUserToDTO);
    }
}
