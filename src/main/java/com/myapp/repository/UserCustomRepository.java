package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface UserCustomRepository {

    Optional<UserDTO> findOne(Long userId, User currentUser);

    Page<UserDTO> findAll(PageRequest pageable);

}
