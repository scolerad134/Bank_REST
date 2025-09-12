# Bank Cards Management System

Система управления банковскими картами - RESTful API для управления банковскими картами, пользователями и транзакциями.

## 🚀 Функциональность

- **Управление пользователями**: регистрация, аутентификация через JWT
- **Управление картами**: создание, просмотр, изменение статуса, удаление
- **Переводы**: переводы между собственными картами пользователя  
- **История транзакций**: просмотр истории операций с пагинацией
- **Безопасность**: маскирование номеров карт (** ** 1234)
- **Документация**: Swagger UI / OpenAPI

## 🛠 Технологический стек

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** с JWT аутентификацией
- **Spring Data JPA** 
- **PostgreSQL** (основная БД)
- **H2** (для тестов)
- **Liquibase** для миграций БД
- **Docker & Docker Compose**
- **Swagger/OpenAPI** для документации API
- **JUnit 5 & Mockito** для тестирования

## 📋 Предварительные требования

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (для запуска с контейнерами)
- PostgreSQL 15+ (для локального запуска без Docker)

## 🚀 Быстрый старт

### Вариант 1: Запуск с Docker Compose (рекомендуется)

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd cards
```

2. Запустите приложение:
```bash
docker-compose up -d
```

3. Приложение будет доступно по адресу: http://localhost:8080/api

### Вариант 2: Локальный запуск

1. Установите и запустите PostgreSQL:
```bash
# Создайте базу данных
createdb bank_cards
```

2. Настройте подключение в `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bank_cards
    username: your_username
    password: your_password
```

3. Запустите приложение:
```bash
./mvnw spring-boot:run
```

## 📚 API Документация

После запуска приложения документация API будет доступна по адресам:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs
- **Статическая документация**: [docs/openapi.yaml](docs/openapi.yaml)

## 🔐 Аутентификация

API использует JWT токены для аутентификации. 

### Регистрация пользователя
```bash
POST /api/auth/register
Content-Type: application/json

{
    \"username\": \"john_doe\",
    \"password\": \"password123\",
    \"fullName\": \"John Doe\", 
    \"email\": \"john@example.com\"
}
```

### Вход в систему
```bash
POST /api/auth/login
Content-Type: application/json

{
    \"username\": \"john_doe\",
    \"password\": \"password123\"
}
```

Ответ содержит JWT токен:
```json
{
    \"token\": \"eyJhbGciOiJIUzUxMiJ9...\",
    \"username\": \"john_doe\",
    \"fullName\": \"John Doe\"
}
```

### Использование токена
Добавляйте токен в заголовок Authorization для защищенных эндпоинтов:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 💳 Примеры использования API

### Создание карты
```bash
POST /api/cards
Authorization: Bearer <token>
Content-Type: application/json

{
    \"cardHolderName\": \"John Doe\",
    \"cardType\": \"DEBIT\",
    \"expiryDate\": \"2027-12\"
}
```

### Просмотр карт пользователя
```bash
GET /api/cards
Authorization: Bearer <token>
```

### Перевод между картами
```bash
POST /api/transactions/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
    \"fromCardId\": 1,
    \"toCardId\": 2,
    \"amount\": 100.00,
    \"description\": \"Перевод на сберегательную карту\"
}
```

### История транзакций
```bash
GET /api/transactions?page=0&size=10
Authorization: Bearer <token>
```

## 🗄️ База данных

Проект использует Liquibase для управления схемой БД. Миграции находятся в `src/main/resources/db/changelog/`.

### Основные таблицы:
- `users` - пользователи системы
- `cards` - банковские карты
- `transactions` - история транзакций

### Тестовые данные
При первом запуске создается тестовый пользователь:
- **Username**: `testuser` 
- **Password**: `password123`
- **Email**: `test@example.com`

## 🧪 Тестирование

### Запуск всех тестов
```bash
./mvnw test
```

### Запуск только unit-тестов
```bash
./mvnw test -Dtest=\"*Test\"
```

### Проверка покрытия
```bash
./mvnw jacoco:report
```

## 🐳 Docker

### Сборка образа
```bash
docker build -t bank-cards-app .
```

### Переменные окружения
- `SPRING_PROFILES_ACTIVE` - активный профиль (docker/prod)
- `SPRING_DATASOURCE_URL` - URL базы данных
- `SPRING_DATASOURCE_USERNAME` - пользователь БД
- `SPRING_DATASOURCE_PASSWORD` - пароль БД
- `JWT_SECRET` - секретный ключ для JWT
- `JWT_EXPIRATION` - время жизни JWT токена (мс)

## 🔧 Конфигурация

### Основные настройки в application.yml:
- **Порт**: 8080
- **Context path**: /api
- **JWT срок действия**: 24 часа
- **Логирование**: DEBUG для пакета com.bank.cards

### Профили:
- `default` - для локальной разработки
- `test` - для тестов (H2 БД)
- `docker` - для запуска в контейнере

## 📊 Мониторинг

Приложение включает Spring Boot Actuator:
- **Health check**: http://localhost:8080/api/actuator/health
- **Info**: http://localhost:8080/api/actuator/info

## 🛡️ Безопасность

### Реализованные меры безопасности:
- JWT токены для аутентификации
- Маскирование номеров карт в ответах API
- Валидация входных данных
- CORS настройки
- Защита от SQL-инъекций через JPA
- Хеширование паролей с BCrypt

### Рекомендации для продакшена:
- Используйте HTTPS
- Настройте строгую JWT политику
- Добавьте rate limiting
- Настройте мониторинг и логирование
- Используйте внешний key management для секретов

## 🚦 Статусы и коды ошибок

### HTTP статусы:
- `200 OK` - успешный запрос
- `201 Created` - ресурс создан
- `204 No Content` - ресурс удален
- `400 Bad Request` - некорректный запрос
- `401 Unauthorized` - требуется аутентификация  
- `404 Not Found` - ресурс не найден
- `500 Internal Server Error` - серверная ошибка

## 📝 Лицензия

Этот проект распространяется под лицензией MIT. См. файл [LICENSE](LICENSE) для подробностей.

## 👥 Команда разработки

- **Email**: dev@bank.com
- **GitHub**: [Bank Development Team](https://github.com/bank-team)

## 🤝 Содействие

1. Форкните проект
2. Создайте feature branch (`git checkout -b feature/AmazingFeature`)
3. Закоммитьте изменения (`git commit -m 'Add some AmazingFeature'`)
4. Запушьте в branch (`git push origin feature/AmazingFeature`)
5. Создайте Pull Request

## 📈 Roadmap

- [ ] Интеграция с внешними платежными системами
- [ ] Добавление push-уведомлений
- [ ] Реализация лимитов по картам
- [ ] Добавление категорий трат
- [ ] Мобильное приложение
- [ ] Админ-панель

---

**Note**: Это демонстрационный проект для изучения Spring Boot и современных практик разработки. Не используйте в продакшене без дополнительных мер безопасности.