# 🏦 Bank Cards Management System

Система управления банковскими картами с возможностью создания карт, переводов между ними и управления пользователями.

## 🚀 Возможности

### 👤 Пользователи
- **Регистрация и аутентификация** через JWT токены
- **Роли**: USER и ADMIN
- **Управление профилем**

### 💳 Банковские карты
- **Создание карт** (только для админов)
- **Просмотр своих карт** с поиском и пагинацией
- **Блокировка/активация** карт
- **Маскирование номеров** для безопасности
- **Автоматическое истечение** срока действия

### 💰 Транзакции
- **Переводы между своими картами**
- **История операций** с пагинацией
- **Валидация баланса** и статуса карт
- **Аудит всех операций**

## 🛠 Технологии

- **Java 17+**
- **Spring Boot 3.5.5**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase** для миграций
- **Docker Compose**
- **Swagger/OpenAPI**

## 📋 Требования

- Java 17+
- Maven 3.6+
- Docker и Docker Compose
- PostgreSQL (если запуск без Docker)

## 🚀 Быстрый старт

### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd bank-application
```

### 2. Запуск через Docker Compose
```bash
# Запуск всех сервисов
docker-compose up -d

# Проверка статуса
docker-compose ps
```

### 3. Запуск приложения
```bash
# Сборка проекта
mvn clean install

# Запуск Spring Boot приложения
mvn spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8080

## 📚 API Документация

После запуска приложения Swagger UI доступен по адресу:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔐 Аутентификация

### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "USER"
  }'
```

### Вход в систему
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

## 💳 Работа с картами

### Создание карты (требует роль ADMIN)
```bash
curl -X POST http://localhost:8080/api/cards \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "cardholderName": "Иван Иванов",
    "ownerId": 1
  }'
```

### Получение списка карт
```bash
curl -X GET "http://localhost:8080/api/cards?page=0&size=10" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### Обновление статуса карты
```bash
curl -X PUT http://localhost:8080/api/cards/1/status \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "BLOCKED"
  }'
```

## 💰 Переводы между картами

### Выполнение перевода
```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "fromCardId": 1,
    "toCardId": 2,
    "amount": 1000.00,
    "description": "Перевод на основную карту"
  }'
```

### История транзакций
```bash
curl -X GET "http://localhost:8080/api/transactions/history?page=0&size=20" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 🗄 База данных

### Структура таблиц
- **users** - пользователи системы
- **bank_cards** - банковские карты
- **transactions** - транзакции между картами

### Миграции
Все изменения схемы БД выполняются через Liquibase:
- Файлы миграций: `src/main/resources/db/migration/`
- Автоматическое применение при запуске

## 🧪 Тестирование

### Запуск тестов
```bash
# Все тесты
mvn test

# Только unit тесты
mvn test -Dtest=*ServiceTest

# Только integration тесты
mvn test -Dtest=*ControllerTest
```

### Покрытие кода
```bash
mvn jacoco:report
```

## 🔧 Конфигурация

### Основные настройки
Файл: `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankdb
    username: bankuser
    password: bankpass
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  liquibase:
    change-log: classpath:db/migration/changelog.xml

logging:
  level:
    com.example.bankcards: DEBUG
```

### Переменные окружения
- `DB_URL` - URL базы данных
- `DB_USERNAME` - имя пользователя БД
- `DB_PASSWORD` - пароль БД
- `JWT_SECRET` - секретный ключ для JWT

## 🐳 Docker

### Сборка образа
```bash
docker build -t bank-app .
```

### Запуск контейнера
```bash
docker run -p 8080:8080 bank-app
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:postgresql://db:5432/bankdb
    depends_on:
      - db
  
  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=bankdb
      - POSTGRES_USER=bankuser
      - POSTGRES_PASSWORD=bankpass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

## 📁 Структура проекта

```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── config/          # Конфигурации
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA сущности
│   │   ├── exception/      # Кастомные исключения
│   │   ├── repository/     # Репозитории
│   │   ├── security/       # Безопасность
│   │   └── service/        # Бизнес-логика
│   └── resources/
│       ├── db/migration/   # Миграции Liquibase
│       └── application.yml # Конфигурация
└── test/                   # Тесты
```

## 🚨 Безопасность

- **JWT токены** для аутентификации
- **BCrypt** для хеширования паролей
- **Ролевой доступ** к ресурсам
- **Валидация входных данных**
- **Маскирование** номеров карт
- **Аудит** всех операций

## 🔍 Мониторинг и логирование

- **Structured logging** с SLF4J
- **Логирование** всех важных операций
- **Метрики** Spring Boot Actuator
- **Health checks** для БД

## 🚀 Развертывание

### Production
1. Настройка переменных окружения
2. Настройка SSL/TLS
3. Настройка мониторинга
4. Backup стратегия для БД

### CI/CD
- Автоматические тесты
- Сборка Docker образа
- Развертывание в staging/production

## 🤝 Вклад в проект

1. Fork репозитория
2. Создание feature ветки
3. Внесение изменений
4. Написание тестов
5. Pull Request

## 📄 Лицензия

MIT License

## 📞 Поддержка

- **Email**: dev@bank.com
- **Issues**: GitHub Issues
- **Документация**: [Wiki](link-to-wiki)

---

**Версия**: 1.0.0  
**Последнее обновление**: 2024
