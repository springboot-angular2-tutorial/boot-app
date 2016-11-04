package com.myapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.domain.Micropost;
import com.myapp.domain.UserStats;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class PostDTO {

    private final long id;
    @NonNull
    private final String content;
    @NonNull
    private final Date createdAt;
    @JsonProperty("user")
    private final UserDTO userDTO;
    private final Boolean isMyPost;

    public static PostDTO newInstance(Micropost post, UserStats userStats, Boolean isMyPost) {
        final UserDTO userDTO = UserDTO.builder2(post.getUser(), userStats)
                .isMyself(isMyPost)
                .build();

        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userDTO(userDTO)
                .isMyPost(isMyPost)
                .build();
    }

    public static PostDTO newInstance(Micropost post, Boolean isMyPost) {
        return PostDTO.newInstance(post, null, isMyPost);
    }

}
