package com.myapp.repository;

import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.domain.UserStats;
import com.myapp.repository.helper.UserStatsQueryHelper;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public List<Row> findFollowings(User subject, PageParams pageParams) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser);

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
                .map(tuple -> Row.builder()
                        .user(tuple.get(qUser))
                        .relationship(tuple.get(qRelationship))
                        .userStats(tuple.get(userStatsExpression))
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Row> findFollowers(User subject, PageParams pageParams) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser);

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
                .map(tuple -> Row.builder()
                        .user(tuple.get(qUser))
                        .relationship(tuple.get(qRelationship))
                        .userStats(tuple.get(userStatsExpression))
                        .build()
                )
                .collect(Collectors.toList());
    }

}
