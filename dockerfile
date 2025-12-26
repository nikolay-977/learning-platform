# Используем Amazon Corretto 17 (Amazon's OpenJDK)
FROM amazoncorretto:17-alpine AS build

WORKDIR /app

# Устанавливаем необходимые пакеты
RUN apk add --no-cache bash curl

# Копируем Gradle wrapper и файлы конфигурации
COPY gradlew .
COPY gradle gradle
RUN chmod +x ./gradlew
COPY build.gradle .
COPY settings.gradle .

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./gradlew bootJar -x test --no-daemon

# Второй этап для запуска
FROM amazoncorretto:17-alpine

WORKDIR /app

# Копируем JAR из этапа сборки
COPY --from=build /app/build/libs/learning-platform-*.jar app.jar

# Создаем пользователя для безопасного запуска
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Запуск приложения
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]