package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserParams;
import com.myapp.repository.UserCustomRepository;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    public User update(User user, UserParams params) {
        params.getEmail().ifPresent(user::setUsername);
        params.getEncodedPassword().ifPresent(user::setPassword);
        params.getName().ifPresent(user::setName);
        return userRepository.save(user);
    }

    @Override
    public List<RelatedUserDTO> findFollowings(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return userCustomRepository.findFollowings(user, currentUser, pageParams);
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        return userCustomRepository.findFollowers(user, currentUser, pageParams);
    }

    @Override
    public Optional<UserDTO> findOne(Long id) {
        final User currentUser = securityContextService.currentUser();
        return userCustomRepository.findOne(id, currentUser);
    }

    @Override
    public Optional<UserDTO> findMe() {
        final User currentUser = securityContextService.currentUser();
        return findOne(currentUser.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findOneByUsername(username);
        final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
        user.ifPresent(detailsChecker::check);
        return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    }

}
