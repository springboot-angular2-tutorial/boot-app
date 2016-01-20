package com.myapp.repository;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;

import java.util.List;
import java.util.Optional;

public interface MicropostRepositoryCustom {

    List<Micropost> findAsFeed(User user,
                               Optional<Long> sinceId,
                               Optional<Long> maxId,
                               Integer maxSize);

    List<Micropost> findByUser(User user,
                               Optional<Long> sinceId,
                               Optional<Long> maxId,
                               Integer maxSize);
}
