package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CustomUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepo extends PagingAndSortingRepository<Card,Long> {

    List<Card> findAllByUser(CustomUser customUser, Pageable pageable);
    Optional<Card> findById(Long cardId);
    List<Card> findAll();
    Card save(Card from);
    boolean existsById(Long cardId);
    void deleteById(Long cardId);
}
