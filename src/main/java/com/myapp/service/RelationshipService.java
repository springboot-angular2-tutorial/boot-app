package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.service.exceptions.RelationshipNotFoundException;

import java.util.List;

public interface RelationshipService {

    List<RelatedUserDTO> findFollowings(User user, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(User user, PageParams pageParams);

    void follow(Long userId);

    void unfollow(Long userId) throws RelationshipNotFoundException;

}
