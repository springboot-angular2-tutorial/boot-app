package com.myapp.repository;

import com.myapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByUsername(String username);

    Page<User> findFollowings(@Param("user") User user, Pageable pageable);

    Page<User> findFollowers(@Param("user") User user, Pageable pageable);
}
