package com.myapp.repository;

import com.myapp.domain.*;
import com.myapp.dto.PostDTO;
import com.myapp.dto.UserStats;
import com.myapp.repository.helper.UserStatsQueryHelper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstructorExpression;
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
        final QRelationship qRelationship = QRelationship.relationship;

        final ConstructorExpression<UserStats> userStatsExpression =
                UserStatsQueryHelper.userStatsExpression(qMicropost.user, user);
        final JPQLQuery<Relationship> relationshipSubQuery = JPAExpressions
                .selectFrom(qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(qRelationship.followed.eq(qMicropost.user))
                );
        final List<Tuple> intermediatePosts = queryFactory
                .select(qMicropost, qMicropost.user, userStatsExpression)
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
                    .userStats(row.get(userStatsExpression))
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
