package com.myapp.repository.helper;

import com.myapp.domain.*;
import com.myapp.dto.UserStats;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

import java.util.Optional;

public class UserStatsQueryHelper {

    public static ConstructorExpression<UserStats> userStatsExpression(QUser qUser, User currentUser) {
        return Projections.constructor(UserStats.class,
                cntPostsQuery(qUser),
                cntFollowingsQuery(qUser),
                cntFollowersQuery(qUser),
                isFollowedByMeQuery(qUser, currentUser),
                isMyselfQuery(qUser, currentUser)
        );
    }

    private static JPQLQuery<Boolean> isFollowedByMeQuery(QUser qUser, User currentUser) {
        final QRelationship qRelationship = new QRelationship("relationship_is_followed_by_me");
        return JPAExpressions.select(qRelationship.count().eq(1L))
                .from(qRelationship)
                .where(qRelationship.followed.eq(qUser)
                        .and(Optional.ofNullable(currentUser)
                                .map(qRelationship.follower::eq)
                                .orElse(qRelationship.ne(qRelationship)) // make it always false when no current user
                        )
                );
    }

    private static JPQLQuery<Boolean> isMyselfQuery(QUser qUser, User currentUser) {
        final BooleanExpression expr = Optional.ofNullable(currentUser)
                .map(qUser::eq)
                .orElse(qUser.ne(qUser)); // make it always false when no current user
        return JPAExpressions.select(expr)
                .from(QDual.dual)
                ;
    }

    private static JPQLQuery<Long> cntFollowersQuery(QUser qUser) {
        final QRelationship qRelationship = new QRelationship("relationship_cnt_followers");
        return JPAExpressions.select(qRelationship.count())
                .from(qRelationship)
                .where(qRelationship.followed.eq(qUser));
    }

    private static JPQLQuery<Long> cntFollowingsQuery(QUser qUser) {
        final QRelationship qRelationship = new QRelationship("relationship_cnt_followings");
        return JPAExpressions.select(qRelationship.count())
                .from(qRelationship)
                .where(qRelationship.follower.eq(qUser));
    }

    private static JPQLQuery<Long> cntPostsQuery(QUser qUser) {
        final QMicropost qMicropost = new QMicropost("micropost_cnt_posts");
        return JPAExpressions.select(qMicropost.count())
                .from(qMicropost)
                .where(qMicropost.user.eq(qUser));
    }
}
