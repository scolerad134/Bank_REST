# Многоэтапная сборка
FROM openjdk:17-jdk-slim AS build

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app

# Копируем pom.xml и src
COPY pom.xml .
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

# Runtime образ
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем собранный JAR
COPY --from=build /app/target/Bank_REST-*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]
