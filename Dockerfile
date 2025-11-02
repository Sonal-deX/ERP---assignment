# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests
# Runtime stage
FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
# Create non-privileged user
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid 10001 \
    appuser
# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar
# Set ownership
RUN chown appuser:appuser app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
