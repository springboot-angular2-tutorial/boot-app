package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.*;

import java.util.List;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    User update(User user, UserParams params);

    List<RelatedUserDTO> findFollowings(User user, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(User user, PageParams pageParams);

    UserDTO findOne(Long id);
}
