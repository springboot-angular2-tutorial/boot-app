package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;

import javax.annotation.Nullable;
import java.util.List;

public interface UserRepositoryCustom {

    List<RelatedUserDTO> findFollowings(User user,
                                        User currentUser,
                                        @Nullable Long sinceId,
                                        @Nullable Long maxId,
                                        @Nullable Integer maxSize);

    List<RelatedUserDTO> findFollowers(User user,
                                       User currentUser,
                                       @Nullable Long sinceId,
                                       @Nullable Long maxId,
                                       @Nullable Integer maxSize);

    UserDTO findOne(Long userId, User currentUser);
}
