package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserParams;
import com.myapp.repository.UserCustomRepository;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserCustomRepository userCustomRepository,
                           SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.userCustomRepository = userCustomRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public Optional<UserDTO> findOne(Long id) {
        final User currentUser = securityContextService.currentUser();
        return userCustomRepository.findOne(id, currentUser)
                .map(r -> {
                    final Boolean isMyself = Optional.ofNullable(currentUser)
                            .map(u -> u.equals(r.getUser()))
                            .orElse(null);
                    return UserDTO.newInstance(
                            r.getUser(),
                            r.getUserStats(),
                            isMyself
                    );
                });
    }

    @Override
    public Optional<UserDTO> findMe() {
        final User currentUser = securityContextService.currentUser();
        return findOne(currentUser.getId());
    }

    @Override
    public Page<UserDTO> findAll(PageRequest pageable) {
        return userRepository.findAll(pageable).map(UserDTO::newInstance);
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
        User user = securityContextService.currentUser();
        return update(user, params);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findOneByUsername(username);
        final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
        user.ifPresent(detailsChecker::check);
        return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    }

}
