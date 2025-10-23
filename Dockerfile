# -------------------------
# FASE DE COMPILACIÓN
# -------------------------
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Definir directorio de trabajo
WORKDIR /app

# Copiar archivos de dependencia primero para aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el resto del código y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------
# FASE DE EJECUCIÓN (Usando una imagen más ligera)
# -------------------------
# CAMBIO CLAVE: Usamos '17-jre' en lugar de '17-jre-slim'
FROM eclipse-temurin:17-jre

# Definir variables de entorno de JVM para optimización de contenedor
ENV JAVA_OPTS="-XX:+ExitOnOutOfMemoryError -XX:+UseG1GC"

# Crear un usuario no root para seguridad
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

WORKDIR /app

# Copiar el jar generado en la fase de build
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Ejecutar la app. Usar el formato ENTRYPOINT + CMD es más robusto para Docker.
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]
