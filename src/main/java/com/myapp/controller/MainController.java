package com.myapp.controller;

import com.myapp.config.AppConfig;
import com.myapp.service.AssetManifestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class MainController {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final AppConfig appConfig;

    private final AssetManifestService assetManifestService;

    @Autowired
    public MainController(AppConfig appConfig, AssetManifestService assetManifestService) {
        this.appConfig = appConfig;
        this.assetManifestService = assetManifestService;
    }

    @RequestMapping({"/**"})
    public String index(Model model) {
        model.addAttribute("assetHost", appConfig.getAssetHost());
        model.addAttribute("manifest", assetManifestService.fetchAssetManifest());
        return "index";
    }

}
