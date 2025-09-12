FROM openjdk:17-jdk-slim

LABEL maintainer="Bank Development Team <dev@bank.com>"
LABEL description="Bank Cards Management System"

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Create logs directory
RUN mkdir -p /app/logs

# Run application
EXPOSE 8080
CMD ["java", "-jar", "target/cards-1.0.0.jar"]