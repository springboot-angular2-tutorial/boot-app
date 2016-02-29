package com.myapp.service;

import com.myapp.dto.PostDTO;

import javax.annotation.Nullable;
import java.util.List;

public interface MicropostService {

    void delete(Long id);

    List<PostDTO> findAsFeed(@Nullable Long sinceId,
                             @Nullable Long maxId,
                             @Nullable Integer maxSize);
}
