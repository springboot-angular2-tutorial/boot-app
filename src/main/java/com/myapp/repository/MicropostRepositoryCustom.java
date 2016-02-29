package com.myapp.repository;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PostDTO;

import javax.annotation.Nullable;
import java.util.List;

public interface MicropostRepositoryCustom {

    List<PostDTO> findAsFeed(User user,
                             @Nullable Long sinceId,
                             @Nullable Long maxId,
                             @Nullable Integer maxSize);

    List<Micropost> findByUser(User user,
                               @Nullable Long sinceId,
                               @Nullable Long maxId,
                               @Nullable Integer maxSize);
}
