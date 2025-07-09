# Stage 1: Build Maven project with cache
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Chỉ copy pom.xml trước để cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Sau đó copy toàn bộ source code
COPY . .

# Build project, skip test cho nhanh
RUN mvn clean package -DskipTests

# Kiểm tra file jar tồn tại
RUN echo "=== JAR output ===" && ls -l /app/target

# Stage 2: Run app
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy đúng tên jar
COPY --from=build /app/target/Project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
