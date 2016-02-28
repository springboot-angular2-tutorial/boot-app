package com.myapp.service;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PostDTO;
import com.myapp.repository.MicropostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<PostDTO> findAsFeed(Optional<Long> sinceId, Optional<Long> maxId, Integer count) {
        final User currentUser = securityContextService.currentUser();
        final List<PostDTO> feed = micropostRepository.findAsFeed(currentUser, sinceId, maxId, count);
        feed.forEach(p -> p.setIsMyPost(p.getUser().getId() == currentUser.getId()));
        return feed;
    }

}
