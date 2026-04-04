FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
# Sử dụng JRE 21 để chạy ứng dụng
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file jar từ stage build sang
COPY --from=build /app/target/vaccinebackend-0.0.1-SNAPSHOT.jar app.jar

# Render sẽ tự động truyền biến PORT vào, chúng ta dùng nó làm mặc định
ENV PORT=8080
EXPOSE ${PORT}

# QUAN TRỌNG: Thêm tham số -Dserver.port để Spring Boot nhận đúng port từ Render
ENTRYPOINT ["java", "-Xmx512M", "-Dserver.port=${PORT}", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]