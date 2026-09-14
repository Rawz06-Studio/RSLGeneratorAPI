# Multi-stage build for RSLFrancoBot
# Stage 1: Build the Java application
FROM maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image with Java + Python
FROM eclipse-temurin:25-jre

WORKDIR /app

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

# uv + Python 3.14 (installed system-wide, accessible to all users)
COPY --from=ghcr.io/astral-sh/uv:latest /uv /usr/local/bin/uv

ENV UV_PYTHON_INSTALL_DIR=/opt/python \
    UV_PYTHON_BIN_DIR=/usr/local/bin

RUN uv python install 3.14 --default --preview \
    && uv pip install --system --break-system-packages requests

# Clone plando-random-settings at specific commit
RUN git clone https://github.com/matthewkirby/plando-random-settings.git ./plando-random-settings \
    && git -C plando-random-settings checkout 240cdc5

# Pre-compile all Python modules to .pyc for faster startup
RUN python3.14 -m compileall -b ./plando-random-settings/ && \
    find ./plando-random-settings -name "*.pyc" -type f && \
    du -sh ./plando-random-settings/__pycache__ 2>/dev/null || echo "Compilation done"

# Copy custom weight files
COPY weights/ ./plando-random-settings/weights/

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Create non-root user for security
RUN groupadd -g 10000 botuser && \
    useradd -m -u 10000 -g botuser botuser && \
    chown -R botuser:botuser /app

USER botuser

# Expose health check port (if needed)
EXPOSE 8080

# Set environment variables (override via docker-compose or -e flags)
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE="prod"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]