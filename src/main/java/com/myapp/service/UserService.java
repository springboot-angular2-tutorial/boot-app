package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserOptionalParams;

import javax.annotation.Nullable;
import java.util.List;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    User update(User user, UserOptionalParams params);

    List<RelatedUserDTO> findFollowings(User user,
                                        @Nullable Long sinceId,
                                        @Nullable Long maxId,
                                        @Nullable Integer maxSize);

    List<RelatedUserDTO> findFollowers(User user,
                                       @Nullable Long sinceId,
                                       @Nullable Long maxId,
                                       @Nullable Integer maxSize);

    UserDTO findOne(Long id);
}
