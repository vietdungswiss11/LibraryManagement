   # Stage 1: Build app với Maven và JDK 21
   FROM maven:3.9.6-eclipse-temurin-21 AS build
   WORKDIR /app
   COPY . .
   RUN mvn clean package -DskipTests

   # Stage 2: Chạy app với JDK 21
   FROM eclipse-temurin:21-jdk
   WORKDIR /app
   COPY --from=build /app/target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]