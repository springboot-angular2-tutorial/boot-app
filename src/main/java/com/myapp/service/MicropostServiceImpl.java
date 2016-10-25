package com.myapp.service;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.repository.MicropostCustomRepository;
import com.myapp.repository.MicropostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MicropostServiceImpl implements MicropostService {

    private final MicropostRepository micropostRepository;
    private final MicropostCustomRepository micropostCustomRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public MicropostServiceImpl(MicropostRepository micropostRepository, MicropostCustomRepository micropostCustomRepository, SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.micropostCustomRepository = micropostCustomRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public void delete(Long id) throws NotPermittedException {
        User currentUser = securityContextService.currentUser();
        Micropost micropost = micropostRepository.findOne(id);
        if (currentUser != micropost.getUser())
            throw new NotPermittedException("no permission to delete this post");
        micropostRepository.delete(id);
    }

    @Override
    public List<PostDTO> findAsFeed(PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return micropostCustomRepository.findAsFeed(currentUser, pageParams)
                .map(r -> {
                    final Boolean isMyPost = (r.getMicropost().getUser().equals(currentUser));
                    return PostDTO.newInstance(r.getMicropost(), r.getUserStats(), isMyPost);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findByUser(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        final Boolean isMyself = Optional.ofNullable(currentUser)
                .map(user::equals)
                .orElse(null);
        return micropostCustomRepository.findByUser(user, pageParams)
                .map(r -> PostDTO.newInstance(r.getMicropost(), isMyself))
                .collect(Collectors.toList());
    }

    @Override
    public Micropost saveMyPost(Micropost post) {
        User currentUser = securityContextService.currentUser();
        post.setUser(currentUser);
        return micropostRepository.save(post);
    }

}
