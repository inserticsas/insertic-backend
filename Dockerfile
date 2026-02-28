# ══════════════════════════════════════════════════════════════
# Dockerfile - Optimizado para Render.com
# ══════════════════════════════════════════════════════════════

# ═══ STAGE 1: Build ═══
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar solo pom.xml para cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Build (skip tests en producción)
RUN mvn clean package -DskipTests -B

# ═══ STAGE 2: Runtime ═══
FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="INSERTIC SAS <info@inserticsas.com>"
LABEL version="1.0.0"
LABEL description="INSERTIC Backend API"

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC" \
    SPRING_PROFILES_ACTIVE=prod \
    TZ=America/Bogota

# Usuario no-root (seguridad)
RUN addgroup -g 1001 -S insertic && \
    adduser -u 1001 -S insertic -G insertic

WORKDIR /app

# Copiar JAR desde build stage
COPY --from=build --chown=insertic:insertic /app/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Cambiar a usuario no-root
USER insertic

# Puerto
EXPOSE 8080

# Entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]