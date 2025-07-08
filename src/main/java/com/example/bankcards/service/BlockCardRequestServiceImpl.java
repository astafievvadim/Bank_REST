package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.BlockCardRequestRepo;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.repository.CustomUserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockCardRequestServiceImpl implements BlockCardRequestService{

    private final BlockCardRequestRepo requestRepository;
    private final CardRepo cardRepository;
    private final CustomUserRepo userRepository;

    public BlockCardRequestServiceImpl(BlockCardRequestRepo requestRepository,
                                   CardRepo cardRepository,
                                   CustomUserRepo userRepository) {
        this.requestRepository = requestRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }
    @Override
    public BlockCardRequest createBlockRequest(Long cardId, Long userId, String comment) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        CustomUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User doesn't own this card");
        }

        BlockCardRequest request = new BlockCardRequest();
        request.setCard(card);
        request.setUser(user);
        request.setRequestedAt(LocalDateTime.now());
        request.setStatus(RequestStatusEnum.PENDING);
        request.setComment(comment);

        return requestRepository.save(request);
    }
    @Override
    public BlockCardRequest approveRequest(Long requestId) {
        BlockCardRequest request = getRequestById(requestId);
        request.setStatus(RequestStatusEnum.APPROVED);

        Card card = request.getCard();
        card.setStatus(CardStatusEnum.BLOCKED);
        cardRepository.save(card);

        return requestRepository.save(request);
    }
    @Override
    public BlockCardRequest declineRequest(Long requestId, String comment) {
        BlockCardRequest request = getRequestById(requestId);
        request.setStatus(RequestStatusEnum.DECLINED);
        request.setComment(comment);
        return requestRepository.save(request);
    }

    @Override
    public List<BlockCardRequestDto> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BlockCardRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    private BlockCardRequestDto toDto(BlockCardRequest request) {
        BlockCardRequestDto dto = new BlockCardRequestDto();
        dto.setCardId(request.getCard().getId());
        dto.setUserId(request.getUser().getId());
        dto.setComment(request.getComment());
        return dto;
    }
}
