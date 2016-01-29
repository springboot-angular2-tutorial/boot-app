package com.myapp.repository;

import com.myapp.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    List<User> findFollowings(User user,
                              Optional<Long> sinceId,
                              Optional<Long> maxId,
                              Integer maxSize);

    List<User> findFollowers(User user,
                             Optional<Long> sinceId,
                             Optional<Long> maxId,
                             Integer maxSize);
}
