package com.myapp.service;

import com.myapp.dto.PostDTO;

import java.util.List;
import java.util.Optional;

public interface MicropostService {

    void delete(Long id);

    List<PostDTO> findAsFeed(Optional<Long> sinceId, Optional<Long> maxId, Integer integer);
}
