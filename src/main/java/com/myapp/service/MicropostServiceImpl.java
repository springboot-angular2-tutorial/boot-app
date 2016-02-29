package com.myapp.service;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.repository.MicropostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<PostDTO> findAsFeed(PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        final List<PostDTO> feed = micropostRepository.findAsFeed(currentUser, pageParams);
        feed.forEach(p -> p.setIsMyPost(p.getUser().getId() == currentUser.getId()));
        return feed;
    }

    @Override
    public List<PostDTO> findByUser(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        final Boolean isMyPost = (currentUser != null) ? (currentUser.equals(user)) : null;
        return micropostRepository.findByUser(user, pageParams)
                .stream()
                .map(p -> PostDTO.builder()
                        .micropost(p)
                        .user(p.getUser())
                        .isMyPost(isMyPost)
                        .build()
                )
                .collect(Collectors.toList());
    }

}
