package com.myapp.repository;

import com.myapp.domain.Micropost;
import com.myapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MicropostRepository extends JpaRepository<Micropost, Long>, MicropostRepositoryCustom {

    Integer countByUser(User user);

}
