package com.myapp.repository;

import com.myapp.domain.QRelationship;
import com.myapp.domain.QUser;
import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<User> findFollowings(User user,
                                     Optional<Long> sinceId,
                                     Optional<Long> maxId,
                                     Integer maxSize) {
        final QUser qUser = QUser.user;
        final QRelationship qRelationship = QRelationship.relationship;

        final JPQLQuery<Relationship> relationshipSubQuery = JPAExpressions.selectFrom(qRelationship)
                .where(qRelationship.follower.eq(user)
                        .and(qRelationship.followed.eq(qUser))
                );
        return queryFactory.selectFrom(qUser)
                .where(relationshipSubQuery.exists()
                        .and(sinceId.map(qUser.id::gt).orElse(null))
                        .and(maxId.map(qUser.id::lt).orElse(null))
                )
                .orderBy(qUser.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();
    }

    @Override
    public List<User> findFollowers(User user,
                                    Optional<Long> sinceId,
                                    Optional<Long> maxId,
                                    Integer maxSize) {
        final QUser qUser = QUser.user;
        final QRelationship qRelationship = QRelationship.relationship;

        final JPQLQuery<Relationship> relationshipSubQuery = JPAExpressions.selectFrom(qRelationship)
                .where(qRelationship.follower.eq(qUser)
                        .and(qRelationship.followed.eq(user))
                );
        return queryFactory.selectFrom(qUser)
                .where(relationshipSubQuery.exists()
                        .and(sinceId.map(qUser.id::gt).orElse(null))
                        .and(maxId.map(qUser.id::lt).orElse(null))
                )
                .orderBy(qUser.id.desc())
                .limit(Optional.ofNullable(maxSize).orElse(20))
                .fetch();
    }

}
