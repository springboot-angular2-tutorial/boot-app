package com.myapp.repository;

import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserStats;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QUser qUser = QUser.user;
    private final QRelationship qRelationship = QRelationship.relationship;
    private final Function<User, JPQLQuery<Long>> isFollowedByUserQuery = (User u) ->
            JPAExpressions
                    .select(qRelationship.count())
                    .from(qRelationship)
                    .where(qRelationship.follower.eq(u)
                            .and(qRelationship.followed.eq(qUser))
                    );

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
        final JPQLQuery<Long> isFollowedByMeQuery = isFollowedByUserQuery.apply(currentUser);
        final List<Tuple> followings = queryFactory
                .select(qUser, qRelationship, isFollowedByMeQuery)
                .from(qUser)
                .innerJoin(qUser.followedRelations, qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(sinceId.map(qUser.id::gt).orElse(null))
                        .and(maxId.map(qUser.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();

        return getUserDTOs(isFollowedByMeQuery, followings);
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user,
                                              User currentUser,
                                              Optional<Long> sinceId,
                                              Optional<Long> maxId,
                                              Integer maxSize) {
        final JPQLQuery<Long> isFollowedByMeQuery = isFollowedByUserQuery.apply(currentUser);
        final List<Tuple> followers = queryFactory
                .select(qUser, qRelationship, isFollowedByMeQuery)
                .from(qUser)
                .innerJoin(qUser.followerRelations, qRelationship)
                .where(qRelationship.followed.eq(user)
                        .and(sinceId.map(qUser.id::gt).orElse(null))
                        .and(maxId.map(qUser.id::lt).orElse(null))
                )
                .orderBy(qRelationship.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();

        return getUserDTOs(isFollowedByMeQuery, followers);
    }

    private List<RelatedUserDTO> getUserDTOs(JPQLQuery<Long> isFollowedByMeQuery, List<Tuple> followings) {
        final List<RelatedUserDTO> list = new ArrayList<>();
        for (Tuple row : followings) {
            @SuppressWarnings("ConstantConditions")
            final boolean isFollowedByMe = row.get(isFollowedByMeQuery) == 1;
            final UserStats userStats = UserStats.builder()
                    .isFollowedByMe(isFollowedByMe)
                    .build();
            final RelatedUserDTO relatedUserDTO = RelatedUserDTO.builder()
                    .user(row.get(qUser))
                    .relationship(row.get(qRelationship))
                    .userStats(userStats)
                    .build();
            list.add(relatedUserDTO);
        }
        return list;
    }

}
