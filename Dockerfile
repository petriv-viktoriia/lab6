# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Копіюємо файли для завантаження залежностей
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Завантажуємо залежності (це кешується Docker)
RUN ./gradlew build -x test --refresh-dependencies || true

# Копіюємо весь проєкт
COPY . .

# Збираємо проект без запуску тестів
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копіюємо зібраний .jar з build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Відкриваємо порт для роботи Spring Boot
EXPOSE 8080

# Команда для запуску додатку
ENTRYPOINT ["java", "-jar", "app.jar"]
