package com.myapp.dto;

import java.util.HashMap;
import java.util.Optional;

public class AssetManifest extends HashMap<String, String> {
    @Override
    public String get(Object key) {
        return Optional.ofNullable(super.get(key)).orElse(key.toString());
    }
}
