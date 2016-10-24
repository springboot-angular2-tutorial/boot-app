package com.myapp.repository;

import com.myapp.domain.Micropost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MicropostRepository extends JpaRepository<Micropost, Long> {
}
