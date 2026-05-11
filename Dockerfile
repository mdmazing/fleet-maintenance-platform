# ── Multi-stage build ─────────────────────────────────────────────────────────
# Stage 1 compiles the app. Stage 2 runs it.
# Result: the final image only contains the JRE + the JAR — not Maven, not source code.
# This keeps the image small and without build tools in prod (security best practice).

FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first — Docker caches this layer separately from source.
# If only source changes, Docker reuses the cached dependency download layer.
COPY pom.xml ./
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Runtime image ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]