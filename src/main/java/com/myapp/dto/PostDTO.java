package com.myapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import lombok.*;

import java.util.Date;

@Value
@Builder
public class PostDTO {

    private final long id;
    private final String content;
    private final Date createdAt;
    @JsonProperty("user")
    private final UserDTO userDTO;
    private final Boolean isMyPost;


    public static PostDTO newInstance(Micropost post, User user, UserStats userStats, Boolean isMyPost) {
        final UserDTO userDTO = UserDTO.newInstance(user, userStats, isMyPost);

        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .userDTO(userDTO)
                .isMyPost(isMyPost)
                .build();
    }

    public static PostDTO newInstance(Micropost post, User user, Boolean isMyPost) {
        return PostDTO.newInstance(post, user, null, isMyPost);
    }

}
