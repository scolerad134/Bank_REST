# Безопасность

## Компоненты безопасности

### JWT Аутентификация
- **JwtTokenProvider** - генерация и валидация JWT токенов
- **JwtAuthenticationFilter** - фильтр для проверки JWT токенов в запросах
- **CustomUserDetailsService** - загрузка пользователей для Spring Security

### Конфигурация безопасности
- **SecurityConfig** - основная конфигурация Spring Security
- **AppConfig** - конфигурация BCrypt кодировщика паролей

### Шифрование данных
- **EncryptionService** - шифрование/дешифрование номеров карт с использованием AES-GCM

## Endpoints аутентификации

### POST /api/v1/auth/login
Вход в систему:
```json
{
  "username": "user",
  "password": "password"
}
```

### POST /api/v1/auth/register
Регистрация нового пользователя:
```json
{
  "username": "newuser",
  "password": "password",
  "email": "user@example.com",
  "role": "USER"
}
```

## Использование JWT токенов

После успешной аутентификации возвращается JWT токен, который нужно передавать в заголовке:
```
Authorization: Bearer <jwt_token>
```

## Роли и доступ

- **ADMIN** - полный доступ ко всем функциям
- **USER** - доступ только к своим картам и транзакциям

## Шифрование номеров карт

Номера карт шифруются в базе данных с использованием AES-GCM алгоритма. 
В API возвращаются только маскированные номера (например: **** **** **** 1234).

## Конфигурация

В `application.yml`:
```yaml
app:
  jwt:
    secret: your-super-secret-jwt-key
    expiration: 86400000 # 24 часа
  encryption:
    key: your-super-secret-encryption-key
```
