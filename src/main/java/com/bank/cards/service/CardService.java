package com.bank.cards.service;

import com.bank.cards.dto.CardDto;
import com.bank.cards.entity.Card;
import com.bank.cards.entity.User;

import java.util.List;

public interface CardService {
    
    CardDto createCard(CardDto cardDto, User owner);
    
    List<CardDto> getUserCards(User user);
    
    CardDto getCardById(Long cardId, User user);
    
    CardDto updateCardStatus(Long cardId, Card.CardStatus status, User user);
    
    void deleteCard(Long cardId, User user);
    
    List<CardDto> getAllCards(User user);
}