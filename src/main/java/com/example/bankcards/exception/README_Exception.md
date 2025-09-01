# Обработка исключений

## Кастомные исключения

### UserNotFoundException
Выбрасывается когда пользователь не найден:
```java
throw new UserNotFoundException(id);
throw new UserNotFoundException("username", "john");
```

### CardNotFoundException
Выбрасывается когда карта не найдена:
```java
throw new CardNotFoundException(id);
throw new CardNotFoundException("cardNumber", "1234567890123456");
```

### TransactionException
Выбрасывается при ошибках транзакций:
```java
throw new TransactionException("Transaction failed");
throw new TransactionException("Transaction failed", cause);
```

### InsufficientFundsException
Выбрасывается при недостаточных средствах:
```java
throw new InsufficientFundsException(availableBalance, requestedAmount);
```

### CardExpiredException
Выбрасывается когда карта истекла:
```java
throw new CardExpiredException(expiryDate);
```

### AccessDeniedException
Выбрасывается при отказе в доступе:
```java
throw new AccessDeniedException("Access denied to card");
throw new AccessDeniedException("card", cardId);
```

## Глобальный обработчик исключений

**GlobalExceptionHandler** обрабатывает все исключения и возвращает стандартизированные ответы:

### Структура ответа об ошибке
```json
{
  "message": "Описание ошибки",
  "error": "Тип ошибки",
  "status": 400,
  "timestamp": "2024-01-01 12:00:00",
  "path": "/api/v1/cards/123",
  "details": {
    "field": "описание проблемы с полем"
  }
}
```

### HTTP статус коды
- **400** - Bad Request (валидация, недостаточные средства, истекшие карты)
- **401** - Unauthorized (неверные учетные данные)
- **403** - Forbidden (отказ в доступе)
- **404** - Not Found (пользователь/карта не найдены)
- **500** - Internal Server Error (неожиданные ошибки)

## Примеры использования

### В сервисах
```java
public UserDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    return mapToDto(user);
}
```

### Валидация данных
```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
private String username;
```

## Логирование

Все исключения логируются с соответствующими уровнями:
- **ERROR** - для критических ошибок
- **WARN** - для предупреждений
- **INFO** - для информационных сообщений
