package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    List<UserDTO> findFollowings(User user,
                                 User currentUser,
                                 Optional<Long> sinceId,
                                 Optional<Long> maxId,
                                 Integer maxSize);

    List<UserDTO> findFollowers(User user,
                                User currentUser,
                                Optional<Long> sinceId,
                                Optional<Long> maxId,
                                Integer maxSize);
}
