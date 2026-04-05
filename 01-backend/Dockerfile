# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

# Copy mã nguồn và đóng gói file JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file jar từ stage build sang
COPY --from=build /app/target/vaccinebackend-0.0.1-SNAPSHOT.jar app.jar

# Khai báo biến PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT java -Xmx512M \
    -Dserver.port=${PORT} \
    -Dserver.address=0.0.0.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -jar app.jar