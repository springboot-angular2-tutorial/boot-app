package com.myapp.repository;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.domain.UserStats;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RelatedUserCustomRepository extends Repository<User, Long> {

    List<Row> findFollowings(User user, PageParams pageParams);

    List<Row> findFollowers(User user, PageParams pageParams);

    @Value
    @Builder
    class Row {
        private final User user;
        private final Relationship relationship;
        private final UserStats userStats;
    }
}
