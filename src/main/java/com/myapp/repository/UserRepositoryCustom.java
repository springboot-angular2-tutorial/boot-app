package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    List<RelatedUserDTO> findFollowings(User user,
                                        User currentUser,
                                        Optional<Long> sinceId,
                                        Optional<Long> maxId,
                                        Integer maxSize);

    List<RelatedUserDTO> findFollowers(User user,
                                       User currentUser,
                                       Optional<Long> sinceId,
                                       Optional<Long> maxId,
                                       Integer maxSize);
}
