## Multi-stage build for Spring Boot app
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the backend module to optimize build context
COPY backend/pom.xml ./pom.xml
COPY backend/src ./src

# Build application (skip tests for image build speed)
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS=""

# Copy Jar from build stage
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# Healthcheck using actuator
HEALTHCHECK --interval=5s --timeout=3s --start-period=15s --retries=20 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

