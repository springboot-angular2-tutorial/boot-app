package com.myapp.repository;

import com.myapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByUsername(String username);

    @Query("select u from User u left join u.followedRelations f where f.follower = :user and u in :targetUsers")
    List<User> findFollowedBy(@Param("user") User user, @Param("targetUsers") List<User> targetUsers);

}
