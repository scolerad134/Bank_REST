package com.bank.cards.controller;

import com.bank.cards.dto.CardDto;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.User;
import com.bank.cards.security.JwtUtil;
import com.bank.cards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");

        testCardDto = new CardDto();
        testCardDto.setId(1L);
        testCardDto.setCardHolderName("Test User");
        testCardDto.setExpiryDate(YearMonth.of(2027, 12));
        testCardDto.setBalance(BigDecimal.valueOf(1000.00));
        testCardDto.setStatus(Card.CardStatus.ACTIVE);
        testCardDto.setCardType(Card.CardType.DEBIT);
        testCardDto.setOwnerUsername("testuser");
        testCardDto.setMaskedCardNumber("** ** 3456");
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserCards_Success() throws Exception {
        // Given
        List<CardDto> cards = Arrays.asList(testCardDto);
        when(cardService.getUserCards(any(User.class))).thenReturn(cards);

        // When & Then
        mockMvc.perform(get("/cards")
                .with(authentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cardHolderName").value("Test User"))
                .andExpect(jsonPath("$[0].maskedCardNumber").value("** ** 3456"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createCard_Success() throws Exception {
        // Given
        CardDto createRequest = new CardDto();
        createRequest.setCardHolderName("Test User");
        createRequest.setExpiryDate(YearMonth.of(2027, 12));
        createRequest.setCardType(Card.CardType.DEBIT);

        when(cardService.createCard(any(CardDto.class), any(User.class))).thenReturn(testCardDto);

        // When & Then
        mockMvc.perform(post("/cards")
                .with(authentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardHolderName").value("Test User"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCardById_Success() throws Exception {
        // Given
        when(cardService.getCardById(eq(1L), any(User.class))).thenReturn(testCardDto);

        // When & Then
        mockMvc.perform(get("/cards/1")
                .with(authentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardHolderName").value("Test User"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCardStatus_Success() throws Exception {
        // Given
        testCardDto.setStatus(Card.CardStatus.BLOCKED);
        when(cardService.updateCardStatus(eq(1L), eq(Card.CardStatus.BLOCKED), any(User.class))).thenReturn(testCardDto);

        // When & Then
        mockMvc.perform(put("/cards/1/status")
                .with(authentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .param("status", "BLOCKED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteCard_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/cards/1")
                .with(authentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities()))))
                .andExpect(status().isNoContent());
    }
}