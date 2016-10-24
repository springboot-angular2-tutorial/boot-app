package com.myapp.repository;

import com.myapp.domain.User;
import com.myapp.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserCustomRepository extends Repository<User, Long> {

    Optional<UserDTO> findOne(Long userId, User currentUser);

    Page<UserDTO> findAll(PageRequest pageable);

}
