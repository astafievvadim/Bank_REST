package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.*;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.UuidUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepo cardRepo;
    @Mock
    private CustomUserRepo customUserRepo;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private UuidUtils uuidUtil;
    @Mock
    private TransferRepo transferRepo;
    @Mock
    private BlockCardRequestRepo blockCardRequestRepo;

    @InjectMocks
    private CardServiceImpl service;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        setPrivateField(service, "expiration", 5);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createCard_success() {
        CustomUser user = new CustomUser();
        user.setId(1L);

        when(customUserRepo.findById(1L)).thenReturn(Optional.of(user));
        when(uuidUtil.generateUuid()).thenReturn("test-uuid");
        when(encryptionUtil.encrypt("test-uuid")).thenReturn("encrypted");
        when(cardRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(uuidUtil.maskUuid(any())).thenReturn("masked-uuid");
        when(encryptionUtil.decrypt("encrypted")).thenReturn("test-uuid");

        CreateCardRequestDto req = new CreateCardRequestDto(1L);
        CardDto dto = service.createCard(req);

        assertNotNull(dto);
        assertEquals("masked-uuid", dto.getMaskedUuid());
        assertEquals(CardStatusEnum.ACTIVE, dto.getStatus());
        assertEquals(0.0, dto.getBalance());
    }

    @Test
    void transfer_success() {
        CustomUser user = new CustomUser();
        user.setId(1L);

        Card from = new Card();
        from.setId(1L);
        from.setUser(user);
        from.setBalance(new BigDecimal("100.00"));
        from.setStatus(CardStatusEnum.ACTIVE);
        from.setExpirationDate(LocalDate.now().plusDays(1));

        Card to = new Card();
        to.setId(2L);
        to.setUser(user);
        to.setBalance(new BigDecimal("50.00"));
        to.setStatus(CardStatusEnum.ACTIVE);
        to.setExpirationDate(LocalDate.now().plusDays(1));

        when(cardRepo.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(to));

        TransferRequestDto dto = new TransferRequestDto(1L, 2L, new BigDecimal("30.00"));

        service.transfer(1L, dto);

        assertEquals(new BigDecimal("70.00"), from.getBalance());
        assertEquals(new BigDecimal("80.00"), to.getBalance());

        verify(cardRepo, times(1)).save(from);
        verify(cardRepo, times(1)).save(to);
        verify(transferRepo, times(1)).save(any(Transfer.class));
    }

    @Test
    void transfer_insufficientFunds() {
        CustomUser user = new CustomUser();
        user.setId(1L);

        Card from = new Card();
        from.setId(1L);
        from.setUser(user);
        from.setBalance(new BigDecimal("20.00"));
        from.setStatus(CardStatusEnum.ACTIVE);
        from.setExpirationDate(LocalDate.now().plusDays(1));

        Card to = new Card();
        to.setId(2L);
        to.setUser(user);
        to.setBalance(new BigDecimal("50.00"));
        to.setStatus(CardStatusEnum.ACTIVE);
        to.setExpirationDate(LocalDate.now().plusDays(1));

        when(cardRepo.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepo.findById(2L)).thenReturn(Optional.of(to));

        TransferRequestDto dto = new TransferRequestDto(1L, 2L, new BigDecimal("30.00"));

        assertThrows(InsufficientFundsException.class, () -> service.transfer(1L, dto));
    }

    @Test
    void getCardDtoById_cardNotFound() {
        when(cardRepo.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getCardDtoById(10L));
    }

    @Test
    void requestCardBlock_success() {
        CustomUser user = new CustomUser();
        user.setId(1L);

        Card card = new Card();
        card.setId(5L);
        card.setUser(user);

        when(cardRepo.findById(5L)).thenReturn(Optional.of(card));
        when(customUserRepo.findById(1L)).thenReturn(Optional.of(user));

        BlockCardRequestDto dto = new BlockCardRequestDto(5L, 1L, OffsetDateTime.now(), "Lost");

        service.requestCardBlock(dto);

        verify(blockCardRequestRepo).save(any(BlockCardRequest.class));
    }

    @Test
    void expireCards_shouldUpdateExpired() {
        Card c1 = new Card();
        c1.setStatus(CardStatusEnum.ACTIVE);
        c1.setExpirationDate(LocalDate.now().minusDays(1));

        when(cardRepo.findByStatusAndExpirationDateBefore(CardStatusEnum.ACTIVE, LocalDate.now()))
                .thenReturn(List.of(c1));

        service.expireCards();

        assertEquals(CardStatusEnum.EXPIRED, c1.getStatus());
        verify(cardRepo).saveAll(List.of(c1));
    }
}
