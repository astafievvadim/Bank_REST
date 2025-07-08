package com.example.bankcards.service;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.entity.BlockCardRequest;

import java.util.List;

public interface BlockCardRequestService {

    BlockCardRequest createBlockRequest(Long cardId, Long userId, String comment);
    BlockCardRequest approveRequest(Long requestId);
    BlockCardRequest declineRequest(Long requestId, String comment);
    List<BlockCardRequestDto> getAllRequests();
}
