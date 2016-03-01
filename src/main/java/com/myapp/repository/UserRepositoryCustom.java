package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;

import java.util.List;

public interface UserRepositoryCustom {

    List<RelatedUserDTO> findFollowings(User user, User currentUser, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(User user, User currentUser, PageParams pageParams);

    UserDTO findOne(Long userId, User currentUser);
}
