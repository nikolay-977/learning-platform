# Собрать JAR файл
```bash
mvn clean package -DskipTests
```

# Собрать Docker образ
```bash
docker build -t learning-platform .
```

# Запустить PostgreSQL
```bash
docker run --name learning-platform-db -e POSTGRES_DB=learning_platform -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:15-alpine
```

# Запустить приложение
```bash
docker run --name learning-platform-app -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/learning_platform -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=password -p 8080:8080 learning-platform
```
# learning-platform
