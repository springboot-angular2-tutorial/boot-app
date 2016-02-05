package com.myapp.repository;

import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserStats;
import com.myapp.repository.helper.UserStatsQueryHelper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<RelatedUserDTO> findFollowings(User user,
                                               User currentUser,
                                               Optional<Long> sinceId,
                                               Optional<Long> maxId,
                                               Integer maxSize) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);
        final List<Tuple> followings = queryFactory
                .select(qUser, qRelationship, userStatsExpression)
                .from(qUser)
                .innerJoin(qUser.followedRelations, qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(sinceId.map(qRelationship.id::gt).orElse(null))
                        .and(maxId.map(qRelationship.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();

        return getRelatedUserDTOs(userStatsExpression, followings);
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user,
                                              User currentUser,
                                              Optional<Long> sinceId,
                                              Optional<Long> maxId,
                                              Integer maxSize) {
        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qUser, currentUser);
        final List<Tuple> followers = queryFactory
                .select(qUser, qRelationship, userStatsExpression)
                .from(qUser)
                .innerJoin(qUser.followerRelations, qRelationship)
                .where(qRelationship.followed.eq(user)
                        .and(sinceId.map(qRelationship.id::gt).orElse(null))
                        .and(maxId.map(qRelationship.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();

        return getRelatedUserDTOs(userStatsExpression, followers);
    }

    private List<RelatedUserDTO> getRelatedUserDTOs(ConstructorExpression<UserStats> userStatsExpression, List<Tuple> followings) {
        final List<RelatedUserDTO> list = new ArrayList<>();
        for (Tuple row : followings) {
            final RelatedUserDTO relatedUserDTO = RelatedUserDTO.builder()
                    .user(row.get(qUser))
                    .relationship(row.get(qRelationship))
                    .userStats(row.get(userStatsExpression))
                    .build();
            list.add(relatedUserDTO);
        }
        return list;
    }

}
