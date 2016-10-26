package com.myapp.service;

import com.myapp.domain.User;
import com.myapp.dto.PageParams;
import com.myapp.dto.RelatedUserDTO;
import com.myapp.dto.UserDTO;
import com.myapp.dto.UserParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {

    Optional<UserDTO> findOne(Long id);

    Optional<UserDTO> findMe();

    Page<UserDTO> findAll(PageRequest pageable);

    User create(UserParams params);

    User update(User user, UserParams params);

    User updateMe(UserParams params);


}
