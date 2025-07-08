package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.dto.DeclineCommentDto;
import com.example.bankcards.entity.BlockCardRequest;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.repository.CustomUserRepo;
import com.example.bankcards.service.BlockCardRequestService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserRepo userRepo;

    @Mock
    private BlockCardRequestService blockRequestService;

    @Mock
    private Principal principal;

    private CustomUser adminUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new CustomUser();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(RoleEnum.ADMIN);

        when(principal.getName()).thenReturn(adminUser.getEmail());
        when(userRepo.findByEmail(adminUser.getEmail())).thenReturn(Optional.of(adminUser));
    }

    @Test
    void getAllBlockRequests_shouldReturnList() {
        BlockCardRequestDto dto = new BlockCardRequestDto();
        dto.setCardId(10L);
        dto.setUserId(1L);
        dto.setComment("Test comment");

        when(blockRequestService.getAllRequests()).thenReturn(List.of(dto));

        ResponseEntity<List<BlockCardRequestDto>> response = adminController.getAllBlockRequests(principal);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getCardId()).isEqualTo(10L);

        verify(blockRequestService).getAllRequests();
    }

    @Test
    void approveBlockRequest_shouldReturnApprovedRequest() {
        Long requestId = 5L;
        BlockCardRequest approvedRequest = new BlockCardRequest();
        approvedRequest.setId(requestId);

        when(blockRequestService.approveRequest(requestId)).thenReturn(approvedRequest);

        ResponseEntity<BlockCardRequest> response = adminController.approveBlockRequest(requestId, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(approvedRequest, response.getBody());

        verify(blockRequestService).approveRequest(requestId);
    }

    @Test
    void declineBlockRequest_withComment_shouldReturnDeclinedRequest() {
        Long requestId = 7L;
        String declineComment = "Invalid request";

        DeclineCommentDto dto = new DeclineCommentDto("Some comment");
        dto.setComment(declineComment);

        BlockCardRequest declinedRequest = new BlockCardRequest();
        declinedRequest.setId(requestId);

        when(blockRequestService.declineRequest(requestId, declineComment)).thenReturn(declinedRequest);

        ResponseEntity<BlockCardRequest> response = adminController.declineBlockRequest(requestId, dto, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(declinedRequest, response.getBody());

        verify(blockRequestService).declineRequest(requestId, declineComment);
    }

    @Test
    void declineBlockRequest_withoutComment_shouldReturnDeclinedRequest() {
        Long requestId = 8L;

        BlockCardRequest declinedRequest = new BlockCardRequest();
        declinedRequest.setId(requestId);

        when(blockRequestService.declineRequest(requestId, null)).thenReturn(declinedRequest);

        ResponseEntity<BlockCardRequest> response = adminController.declineBlockRequest(requestId, null, principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(declinedRequest, response.getBody());

        verify(blockRequestService).declineRequest(requestId, null);
    }

    @Test
    void enforceAdmin_shouldThrowIfNotAdmin() {
        CustomUser nonAdminUser = new CustomUser();
        nonAdminUser.setRole(RoleEnum.USER);
        when(userRepo.findByEmail(principal.getName())).thenReturn(Optional.of(nonAdminUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            adminController.getAllBlockRequests(principal);
        });

        assertEquals("Access denied", ex.getMessage());
    }
}
