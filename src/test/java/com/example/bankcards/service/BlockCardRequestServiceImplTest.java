package com.example.bankcards.service;

import com.example.bankcards.entity.*;
import com.example.bankcards.repository.BlockCardRequestRepo;
import com.example.bankcards.repository.CardRepo;
import com.example.bankcards.repository.CustomUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockCardRequestServiceImplTest {

    @Mock
    private BlockCardRequestRepo requestRepo;

    @Mock
    private CardRepo cardRepo;

    @Mock
    private CustomUserRepo userRepo;

    @InjectMocks
    private BlockCardRequestServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBlockRequest_success() {
        Card card = new Card();
        card.setId(1L);
        CustomUser user = new CustomUser();
        user.setId(2L);
        card.setUser(user);

        when(cardRepo.findById(1L)).thenReturn(Optional.of(card));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(requestRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        BlockCardRequest result = service.createBlockRequest(1L, 2L, "Reason");

        assertNotNull(result);
        assertEquals(RequestStatusEnum.PENDING, result.getStatus());
        assertEquals("Reason", result.getComment());
        assertEquals(card, result.getCard());
        assertEquals(user, result.getUser());
        assertNotNull(result.getRequestedAt());
        verify(requestRepo, times(1)).save(any());
    }

    @Test
    void createBlockRequest_cardNotFound() {
        when(cardRepo.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.createBlockRequest(1L, 2L, "Test")
        );

        assertEquals("Card not found", ex.getMessage());
    }

    @Test
    void createBlockRequest_userMismatch() {
        Card card = new Card();
        card.setId(1L);
        CustomUser owner = new CustomUser();
        owner.setId(5L);
        card.setUser(owner);

        CustomUser user = new CustomUser();
        user.setId(2L);

        when(cardRepo.findById(1L)).thenReturn(Optional.of(card));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.createBlockRequest(1L, 2L, "Test")
        );

        assertEquals("User doesn't own this card", ex.getMessage());
    }

    @Test
    void approveRequest_success() {
        BlockCardRequest req = new BlockCardRequest();
        req.setId(1L);
        Card card = new Card();
        card.setStatus(CardStatusEnum.ACTIVE);
        req.setCard(card);
        req.setStatus(RequestStatusEnum.PENDING);

        when(requestRepo.findById(1L)).thenReturn(Optional.of(req));
        when(requestRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(cardRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        BlockCardRequest result = service.approveRequest(1L);

        assertEquals(RequestStatusEnum.APPROVED, result.getStatus());
        assertEquals(CardStatusEnum.BLOCKED, card.getStatus());
        verify(cardRepo).save(card);
        verify(requestRepo).save(req);
    }

    @Test
    void declineRequest_success() {
        BlockCardRequest req = new BlockCardRequest();
        req.setId(1L);
        req.setStatus(RequestStatusEnum.PENDING);

        when(requestRepo.findById(1L)).thenReturn(Optional.of(req));
        when(requestRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        BlockCardRequest result = service.declineRequest(1L, "Declined reason");

        assertEquals(RequestStatusEnum.DECLINED, result.getStatus());
        assertEquals("Declined reason", result.getComment());
        verify(requestRepo).save(req);
    }

    @Test
    void approveRequest_requestNotFound() {
        when(requestRepo.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.approveRequest(1L)
        );

        assertEquals("Request not found", ex.getMessage());
    }
}
