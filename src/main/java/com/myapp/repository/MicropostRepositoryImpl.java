package com.myapp.repository;

import com.myapp.domain.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class MicropostRepositoryImpl implements MicropostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    public MicropostRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Micropost> findAsFeed(User user,
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

        return queryFactory.selectFrom(qMicropost)
                .innerJoin(qMicropost.user)
                .fetchJoin()
                .where((qMicropost.user.eq(user).or(relationshipSubQuery.exists()))
                        .and(sinceId.map(qMicropost.id::gt).orElse(null))
                        .and(maxId.map(qMicropost.id::lt).orElse(null))
                )
                .orderBy(qMicropost.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();
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
