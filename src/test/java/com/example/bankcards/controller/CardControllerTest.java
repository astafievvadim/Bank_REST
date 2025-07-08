package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.service.CardService;
import com.example.bankcards.repository.CustomUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private CustomUserRepo userRepo;

    @InjectMocks
    private CardController cardController;

    private MockMvc mockMvc;

    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();

        mockPrincipal = () -> "user@example.com";

        CustomUser user = new CustomUser();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setRole(RoleEnum.USER);

        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    }

    @Test
    void getAllUserCards_shouldReturnCards() throws Exception {
        CardDto cardDto = new CardDto(1L, "xxxx-xxxx-xxxx-1234", LocalDate.now(), null, 100.0);
        when(cardService.getCardsByUser(eq(1L), anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(cardDto));

        mockMvc.perform(get("/cards")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getCardBalance_shouldReturnBalance() throws Exception {
        Card card = new Card();
        CustomUser user = new CustomUser();
        user.setId(1L);
        card.setUser(user);
        when(cardService.getCardById(1L)).thenReturn(card);

        CardDto cardDto = new CardDto(1L, "xxxx-xxxx", LocalDate.now(), null, 200.0);
        when(cardService.getCardDtoById(1L)).thenReturn(cardDto);

        mockMvc.perform(get("/cards/1/balance").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void blockCard_shouldCallService() throws Exception {
        Card card = new Card();
        CustomUser user = new CustomUser();
        user.setId(1L);
        card.setUser(user);
        when(cardService.getCardById(1L)).thenReturn(card);

        mockMvc.perform(post("/cards/1/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardId\":1,\"userId\":1,\"comment\":\"Lost card\"}")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());

        verify(cardService).requestCardBlock(any(BlockCardRequestDto.class));
    }

    @Test
    void transfer_shouldCallService() throws Exception {
        mockMvc.perform(post("/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCardId\":1,\"toCardId\":2,\"amount\":50}")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());

        verify(cardService).transfer(eq(1L), any(TransferRequestDto.class));
    }

    @Test
    void getCardsByUserAsAdmin_shouldReturnCards() throws Exception {
        CustomUser admin = new CustomUser();
        admin.setId(99L);
        admin.setEmail("user@example.com");
        admin.setRole(RoleEnum.ADMIN);
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(admin));

        CardDto cardDto = new CardDto(1L, "xxxx-xxxx-xxxx-1234", LocalDate.now(), null, 100.0);
        when(cardService.getCardsByUser(eq(2L), anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(cardDto));

        mockMvc.perform(get("/cards/admin/users/2/cards")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
