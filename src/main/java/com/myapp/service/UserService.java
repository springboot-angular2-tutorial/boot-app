package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserOptionalParams;

import java.util.List;
import java.util.Optional;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    User update(User user, UserOptionalParams params);

    List<RelatedUserDTO> findFollowings(User user, Optional<Long> sinceId, Optional<Long> maxId, Integer maxSize);

    List<RelatedUserDTO> findFollowers(User user, Optional<Long> sinceId, Optional<Long> maxId, Integer maxSize);

    UserDTO findOne(Long id);
}
