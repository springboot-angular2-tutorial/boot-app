package com.myapp.controller;

import com.myapp.service.AssetManifestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sns/Micropost-AssetManifest-Updated")
public class AssetManifestSubscriber {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(AssetManifestSubscriber.class);

    private final AssetManifestService assetManifestService;

    @Autowired
    public AssetManifestSubscriber(AssetManifestService assetManifestService) {
        this.assetManifestService = assetManifestService;
    }


    @RequestMapping(method = RequestMethod.POST)
    public void handleNotificationMessage(@RequestBody String body) {
        logger.info(body);
        assetManifestService.invalidateCache();
    }

}

