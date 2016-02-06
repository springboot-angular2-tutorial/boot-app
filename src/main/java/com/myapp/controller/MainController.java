package com.myapp.controller;

import com.myapp.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final AppConfig appConfig;

    @Autowired
    public MainController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @RequestMapping({"/**"})
    public String index(Model model) {
        model.addAttribute("assetHost", appConfig.getAssetHost());
        return "index";
    }

}
