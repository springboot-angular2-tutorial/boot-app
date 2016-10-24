package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RelatedUserCustomRepository extends Repository<User, Long> {

    List<RelatedUserDTO> findFollowings(User user, User currentUser, PageParams pageParams);

    List<RelatedUserDTO> findFollowers(User user, User currentUser, PageParams pageParams);

}
