# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline
COPY src ./src
RUN ./mvnw -q clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# copy jar từ stage build
COPY --from=build /app/target/tech-store-0.0.1-SNAPSHOT.jar app.jar

# (tùy chọn) tạo user không phải root 
RUN useradd -ms /bin/bash appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
