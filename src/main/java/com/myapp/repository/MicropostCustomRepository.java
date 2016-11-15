package com.myapp.repository;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.domain.UserStats;
import com.myapp.dto.PageParams;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface MicropostCustomRepository extends Repository<Micropost, Long> {

    List<Row> findAsFeed(User user, PageParams pageParams);

    List<Row> findByUser(User user, PageParams pageParams);

    @Value
    @Builder
    class Row {
        private final Micropost micropost;
        private final UserStats userStats;
    }

}
