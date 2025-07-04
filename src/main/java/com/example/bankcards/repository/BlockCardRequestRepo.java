package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockCardRequest;
import org.springframework.cglib.core.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockCardRequestRepo extends JpaRepository<BlockCardRequest, Long> {
}
