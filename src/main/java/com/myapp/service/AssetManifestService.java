package com.myapp.service;

import com.myapp.config.AppConfig;
import com.myapp.dto.AssetManifest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AssetManifestService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AssetManifestService.class);

    private final AppConfig appConfig;

    @Autowired
    public AssetManifestService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Cacheable("assetManifest")
    public AssetManifest fetchAssetManifest() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(appConfig.getAssetManifestUrl())
                .build();
        try {
            final Response response = client.newCall(request).execute();
            final String jsonStr = response.body().string();
            return AssetManifest.newInstance(jsonStr);
        } catch (IOException e) {
            logger.info("manifest does not exist. fallback to default assets.");
            return new AssetManifest(); // fallback
        }
    }

    @CacheEvict("assetManifest")
    public void invalidateCache() {
        logger.info("the cache of asset manifest was invalidated.");
    }
}
