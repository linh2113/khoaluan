# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy file pom.xml trước để cache dependencies
COPY pom.xml ./
RUN mvn -q dependency:go-offline

# Copy src và build ra file jar bằng lệnh mvn 
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy file jar từ stage build sang
COPY --from=build /app/target/tech-store-0.0.1-SNAPSHOT.jar app.jar

# Tạo user không phải root để bảo mật 
RUN useradd -ms /bin/bash appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]