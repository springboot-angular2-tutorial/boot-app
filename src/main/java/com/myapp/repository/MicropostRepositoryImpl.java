package com.myapp.repository;

import com.myapp.domain.*;
import com.myapp.dto.PostDTO;
import com.myapp.dto.UserStats;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class MicropostRepositoryImpl implements MicropostRepositoryCustom {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(MicropostRepositoryImpl.class);

    private final JPAQueryFactory queryFactory;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    public MicropostRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<PostDTO> findAsFeed(User user,
                                    Optional<Long> sinceId,
                                    Optional<Long> maxId,
                                    Integer maxSize) {
        final QMicropost qMicropost = QMicropost.micropost;
        final QUser qUser = QUser.user;
        final QRelationship qRelationship = QRelationship.relationship;

        final JPQLQuery<Relationship> relationshipSubQuery = JPAExpressions.selectFrom(qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(qRelationship.followed.eq(qMicropost.user))
                );

        final QMicropost qMicropost2 = new QMicropost("micropost1");
        final JPQLQuery<Long> cntPostsQuery = JPAExpressions.select(qMicropost2.count())
                .from(qMicropost2)
                .where(qMicropost2.user.eq(qMicropost.user));

        final QRelationship qRelationship1 = new QRelationship("relationship1");
        final JPQLQuery<Long> cntFollowersQuery = JPAExpressions.select(qRelationship1.count())
                .from(qRelationship1)
                .where(qRelationship1.followed.eq(qMicropost.user));

        final QRelationship qRelationship2 = new QRelationship("relationship2");
        final JPQLQuery<Long> cntFollowingsQuery = JPAExpressions.select(qRelationship2.count())
                .from(qRelationship2)
                .where(qRelationship2.follower.eq(qMicropost.user));

        final QRelationship qRelationship3 = new QRelationship("relationship3");
        final JPQLQuery<Long> isFollowedByMeQuery = JPAExpressions.select(qRelationship3.count())
                .from(qRelationship3)
                .where(qRelationship3.followed.eq(qMicropost.user)
                        .and(qRelationship3.follower.eq(user)));

        final ConstructorExpression<UserStats> expression = Projections.constructor(UserStats.class,
                cntPostsQuery,
                cntFollowingsQuery,
                cntFollowersQuery,
                isFollowedByMeQuery
        );
        final List<Tuple> intermediatePosts = queryFactory
                .select(qMicropost, qMicropost.user, expression)
                .from(qMicropost)
                .innerJoin(qMicropost.user)
                .where((qMicropost.user.eq(user).or(relationshipSubQuery.exists()))
                        .and(sinceId.map(qMicropost.id::gt).orElse(null))
                        .and(maxId.map(qMicropost.id::lt).orElse(null))
                )
                .orderBy(qMicropost.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();

        final List<PostDTO> posts = new ArrayList<>();
        for (Tuple row : intermediatePosts) {
            PostDTO post = PostDTO.builder()
                    .micropost(row.get(qMicropost))
                    .user(row.get(qMicropost.user))
                    .userStats(row.get(expression))
                    .build();
            posts.add(post);
        }

        return posts;
    }

    @Override
    public List<Micropost> findByUser(User user,
                                      Optional<Long> sinceId,
                                      Optional<Long> maxId,
                                      Integer maxSize) {
        final QMicropost qMicropost = QMicropost.micropost;
        return queryFactory.selectFrom(qMicropost)
                .where(qMicropost.user.eq(user)
                        .and(sinceId.map(qMicropost.id::gt).orElse(null))
                        .and(maxId.map(qMicropost.id::lt).orElse(null))
                )
                .orderBy(qMicropost.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();
    }
}
