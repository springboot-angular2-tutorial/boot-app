package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.repository.MicropostRepository;
import com.myapp.domain.Micropost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MicropostServiceImpl implements MicropostService {

    private final MicropostRepository micropostRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public MicropostServiceImpl(MicropostRepository micropostRepository, SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public void delete(Long id) {
        User currentUser = securityContextService.currentUser();
        Micropost micropost = micropostRepository.findOne(id);
        if (currentUser != micropost.getUser())
            throw new NotPermittedException("no permission to delete this post");
        micropostRepository.delete(id);
    }
}
