package com.example.bankcards.repository;

import com.example.bankcards.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepo extends JpaRepository<CustomUser,Long> {

    Optional<CustomUser> findByEmail(String email);

}
