package com.myapp.repository;

import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserStats;
import com.myapp.repository.helper.UserStatsQueryHelper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QUser qUser = QUser.user;
    private final QRelationship qRelationship = QRelationship.relationship;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RelatedUserDTO> findFollowings(User user, User currentUser, PageParams pageParams) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);

        return queryFactory.select(qUser, qRelationship, userStatsExpression)
                .from(qUser)
                .innerJoin(qUser.followedRelations, qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(pageParams.getSinceId().map(qRelationship.id::gt).orElse(null))
                        .and(pageParams.getMaxId().map(qRelationship.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(pageParams.getCount())
                .fetch()
                .stream()
                .map(row -> RelatedUserDTO.builder()
                        .user(row.get(qUser))
                        .relationship(row.get(qRelationship))
                        .userStats(row.get(userStatsExpression))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user, User currentUser, PageParams pageParams) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);

        return queryFactory.select(qUser, qRelationship, userStatsExpression)
                .from(qUser)
                .innerJoin(qUser.followerRelations, qRelationship)
                .where(qRelationship.followed.eq(user)
                        .and(pageParams.getSinceId().map(qRelationship.id::gt).orElse(null))
                        .and(pageParams.getMaxId().map(qRelationship.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(pageParams.getCount())
                .fetch()
                .stream()
                .map(row -> RelatedUserDTO.builder()
                        .user(row.get(qUser))
                        .relationship(row.get(qRelationship))
                        .userStats(row.get(userStatsExpression))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findOne(Long userId, User currentUser) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);
        final Tuple row = queryFactory.select(qUser, userStatsExpression)
                .from(qUser)
                .where(qUser.id.eq(userId))
                .fetchOne();
        if (row == null) return null;
        return UserDTO.builder()
                .user(row.get(qUser))
                .userStats(row.get(userStatsExpression))
                .build();
    }

}
