package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserOptionalParams;
import com.myapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, SecurityContextService securityContextService) {
        this.userRepository = userRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public User update(User user, UserOptionalParams params) {
        params.getEmail().ifPresent(user::setUsername);
        params.getPassword().ifPresent(p -> user.setPassword(new BCryptPasswordEncoder().encode(p)));
        params.getName().ifPresent(user::setName);
        return userRepository.save(user);
    }

    @Override
    public List<RelatedUserDTO> findFollowings(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        final List<RelatedUserDTO> followings = userRepository.findFollowings(user, currentUser, pageParams);
        followings.forEach(f -> {
            if (currentUser == null) return;
            f.setIsMyself(f.getId() == currentUser.getId());
        });
        return followings;
    }

    @Override
    public List<RelatedUserDTO> findFollowers(User user, PageParams pageParams) {
        final User currentUser = securityContextService.currentUser();
        final List<RelatedUserDTO> followers = userRepository.findFollowers(user, currentUser, pageParams);
        followers.forEach(f -> {
            if (currentUser == null) return;
            f.setIsMyself(f.getId() == currentUser.getId());
        });
        return followers;
    }

    @Override
    public UserDTO findOne(Long id) {
        final User currentUser = securityContextService.currentUser();
        final UserDTO user = userRepository.findOne(id, currentUser);
        if (currentUser != null) {
            user.setIsMyself(user.getId() == currentUser.getId());
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findOneByUsername(username);
        final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
        user.ifPresent(detailsChecker::check);
        return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    }
}
