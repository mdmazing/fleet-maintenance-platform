# ── Multi-stage build ─────────────────────────────────────────────────────────
# Stage 1 compiles the app. Stage 2 runs it.
# Result: the final image only contains the JRE + the JAR — not Maven, not source code.
# This keeps the image small and without build tools in prod (security best practice).

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper first — Docker caches this layer separately.
# If only source changes, Docker reuses the cached dependency download layer.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw package -DskipTests -q

# ── Runtime image ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
