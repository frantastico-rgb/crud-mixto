# 🐳 Dockerfile para DemoMixto - Sistema de Gestión Híbrida
# Arquitectura multi-stage para optimizar tamaño y seguridad

# ============================================================================
# ETAPA 1: BUILD - Compilación de la aplicación
# ============================================================================
FROM maven:3.9-openjdk-17-slim AS builder

# Metadata
LABEL maintainer="frantastico-rgb"
LABEL version="1.0"
LABEL description="DemoMixto - Spring Boot Hybrid Application"

# Configurar directorio de trabajo
WORKDIR /app

# Optimización: Copiar primero pom.xml para cache de dependencias
COPY pom.xml .

# Descargar dependencias (layer cached)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación (saltando tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Verificar que el JAR se creó correctamente
RUN ls -la target/ && \
    mv target/demo-*.jar target/demomixto-app.jar

# ============================================================================
# ETAPA 2: RUNTIME - Imagen de producción optimizada
# ============================================================================
FROM openjdk:17-jdk-slim AS runtime

# Instalar herramientas útiles para debugging y monitoring
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Crear usuario no-root para seguridad
RUN groupadd --system spring && \
    useradd --system --gid spring --shell /bin/bash spring

# Configurar directorio de trabajo
WORKDIR /app

# Copiar JAR desde build stage
COPY --from=builder /app/target/demomixto-app.jar app.jar

# Crear directorio para logs
RUN mkdir -p /app/logs && \
    chown -R spring:spring /app

# Cambiar a usuario no-root
USER spring:spring

# Variables de entorno para optimización JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE=docker

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Exponer puerto de la aplicación
EXPOSE 8080

# Comando de inicio con optimizaciones JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# ============================================================================
# ETAPA 3: DEVELOPMENT - Imagen para desarrollo (opcional)
# ============================================================================
FROM builder AS development

# Instalar herramientas de desarrollo
RUN apt-get update && apt-get install -y \
    git \
    vim \
    && rm -rf /var/lib/apt/lists/*

# Exponer puerto para debugging
EXPOSE 8080 5005

# Comando para desarrollo con debug habilitado
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]