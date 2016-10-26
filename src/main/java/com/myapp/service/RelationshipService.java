package com.myapp.service;

import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.service.exceptions.RelationshipNotFoundException;

import java.util.List;

public interface RelationshipService {

    List<RelatedUserDTO> findFollowings(Long userId, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(Long userId, PageParams pageParams);

    void follow(Long userId);

    void unfollow(Long userId) throws RelationshipNotFoundException;

}
