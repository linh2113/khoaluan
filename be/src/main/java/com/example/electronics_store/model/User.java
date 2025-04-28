package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Boolean role;
    
    @Column(name = "user_name")
    private String userName;
    
    private String password;
    
    private String phone;
    
    private String email;
    
    @Column(name = "sur_name")
    private String surName;
    
    @Column(name = "last_name")
    private String lastName;
    
    private String hash;
    
    private Integer active;
    
    private String picture;
    
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;
    
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    
    @Column(name = "login_times")
    private Integer loginTimes;
    
    @Column(name = "login_by")
    private Integer loginBy;
    
    @Column(name = "lock_fail")
    private Integer lockFail;
    
    private String address;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    private String gender;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
