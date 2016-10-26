package com.myapp.repository;

import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    Optional<Relationship> findOneByFollowerAndFollowed(User follower, User followed);

}
