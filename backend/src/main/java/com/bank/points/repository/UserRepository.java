package com.bank.points.repository;

import com.bank.points.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByPhone(String phone);

    User findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    List<User> findByUsernameContaining(String username);

    List<User> findByPhoneContaining(String phone);

    List<User> findByMemberLevel(Integer memberLevel);

    List<User> findByStatus(Integer status);
}
