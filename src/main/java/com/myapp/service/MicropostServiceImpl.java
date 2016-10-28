package com.myapp.service;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.repository.MicropostCustomRepository;
import com.myapp.repository.MicropostRepository;
import com.myapp.repository.UserRepository;
import com.myapp.service.exceptions.NotPermittedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MicropostServiceImpl implements MicropostService {

    private final MicropostRepository micropostRepository;
    private final UserRepository userRepository;
    private final MicropostCustomRepository micropostCustomRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public MicropostServiceImpl(MicropostRepository micropostRepository, UserRepository userRepository, MicropostCustomRepository micropostCustomRepository, SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.userRepository = userRepository;
        this.micropostCustomRepository = micropostCustomRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public void delete(Long id) throws NotPermittedException {
        final Micropost micropost = micropostRepository.findOne(id);
        final Optional<User> currentUser = securityContextService.currentUser();

        currentUser.filter(u -> u.equals(micropost.getUser()))
                .ifPresent(u -> micropostRepository.delete(id));
        currentUser.filter(u -> u.equals(micropost.getUser()))
                .orElseThrow(() -> new NotPermittedException("no permission to delete this post"));
    }

    @Override
    public List<PostDTO> findAsFeed(PageParams pageParams) {
        return securityContextService.currentUser()
                .map(u -> micropostCustomRepository.findAsFeed(u, pageParams)
                        .map(toDTO())
                        .collect(Collectors.toList()))
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<PostDTO> findByUser(Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        return micropostCustomRepository.findByUser(user, pageParams)
                .map(toDTO())
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findMyPosts(PageParams pageParams) {
        return securityContextService.currentUser()
                .map(u -> findByUser(u.getId(), pageParams))
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public Micropost saveMyPost(Micropost post) {
        return securityContextService.currentUser()
                .map(u -> {
                    post.setUser(u);
                    return micropostRepository.save(post);
                })
                .orElseThrow(RuntimeException::new);
    }

    private Function<MicropostCustomRepository.Row, PostDTO> toDTO() {
        return r -> {
            final Boolean isMyPost = securityContextService.currentUser()
                    .map(u -> r.getMicropost().getUser().equals(u))
                    .orElse(null);
            return PostDTO.newInstance(r.getMicropost(), r.getUserStats(), isMyPost);
        };
    }

}
