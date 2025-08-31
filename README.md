# 🚀 Bank_REST - Система Управления Банковскими Картами

## 📋 Описание проекта

Bank_REST - это RESTful API для управления банковскими картами, пользователями и транзакциями. Система предоставляет полный набор функций для работы с банковскими картами, включая создание, блокировку, активацию и переводы между картами.

## ✨ Основные возможности

### 💳 Управление картами
- Создание новых банковских карт
- Просмотр информации о картах
- Блокировка и активация карт
- Управление статусом карт (Активна, Заблокирована, Истек срок)

### 👥 Управление пользователями
- Регистрация новых пользователей
- Управление ролями (ADMIN, USER)
- Просмотр и редактирование профилей

### 💰 Транзакции
- Переводы между своими картами
- История транзакций
- Статусы транзакций (PENDING, COMPLETED, FAILED, CANCELLED)

### 🔒 Безопасность
- Шифрование номеров карт
- Маскирование данных для отображения
- Ролевой доступ к функциям

## 🛠 Технологии

- **Java 17+**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Liquibase** (миграции БД)
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3.0**
- **Maven**
- **Lombok**

## 📋 Требования

- Java 17 или выше
- Maven 3.6+
- Docker и Docker Compose
- PostgreSQL 15 (если запуск без Docker)

## 🚀 Быстрый старт

### Вариант 1: Docker Compose (рекомендуется)

```bash
# Клонировать репозиторий
git clone <repository-url>
cd Bank_REST

# Запустить приложение и базу данных
docker compose up -d

# Приложение будет доступно по адресу: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Вариант 2: Локальный запуск

```bash
# Запустить только базу данных
docker run -d --name postgres-bank \
  -e POSTGRES_DB=bankdb \
  -e POSTGRES_USER=bankuser \
  -e POSTGRES_PASSWORD=bankpass \
  -p 5433:5432 postgres:15-alpine

# Запустить приложение
mvn spring-boot:run
```

## 📚 API Документация

После запуска приложения API документация доступна по адресам:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## 🔐 Аутентификация и роли

### Роли пользователей:
- **ADMIN**: Полный доступ ко всем функциям
- **USER**: Ограниченный доступ к своим картам и транзакциям

### Примеры запросов:

#### Создание пользователя
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "USER"
  }'
```

#### Создание карты
```bash
curl -X POST http://localhost:8080/api/v1/cards \
  -H "Content-Type: application/json" \
  -d '{
    "ownerId": 1,
    "cardholderName": "John Doe",
    "initialBalance": 1000.00
  }'
```

#### Перевод между картами
```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "fromCardId": 1,
    "toCardId": 2,
    "amount": 100.00,
    "description": "Перевод на другую карту"
  }'
```

## 🗄 Структура базы данных

### Таблицы:
- **users** - пользователи системы
- **bank_cards** - банковские карты
- **transactions** - транзакции между картами

### Основные поля:
- **users**: id, username, password, email, role, enabled, created_at, updated_at
- **bank_cards**: id, card_number, masked_number, owner_id, cardholder_name, expiry_date, status, balance, created_at, updated_at
- **transactions**: id, from_card_id, to_card_id, amount, status, description, created_at

## 🧪 Тестирование

```bash
# Запуск всех тестов
mvn test

# Запуск тестов с отчетом
mvn test jacoco:report
```

## ⚙️ Конфигурация

Основные настройки в `src/main/resources/application.yml`:

- **Порт сервера**: 8080
- **База данных**: PostgreSQL на localhost:5433
- **Liquibase**: включен для автоматических миграций
- **Логирование**: настраиваемые уровни для разных компонентов

## 🐳 Docker

### Сборка образа
```bash
docker build -t bank-rest .
```

### Запуск контейнера
```bash
docker run -p 8080:8080 bank-rest
```

## 📁 Структура проекта

```
Bank_REST/
├── src/
│   ├── main/
│   │   ├── java/com/example/bankcards/
│   │   │   ├── controller/     # REST контроллеры
│   │   │   ├── service/        # Бизнес-логика
│   │   │   ├── repository/     # Доступ к данным
│   │   │   ├── entity/         # JPA сущности
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   └── config/         # Конфигурация
│   │   └── resources/
│   │       ├── db/migration/   # Liquibase миграции
│   │       └── application.yml # Конфигурация
│   └── test/                   # Тесты
├── docs/                       # Документация
├── Dockerfile                  # Docker образ
├── docker-compose.yml          # Docker Compose
└── pom.xml                     # Maven конфигурация
```

## 🔒 Безопасность

- Пароли хешируются с использованием SHA-256
- Номера карт шифруются в базе данных
- Отображаются только маскированные номера карт
- Ролевой доступ к API endpoints

## 📊 Мониторинг и логирование

- Подробное логирование всех операций
- Логи сохраняются в файл `logs/bank-app.log`
- Настраиваемые уровни логирования для разных компонентов
- Автоматическая ротация логов

## 🚀 Развертывание

### Production окружение:
```bash
# Установить переменные окружения
export DB_URL=jdbc:postgresql://your-db-host:5432/bankdb
export DB_USERNAME=your-username
export DB_PASSWORD=your-password

# Запустить с production профилем
java -jar -Dspring.profiles.active=prod target/Bank_REST-0.0.1-SNAPSHOT.jar
```

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку для новой функции (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'Add amazing feature'`)
4. Отправьте в ветку (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект распространяется под лицензией MIT. См. файл `LICENSE` для получения дополнительной информации.

## 📞 Поддержка

Если у вас есть вопросы или предложения, создайте Issue в репозитории или свяжитесь с командой разработки.

---

**Bank_REST** - современное решение для управления банковскими картами! 🎉
