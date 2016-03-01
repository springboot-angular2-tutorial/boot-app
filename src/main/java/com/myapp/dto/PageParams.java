package com.myapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
public final class PageParams {

    private static final int DEFAULT_COUNT = 20;

    private Long sinceId;
    private Long maxId;
    private int count = DEFAULT_COUNT;

    public Optional<Long> getSinceId() {
        return Optional.ofNullable(sinceId);
    }

    public Optional<Long> getMaxId() {
        return Optional.ofNullable(maxId);
    }


}
