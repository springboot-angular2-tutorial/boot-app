package com.myapp.service;

import com.myapp.dto.AssetManifest;

public interface AssetManifestService {
    AssetManifest fetchAssetManifest();

    void invalidateCache();
}
