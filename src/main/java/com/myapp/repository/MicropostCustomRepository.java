package com.myapp.repository;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.UserStats;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

public interface MicropostCustomRepository extends Repository<Micropost, Long> {

    Stream<Row> findAsFeed(User user, PageParams pageParams);

    Stream<Row> findByUser(User user, PageParams pageParams);

    @Value
    @Builder
    class Row {
        private final Micropost micropost;
        private final UserStats userStats;
    }

}
