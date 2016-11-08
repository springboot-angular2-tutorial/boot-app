package com.myapp.service;

import com.myapp.Utils;
import com.myapp.domain.User;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserParams;
import com.myapp.repository.RelationshipRepository;
import com.myapp.repository.UserCustomRepository;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final RelationshipRepository relationshipRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserCustomRepository userCustomRepository,
                           RelationshipRepository relationshipRepository,
                           SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.userCustomRepository = userCustomRepository;
        this.relationshipRepository = relationshipRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public Optional<UserDTO> findOne(Long id) {
        return userCustomRepository.findOne(id).map(r -> {
            final Optional<User> currentUser = securityContextService.currentUser();
            final Boolean isFollowedByMe = currentUser
                    .map(u -> relationshipRepository
                            .findOneByFollowerAndFollowed(u, r.getUser())
                            .isPresent()
                    )
                    .orElse(null);
            // Set email only if it equals with currentUser.
            final String email = currentUser
                    .filter(u -> u.equals(r.getUser()))
                    .map(User::getUsername)
                    .orElse(null);

            return UserDTO.builder()
                    .id(r.getUser().getId())
                    .email(email)
                    .avatarHash(Utils.md5(r.getUser().getUsername()))
                    .name(r.getUser().getName())
                    .userStats(r.getUserStats())
                    .isFollowedByMe(isFollowedByMe)
                    .build();
        });
    }

    @Override
    public Optional<UserDTO> findMe() {
        return securityContextService.currentUser().flatMap(u -> findOne(u.getId()));
    }

    @Override
    public Page<UserDTO> findAll(PageRequest pageable) {
        return userRepository.findAll(pageable).map(u -> UserDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .avatarHash(Utils.md5(u.getUsername()))
                .build()
        );
    }

    @Override
    public User create(UserParams params) {
        return userRepository.save(params.toUser());
    }

    @Override
    public User update(User user, UserParams params) {
        params.getEmail().ifPresent(user::setUsername);
        params.getEncodedPassword().ifPresent(user::setPassword);
        params.getName().ifPresent(user::setName);
        return userRepository.save(user);
    }

    @Override
    public User updateMe(UserParams params) {
        return securityContextService.currentUser()
                .map(u -> update(u, params))
                .orElseThrow(() -> new AccessDeniedException(""));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findOneByUsername(username);
        final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
        user.ifPresent(detailsChecker::check);
        return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    }

}
