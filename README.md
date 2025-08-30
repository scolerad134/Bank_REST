# 🏦 Bank Cards Management System

Система управления банковскими картами с возможностью создания карт, переводов между ними и управления пользователями.

## 🚀 Возможности

### 👤 Пользователи
- **Создание и управление** пользователями
- **Роли**: USER и ADMIN
- **Поиск и фильтрация** пользователей

### 💳 Банковские карты
- **Создание карт** для пользователей
- **Просмотр карт** с поиском и пагинацией
- **Блокировка/активация** карт
- **Маскирование номеров** для безопасности
- **Автоматическое истечение** срока действия

### 💰 Транзакции
- **Переводы между картами** одного пользователя
- **История операций** с пагинацией
- **Валидация баланса** и статуса карт
- **Аудит всех операций**

## 🛠 Технологии

- **Java 17+**
- **Spring Boot 3.5.5**
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

## 👥 Управление пользователями

### Создание пользователя
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "USER"
  }'
```

### Получение списка пользователей
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10&sortBy=username&sortDir=asc"
```

### Поиск пользователей
```bash
curl -X GET "http://localhost:8080/api/users/search?q=test&page=0&size=10"
```

### Получение пользователей по роли
```bash
curl -X GET "http://localhost:8080/api/users/role/ADMIN?page=0&size=10"
```

## 💳 Работа с картами

### Создание карты
```bash
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{
    "cardholderName": "Иван Иванов",
    "ownerId": 1,
    "initialBalance": 10000.00
  }'
```

### Получение списка всех карт
```bash
curl -X GET "http://localhost:8080/api/cards?page=0&size=10&sortBy=createdAt&sortDir=desc"
```

### Получение карт пользователя
```bash
curl -X GET "http://localhost:8080/api/cards/user/1?page=0&size=10&status=ACTIVE"
```

### Получение карт по статусу
```bash
curl -X GET "http://localhost:8080/api/cards/status/ACTIVE?page=0&size=10"
```

### Обновление статуса карты
```bash
curl -X PUT http://localhost:8080/api/cards/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "BLOCKED"
  }'
```

### Получение просроченных карт
```bash
curl -X GET http://localhost:8080/api/cards/expired
```

### Получение карт с низким балансом
```bash
curl -X GET "http://localhost:8080/api/cards/low-balance?minBalance=100"
```

## 💰 Переводы между картами

### Выполнение перевода
```bash
curl -X POST "http://localhost:8080/api/transactions/transfer?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "fromCardId": 1,
    "toCardId": 2,
    "amount": 1000.00,
    "description": "Перевод на основную карту"
  }'
```

### История транзакций пользователя
```bash
curl -X GET "http://localhost:8080/api/transactions/user/1?page=0&size=20"
```

### Получение всех транзакций
```bash
curl -X GET "http://localhost:8080/api/transactions?page=0&size=20&sortBy=createdAt&sortDir=desc"
```

### Получение транзакций по статусу
```bash
curl -X GET "http://localhost:8080/api/transactions/status/COMPLETED?page=0&size=20"
```

### Получение транзакций по диапазону дат
```bash
curl -X GET "http://localhost:8080/api/transactions/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59&page=0&size=20"
```

### Получение всех транзакций пользователя
```bash
curl -X GET http://localhost:8080/api/transactions/user/1/all
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
│   │   ├── repository/     # Репозитории
│   │   └── service/        # Бизнес-логика
│   └── resources/
│       ├── db/migration/   # Миграции Liquibase
│       └── application.yml # Конфигурация
└── test/                   # Тесты
```

## 🚨 Безопасность

- **BCrypt** для хеширования паролей
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
