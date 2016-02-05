package com.myapp.repository;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PostDTO;

import java.util.List;
import java.util.Optional;

public interface MicropostRepositoryCustom {

    List<PostDTO> findAsFeed(User user,
                             Optional<Long> sinceId,
                             Optional<Long> maxId,
                             Integer maxSize);

    List<Micropost> findByUser(User user,
                               Optional<Long> sinceId,
                               Optional<Long> maxId,
                               Integer maxSize);
}
