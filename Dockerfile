FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем pom.xml и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

# Создаем пользователя для безопасности
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser

# Копируем собранный JAR файл
RUN cp target/*.jar app.jar

# Меняем владельца файлов
RUN chown -R javauser:javauser /app

# Переключаемся на пользователя
USER javauser

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
