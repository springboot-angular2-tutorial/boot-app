package com.myapp.service;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.service.exceptions.NotPermittedException;

import java.util.List;

public interface MicropostService {

    void delete(Long id) throws NotPermittedException;

    List<PostDTO> findAsFeed(PageParams pageParams);

    List<PostDTO> findByUser(Long userId, PageParams pageParams);

    List<PostDTO> findMyPosts(PageParams pageParams);

    Micropost saveMyPost(Micropost post);

}
