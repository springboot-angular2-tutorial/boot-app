package com.myapp.repository;

import com.myapp.domain.Micropost;
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
public class MicropostRepositoryImpl implements MicropostRepositoryCustom {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Micropost> findAsFeed(User user,
                                      Optional<Long> sinceId,
                                      Optional<Long> maxId,
                                      Integer maxSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Micropost> query = cb.createQuery(Micropost.class);

        Root<Micropost> root = query.from(Micropost.class);
        root.fetch("user");

        Subquery<Relationship> relationshipSubquery = query.subquery(Relationship.class);
        Root<Relationship> relationshipRoot = relationshipSubquery.from(Relationship.class);
        relationshipSubquery.where(
                cb.equal(relationshipRoot.get("followed"), root.get("user")),
                cb.equal(relationshipRoot.get("follower"), user)
        );
        relationshipSubquery.select(relationshipRoot);

        query.where(
                cb.or(
                        cb.equal(root.get("user"), user),
                        cb.exists(relationshipSubquery)
                ),
                sinceId.map(id -> cb.and(cb.greaterThan(root.get("id"), id)))
                        .orElse(cb.conjunction()),
                maxId.map(id -> cb.and(cb.lessThan(root.get("id"), id)))
                        .orElse(cb.conjunction())
        );
        query.orderBy(cb.desc(root.get("id")));

        return entityManager
                .createQuery(query)
                .setMaxResults(Optional.ofNullable(maxSize).orElse(20))
                .getResultList();
    }
}
