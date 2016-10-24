package com.myapp.controller;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.repository.MicropostRepository;
import com.myapp.service.MicropostService;
import com.myapp.service.NotPermittedException;
import com.myapp.service.SecurityContextService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/microposts")
public class MicropostController {

    private final MicropostRepository micropostRepository;
    private final MicropostService micropostService;
    private final SecurityContextService securityContextService;

    @Autowired
    public MicropostController(MicropostRepository micropostRepository,
                               MicropostService micropostService,
                               SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.micropostService = micropostService;
        this.securityContextService = securityContextService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Micropost create(@RequestBody MicropostParam param) {
        User currentUser = securityContextService.currentUser();
        return micropostRepository.save(new Micropost(currentUser, param.getContent()));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) throws NotPermittedException {
        micropostService.delete(id);
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotPermittedException.class)
    public void handleNoPermission() {
    }

    @Data
    private static class MicropostParam {
        private String content;
    }
}
