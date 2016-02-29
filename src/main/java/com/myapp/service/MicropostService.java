package com.myapp.service;

import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;

import javax.annotation.Nullable;
import java.util.List;

public interface MicropostService {

    void delete(Long id);

    List<PostDTO> findAsFeed(PageParams pageParams);
}
