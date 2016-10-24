package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;

import java.util.List;

public interface MicropostCustomRepository {

    List<PostDTO> findAsFeed(User user, PageParams pageParams);

    List<PostDTO> findByUser(User user, Boolean isMyself, PageParams pageParams);

}
