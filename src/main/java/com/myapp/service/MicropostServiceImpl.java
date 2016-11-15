package com.myapp.service;

import com.myapp.domain.Micropost;
import com.myapp.domain.Relationship;
import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.PostDTO;
import com.myapp.repository.MicropostCustomRepository;
import com.myapp.repository.MicropostRepository;
import com.myapp.repository.RelationshipRepository;
import com.myapp.repository.UserRepository;
import com.myapp.service.exceptions.NotPermittedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MicropostServiceImpl implements MicropostService {

    private final MicropostRepository micropostRepository;
    private final UserRepository userRepository;
    private final MicropostCustomRepository micropostCustomRepository;
    private final RelationshipRepository relationshipRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public MicropostServiceImpl(MicropostRepository micropostRepository, UserRepository userRepository, MicropostCustomRepository micropostCustomRepository, RelationshipRepository relationshipRepository, SecurityContextService securityContextService) {
        this.micropostRepository = micropostRepository;
        this.userRepository = userRepository;
        this.micropostCustomRepository = micropostCustomRepository;
        this.relationshipRepository = relationshipRepository;
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
                .map(u -> {
                    final List<MicropostCustomRepository.Row> rows = micropostCustomRepository.findAsFeed(u, pageParams);
                    final List<User> relatedUsers = rows.stream()
                            .map(row -> row.getMicropost().getUser())
                            .collect(Collectors.toList());
                    return rows.stream()
                            .map(toDTO(relatedUsers))
                            .collect(Collectors.toList());
                })
                .orElseThrow(() -> new AccessDeniedException(""));
    }

    @Override
    public List<PostDTO> findByUser(Long userId, PageParams pageParams) {
        final User user = userRepository.findOne(userId);
        final List<User> relatedUsers = Collections.singletonList(user);
        return micropostCustomRepository.findByUser(user, pageParams)
                .stream()
                .map(toDTO(relatedUsers))
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
                .orElseThrow(() -> new AccessDeniedException(""));
    }

    private Function<MicropostCustomRepository.Row, PostDTO> toDTO(List<User> relatedUsers) {
        final Optional<User> currentUser = securityContextService.currentUser();
        final List<User> followedByMe = currentUser.map(u -> relationshipRepository
                .findAllByFollowerAndFollowedIn(u, relatedUsers)
                .map(Relationship::getFollowed)
                .collect(Collectors.toList())
        ).orElse(Collections.emptyList());

        return r -> {
            final Boolean isMyPost = currentUser
                    .map(u -> r.getMicropost().getUser().equals(u))
                    .orElse(null);
            final Boolean isFollowedByMe = currentUser
                    .map(u -> followedByMe.contains(r.getMicropost().getUser()))
                    .orElse(null);
            return PostDTO.newInstance(r.getMicropost(), r.getUserStats(), isMyPost, isFollowedByMe);
        };
    }

}
