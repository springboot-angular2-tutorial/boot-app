package com.myapp.repository;

import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserStats;
import com.myapp.repository.helper.UserStatsQueryHelper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RelatedUserCustomRepositoryImpl implements RelatedUserCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QUser qUser = QUser.user;
    private final QRelationship qRelationship = QRelationship.relationship;

    @Autowired
    public RelatedUserCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RelatedUserDTO> findFollowings(User subject, User currentUser, PageParams pageParams) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);

        return queryFactory.select(qUser, qRelationship, userStatsExpression)
                .from(qUser)
                .innerJoin(qUser.followedRelations, qRelationship)
                .where(qRelationship.follower.eq(subject)
                        .and(pageParams.getSinceId().map(qRelationship.id::gt).orElse(null))
                        .and(pageParams.getMaxId().map(qRelationship.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(pageParams.getCount())
                .fetch()
                .stream()
                .map(mapRowToRelatedUserDTO(currentUser, userStatsExpression))
                .collect(Collectors.toList());
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User subject, User currentUser, PageParams pageParams) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);

        return queryFactory.select(qUser, qRelationship, userStatsExpression)
                .from(qUser)
                .innerJoin(qUser.followerRelations, qRelationship)
                .where(qRelationship.followed.eq(subject)
                        .and(pageParams.getSinceId().map(qRelationship.id::gt).orElse(null))
                        .and(pageParams.getMaxId().map(qRelationship.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(pageParams.getCount())
                .fetch()
                .stream()
                .map(mapRowToRelatedUserDTO(currentUser, userStatsExpression))
                .collect(Collectors.toList());
    }

    private Function<Tuple, RelatedUserDTO> mapRowToRelatedUserDTO(User currentUser, ConstructorExpression<UserStats> userStatsExpression) {
        return row -> {
            final User user = row.get(qUser);
            final Relationship relationship = row.get(qRelationship);
            final UserStats userStats = row.get(userStatsExpression);
            assert user != null; // Row was found. It never be null.
            final Boolean isMyself = Optional.ofNullable(currentUser)
                    .map(user::equals)
                    .orElse(null);
            return RelatedUserDTO.newInstance(user, relationship, userStats, isMyself);
        };
    }

}
