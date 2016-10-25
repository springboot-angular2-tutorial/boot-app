package com.myapp.dto;

import com.myapp.domain.Micropost;
import lombok.Value;

@Value
public class MicropostParams {

    private String content;

    public Micropost toPost() {
       return new Micropost(content);
    }
}
