package com.myapp.repository;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface RelationshipRepository extends JpaRepository<Relationship, Long>, QueryDslPredicateExecutor<Relationship> {

    Optional<Relationship> findOneByFollowerAndFollowed(User follower, User followed);

    Stream<Relationship> findAllByFollowerAndFollowedIn(User follower, List<User> targets);

}
