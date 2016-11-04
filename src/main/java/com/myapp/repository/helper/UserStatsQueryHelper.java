package com.myapp.repository.helper;

import com.myapp.domain.QMicropost;
import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.UserStats;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

public class UserStatsQueryHelper {

    public static ConstructorExpression<UserStats> userStatsExpression(QUser qUser) {
        return Projections.constructor(UserStats.class,
                cntPostsQuery(qUser),
                cntFollowingsQuery(qUser),
                cntFollowersQuery(qUser)
        );
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
