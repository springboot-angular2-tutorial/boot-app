package com.myapp.repository;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
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

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    EntityManager entityManager;

    @Override
    public List<User> findFollowings(User user,
                                     Optional<Long> sinceId,
                                     Optional<Long> maxId,
                                     Integer maxSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> root = query.from(User.class);
        query.select(root);

        Subquery<Relationship> relationshipSubquery = query.subquery(Relationship.class);
        Root<Relationship> relationshipRoot = relationshipSubquery.from(Relationship.class);
        relationshipSubquery.where(
                cb.equal(relationshipRoot.get("followed"), root.get("id")),
                cb.equal(relationshipRoot.get("follower"), user)
        );
        relationshipSubquery.select(relationshipRoot);

        query.where(
                cb.exists(relationshipSubquery),
                sinceId.map(id -> cb.greaterThan(root.get("id"), id))
                        .orElse(cb.conjunction()),
                maxId.map(id -> cb.lessThan(root.get("id"), id))
                        .orElse(cb.conjunction())
        );
        query.orderBy(cb.desc(root.get("id")));

        return entityManager
                .createQuery(query)
                .setMaxResults(Optional.ofNullable(maxSize).orElse(20))
                .getResultList();
    }

    @Override
    public List<User> findFollowers(User user,
                                    Optional<Long> sinceId,
                                    Optional<Long> maxId,
                                    Integer maxSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> root = query.from(User.class);
        query.select(root);

        Subquery<Relationship> relationshipSubquery = query.subquery(Relationship.class);
        Root<Relationship> relationshipRoot = relationshipSubquery.from(Relationship.class);
        relationshipSubquery.where(
                cb.equal(relationshipRoot.get("followed"), user),
                cb.equal(relationshipRoot.get("follower"), root.get("id"))
        );
        relationshipSubquery.select(relationshipRoot);

        query.where(
                cb.exists(relationshipSubquery),
                sinceId.map(id -> cb.greaterThan(root.get("id"), id))
                        .orElse(cb.conjunction()),
                maxId.map(id -> cb.lessThan(root.get("id"), id))
                        .orElse(cb.conjunction())
        );
        query.orderBy(cb.desc(root.get("id")));

        return entityManager
                .createQuery(query)
                .setMaxResults(Optional.ofNullable(maxSize).orElse(20))
                .getResultList();
    }

}
