package com.example.electronics_store.repository;

import com.example.electronics_store.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByHash(String hash);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.role = true")
    List<User> findAllAdmins();

    @Query("SELECT u FROM User u WHERE u.role = false")
    List<User> findAllCustomers();

    @Query("SELECT u FROM User u WHERE u.active = 1")
    List<User> findAllActiveUsers();

    @Modifying
    @Query("UPDATE User u SET u.loginTimes = u.loginTimes + 1 WHERE u.id = :id")
    void incrementLoginTimes(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE User u SET u.lockFail = u.lockFail + 1 WHERE u.id = :id")
    void incrementLockFail(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE User u SET u.lockFail = 0 WHERE u.id = :id")
    void resetLockFail(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(@Param("id") Integer id, @Param("active") Integer active);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.hash = :hash WHERE u.id = :id")
    void updateHash(@Param("id") Integer id, @Param("hash") String hash);



}
