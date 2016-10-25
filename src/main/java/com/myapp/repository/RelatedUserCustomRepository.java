package com.myapp.repository;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.UserStats;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

public interface RelatedUserCustomRepository extends Repository<User, Long> {

    Stream<Row> findFollowings(User user, User currentUser, PageParams pageParams);

    Stream<Row> findFollowers(User user, User currentUser, PageParams pageParams);

    @Value
    @Builder
    class Row {
        private final User user;
        private final Relationship relationship;
        private final UserStats userStats;
    }
}
