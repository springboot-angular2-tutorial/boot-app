package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.UserOptionalParams;
import com.myapp.repository.MicropostRepository;
import com.myapp.repository.UserRepository;
import com.myapp.dto.UserStats;
import com.myapp.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MicropostRepository micropostRepository;
    private final RelationshipRepository relationshipRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MicropostRepository micropostRepository, RelationshipRepository relationshipRepository) {
        this.userRepository = userRepository;
        this.micropostRepository = micropostRepository;
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public UserStats getStats(User user) {
        Integer micropostCnt = micropostRepository.countByUser(user);
        Integer followingCnt = relationshipRepository.countByFollower(user);
        Integer followerCnt = relationshipRepository.countByFollowed(user);
        return UserStats.builder()
                .micropostCnt(micropostCnt)
                .followerCnt(followerCnt)
                .followingCnt(followingCnt)
                .build();
    }

    @Override
    public User update(User user, UserOptionalParams params) {
        params.getEmail().ifPresent(user::setUsername);
        params.getPassword().ifPresent(p -> user.setPassword(new BCryptPasswordEncoder().encode(p)));
        params.getName().ifPresent(user::setName);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepository.findOneByUsername(username);
        final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();
        user.ifPresent(detailsChecker::check);
        return user.orElseThrow(() -> new UsernameNotFoundException("user not found."));
    }
}
