package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;

import java.util.List;

public interface MicropostService {

    void delete(Long id);

    List<PostDTO> findAsFeed(PageParams pageParams);

    List<PostDTO> findByUser(User user, PageParams pageParams);
}
