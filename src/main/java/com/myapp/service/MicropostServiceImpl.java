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
                .map(toDTO(currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findByUser(Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        final User currentUser = securityContextService.currentUser();
        return micropostCustomRepository.findByUser(user, pageParams)
                .map(toDTO(currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findMyPosts(PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return findByUser(currentUser.getId(), pageParams);
    }

    @Override
    public Micropost saveMyPost(Micropost post) {
        User currentUser = securityContextService.currentUser();
        post.setUser(currentUser);
        return micropostRepository.save(post);
    }

    private Function<MicropostCustomRepository.Row, PostDTO> toDTO(User currentUser) {
        return r -> {
            final Boolean isMyPost = Optional.ofNullable(currentUser)
                    .map(u -> r.getMicropost().getUser().equals(u))
                    .orElse(null);
            return PostDTO.newInstance(r.getMicropost(), r.getUserStats(), isMyPost);
        };
    }

}
