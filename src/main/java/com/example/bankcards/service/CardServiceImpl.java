package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UnauthorizedTransferException;
import com.example.bankcards.repository.BlockCardRequestRepo;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.repository.TransferRepo;
import com.example.bankcards.repository.CustomUserRepo;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.UuidUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CardServiceImpl implements CardService{

    @Value("${key.card_expiration}")
    private int expiration;

    private final TransferRepo transferRepo;
    private final BlockCardRequestRepo blockCardRequestRepo;
    private final CardRepo cardRepo;
    private final CustomUserRepo customUserRepo;
    private final EncryptionUtil encryptionUtil;
    private final UuidUtils uuidUtil;

    @Autowired
    public CardServiceImpl(CardRepo cardRepo, CustomUserRepo customUserRepo, EncryptionUtil encryptionUtil, UuidUtils uuidUtil, TransferRepo transferRepo, BlockCardRequestRepo blockCardRequestRepo) {
        this.cardRepo = cardRepo;
        this.customUserRepo = customUserRepo;
        this.uuidUtil = uuidUtil;
        this.encryptionUtil = encryptionUtil;
        this.transferRepo = transferRepo;
        this.blockCardRequestRepo = blockCardRequestRepo;
    }


    @Override
    public CardDto createCard(CreateCardRequestDto req) {

        CustomUser u = customUserRepo.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("CustomUser not found"));

        String plain = uuidUtil.generateUuid();
        String encrypted = encryptionUtil.encrypt(plain);

        Card card = new Card();

        card.setUser(u);
        card.setEncryptedUuid(encrypted);
        card.setExpirationDate(LocalDate.now().plusYears(expiration));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(CardStatusEnum.ACTIVE);

        return toDto(cardRepo.save(card));
    }

    @Override
    public CardDto getCardDtoById(Long cardId) {

        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        return toDto(card);
    }

    @Override
    public Card getCardById(Long cardId) {
        Card card = cardRepo.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        return card;
    }

    @Override
    public List<CardDto> getCardsByUser(Long userId, int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        CustomUser u = customUserRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("CustomUser not found"));

        List<CardDto> dtos = new ArrayList<>();
        List<Card> cards = cardRepo.findAllByUser(u, pageable);

        for(Card card : cards){
            dtos.add(toDto(card));
        }

        return dtos;
    }

    @Override
    public List<CardDto> getAllCards() {

        List<CardDto> dtos = new ArrayList<>();


        List<Card> cards = cardRepo.findAll();

        for(Card card : cards){
            dtos.add(toDto(card));
        }

        return dtos;
    }

    @Override
    public void updateCard(Long cardId, UpdateCardRequestDto cardDto) {

        Card inDb = cardRepo.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        inDb.setStatus(cardDto.getStatus());

        cardRepo.save(inDb);

    }

    @Override
    public void deleteCardById(Long cardId) {
        if (!cardRepo.existsById(cardId)) {
            throw new NotFoundException("Card not found");
        }
        cardRepo.deleteById(cardId);
    }
    @Override
    public CardDto toDto(Card card) {

        String masked = uuidUtil.maskUuid(
                encryptionUtil.decrypt(
                        card.getEncryptedUuid()
                )
        );

        return new CardDto(
                card.getId(),
                masked,
                card.getExpirationDate(),
                card.getStatus(),
                card.getBalance().doubleValue()
        );
    }

    @Override
    public void transfer(Long userId, TransferRequestDto dto) {
        Card from = cardRepo.findById(dto.getFromCardId())
                .orElseThrow(() -> new NotFoundException("Card not found"));

        Card to = cardRepo.findById(dto.getToCardId())
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (!from.getUser().equals(to.getUser())) {
            throw new UnauthorizedTransferException("Cannot transfer between different users' cards");
        }

        if (!from.getUser().getId().equals(userId)) {
            throw new UnauthorizedTransferException("Not authorized to transfer between these cards");
        }

        if (!isValid(from)) {
            throw new InvalidRequestException("Source card is not valid for transfers");
        }

        if (!isValid(to)) {
            throw new InvalidRequestException("Target card is not valid for transfers");
        }

        BigDecimal amount = dto.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Transfer amount must be positive");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        Transfer t = new Transfer();
        t.setAmount(amount);
        t.setFromCard(from);
        t.setToCard(to);
        t.setTimestamp(LocalDateTime.now());

        cardRepo.save(from);
        cardRepo.save(to);
        transferRepo.save(t);
    }

    @Override
    public void requestCardBlock(BlockCardRequestDto dto) {

        Card card = cardRepo.findById(dto.getCardId())
                .orElseThrow(() -> new NotFoundException("Card not found"));

        CustomUser user = customUserRepo.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!card.getUser().getId().equals(dto.getUserId())) {
            throw new UnauthorizedTransferException("Cannot block another user's card");
        }

        BlockCardRequest bcr = new BlockCardRequest();

        bcr.setCard(card);
        bcr.setUser(user);
        bcr.setRequestedAt(LocalDateTime.now());
        bcr.setStatus(RequestStatusEnum.PENDING);
        bcr.setComment(dto.getComment());

        blockCardRequestRepo.save(bcr);
    }

    private boolean isValid(Card card) {
        return card.getStatus() == CardStatusEnum.ACTIVE
                && (card.getExpirationDate() == null || !card.getExpirationDate().isBefore(LocalDate.now()));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void expireCards() {
        List<Card> cardsToExpire = cardRepo.findByStatusAndExpirationDateBefore(CardStatusEnum.ACTIVE, LocalDate.now());
        for (Card card : cardsToExpire) {
            card.setStatus(CardStatusEnum.EXPIRED);
        }
        cardRepo.saveAll(cardsToExpire);
    }
}
