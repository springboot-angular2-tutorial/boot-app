package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.domain.UserStats;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserCustomRepository extends Repository<User, Long> {

    Optional<Row> findOne(Long userId);

    @Value
    @Builder
    class Row {
        private final User user;
        private final UserStats userStats;
    }
}
