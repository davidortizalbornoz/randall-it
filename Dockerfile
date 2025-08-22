# Multi-stage build para Randall IT API
# Stage 1: Build stage
FROM gradle:8.5-jdk21 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle ./

# Copiar código fuente
COPY src ./src

# Ejecutar build de Gradle
RUN gradle build --no-daemon

# Stage 2: Runtime stage
FROM openjdk:21-jdk-slim-bullseye AS runtime

# Instalar dependencias del sistema
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN groupadd -r randall && useradd -r -g randall randall

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR desde el stage de build
COPY --from=build /app/build/libs/randall_it-1.0.0.jar app.jar

# Copiar archivos de recursos necesarios
COPY --from=build /app/src/main/resources/grafos_light.csv ./grafos_light.csv

# Cambiar propietario de archivos
RUN chown -R randall:randall /app

# Cambiar al usuario no-root
USER randall

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE="docker"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/grafo/estado || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
