package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.domain.Micropost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@SuppressWarnings("JpaQlInspection")
public interface MicropostRepository extends JpaRepository<Micropost, Long> {
    Integer countByUser(User user);

    @Query(value = "select m from Micropost m join fetch m.user u where u = :user or exists (select r from Relationship r where r.followed = u and r.follower = :user) order by m.createdAt desc",
            countQuery = "select count(m) from Micropost m where m.user = :user or exists (select r from Relationship r where r.followed = m.user and r.follower = :user)")
    Page<Micropost> findAsFeed(@Param("user") User user, Pageable pageable);

    Page<Micropost> findByUser(User user, Pageable pageRequest);
}
