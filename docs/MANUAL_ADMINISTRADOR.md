## 📚 **MANUAL DE ADMINISTRADOR - DemoMixto**

### 🚀 **Introducción**

DemoMixto es una aplicación Spring Boot con **arquitectura híbrida** que gestiona empleados (MySQL) y proyectos (MongoDB). Este manual está dirigido a administradores de sistemas y DevOps engineers responsables del deployment y mantenimiento.

---

### ⚙️ **ARQUITECTURA DEL SISTEMA**

```
┌─────────────────────────────────────────────────┐
│               FRONTEND LAYER                     │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │   Thymeleaf     │  │     REST API            ││
│  │   Templates     │  │   (JSON Endpoints)      ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────┐
│              BUSINESS LAYER                      │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │ EmpleadoService │  │   ProyectoService       ││
│  │   (Business     │  │   (Business Logic)      ││
│  │     Logic)      │  │                         ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────┐
│             PERSISTENCE LAYER                    │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │   MySQL 8.0+    │  │   MongoDB Atlas         ││
│  │   (Empleados)   │  │   (Proyectos)           ││
│  │   JPA/Hibernate │  │   Spring Data Mongo     ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
```

---

### 🔧 **CONFIGURACIÓN DEL ENTORNO**

#### **1. Pre-requisitos del Sistema**

```bash
# Java JDK 17+
java -version
# openjdk version "17.0.x"

# Maven 3.8+
mvn -version

# Docker (opcional para containerización)
docker --version

# Base de Datos
# - MySQL 8.0+ (local o remoto)
# - MongoDB Atlas (cuenta gratuita disponible)
```

#### **2. Variables de Entorno Críticas**

```properties
# === DATABASE CONFIGURATION ===

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/empresa?createDatabaseIfNotExist=true
spring.datasource.username=${MYSQL_USERNAME:root}
spring.datasource.password=${MYSQL_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MongoDB Configuration
spring.data.mongodb.uri=${MONGODB_URI:mongodb+srv://username:password@cluster.mongodb.net/empresa}

# === SECURITY CONFIGURATION ===
app.admin.username=${ADMIN_USERNAME:admin}
app.admin.password=${ADMIN_PASSWORD:admin}
app.user.username=${USER_USERNAME:user}
app.user.password=${USER_PASSWORD:password}

# === APPLICATION CONFIGURATION ===
server.port=${PORT:8080}
spring.application.name=DemoMixto
logging.level.com.miAplicacion.demo=${LOG_LEVEL:INFO}

# === PRODUCTION SETTINGS ===
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}
spring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}
```

#### **3. Configuración de Producción**

**application-prod.properties:**
```properties
# Disable dev tools in production
spring.devtools.restart.enabled=false
spring.devtools.livereload.enabled=false

# Security hardening
server.error.include-stacktrace=never
server.error.include-message=never

# Database optimization
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Connection pooling
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000

# Logging for production
logging.level.root=WARN
logging.level.com.miAplicacion.demo=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

---

### 🐳 **DEPLOYMENT CON DOCKER**

#### **1. Docker Compose para Desarrollo**

```yaml
# docker-compose.yml
version: '3.8'

services:
  # MySQL Database
  mysql-db:
    image: mysql:8.0
    container_name: demomixto-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: empresa
      MYSQL_USER: demomixto_user
      MYSQL_PASSWORD: demomixto_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - demomixto-network

  # DemoMixto Application
  demomixto-app:
    build: .
    container_name: demomixto-app
    environment:
      SPRING_PROFILES_ACTIVE: docker
      MYSQL_USERNAME: demomixto_user
      MYSQL_PASSWORD: demomixto_pass
      MONGODB_URI: mongodb+srv://username:password@cluster.mongodb.net/empresa
    ports:
      - "8080:8080"
    depends_on:
      - mysql-db
    networks:
      - demomixto-network

volumes:
  mysql_data:

networks:
  demomixto-network:
    driver: bridge
```

#### **2. Dockerfile Optimizado**

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim AS builder

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy source code
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/demo-*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appuser /app
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### 🚀 **DEPLOYMENT STRATEGIES**

#### **1. Heroku Deployment**

```bash
# 1. Install Heroku CLI
# 2. Login to Heroku
heroku login

# 3. Create Heroku app
heroku create your-demomixto-app

# 4. Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/empresa"
heroku config:set MYSQL_USERNAME=your_mysql_user
heroku config:set MYSQL_PASSWORD=your_mysql_password

# 5. Add MySQL addon (ClearDB)
heroku addons:create cleardb:ignite

# 6. Deploy
git push heroku main

# 7. Check logs
heroku logs --tail
```

#### **2. AWS EC2 Deployment**

```bash
# 1. Launch EC2 instance (t3.micro for testing)
# 2. Install Java and Docker
sudo yum update -y
sudo yum install -y docker java-17-openjdk-devel

# 3. Start Docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user

# 4. Deploy using Docker Compose
scp docker-compose.yml ec2-user@your-ec2-ip:~/
ssh ec2-user@your-ec2-ip
docker-compose up -d

# 5. Configure reverse proxy (nginx)
sudo yum install -y nginx
# Configure nginx to proxy port 8080
```

#### **3. Kubernetes Deployment**

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demomixto-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: demomixto
  template:
    metadata:
      labels:
        app: demomixto
    spec:
      containers:
      - name: demomixto
        image: your-registry/demomixto:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: MONGODB_URI
          valueFrom:
            secretKeyRef:
              name: demomixto-secrets
              key: mongodb-uri
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: demomixto-service
spec:
  selector:
    app: demomixto
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

### 📊 **MONITOREO Y LOGGING**

#### **1. Health Checks**

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connectivity
curl http://localhost:8080/actuator/health/db

# Custom health indicators
curl http://localhost:8080/api/empleados/health
```

#### **2. Logs Configuration**

```properties
# logback-spring.xml location: src/main/resources/
logging.config=classpath:logback-spring.xml

# Application logs
logging.level.com.miAplicacion.demo=INFO
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# File appender for production
logging.file.name=logs/demomixto.log
logging.file.max-size=50MB
logging.file.max-history=30
```

#### **3. Monitoring Endpoints**

| Endpoint | Descripción | Uso |
|----------|-------------|-----|
| `/actuator/health` | Estado general de la aplicación | Load balancer health checks |
| `/actuator/metrics` | Métricas de la aplicación | Prometheus integration |
| `/actuator/info` | Información de la aplicación | Version tracking |
| `/actuator/loggers` | Configuración de logs | Dynamic log level changes |

---

### 🔒 **SECURITY & BACKUP**

#### **1. Security Checklist**

- ✅ **Authentication**: HTTP Basic Auth habilitado
- ✅ **Authorization**: Roles admin/user configurados
- ✅ **HTTPS**: Configurar SSL en producción
- ✅ **Database**: Conexiones encriptadas
- ✅ **Secrets**: Variables de entorno para credenciales
- ✅ **CORS**: Configurado para dominios específicos
- ✅ **Rate Limiting**: Implementar en proxy reverso

#### **2. Backup Strategy**

```bash
# MySQL Backup
mysqldump -h localhost -u root -p empresa > backup_$(date +%Y%m%d_%H%M%S).sql

# MongoDB Backup (Atlas)
# Use MongoDB Atlas automated backups
# Or use mongodump for manual backups
mongodump --uri="mongodb+srv://username:password@cluster.mongodb.net/empresa"

# Application Logs Backup
tar -czf logs_backup_$(date +%Y%m%d).tar.gz logs/
```

#### **3. Disaster Recovery**

1. **Database Recovery**:
   - MySQL: Restore from SQL dump
   - MongoDB: Use Atlas point-in-time recovery

2. **Application Recovery**:
   - Redeploy from Git repository
   - Restore configuration from backup
   - Verify health checks

3. **Data Validation**:
   - Run integration tests
   - Verify data consistency
   - Check API endpoints

---

### 🚨 **TROUBLESHOOTING COMÚN**

#### **1. Connection Issues**

```bash
# MySQL Connection Test
mysql -h localhost -u root -p -e "SELECT 1;"

# MongoDB Connection Test
mongo "mongodb+srv://cluster.mongodb.net/test" --username username

# Application Connection Test
curl -u admin:admin http://localhost:8080/api/empleados
```

#### **2. Performance Issues**

```bash
# Check JVM memory usage
jcmd <pid> GC.run_finalization
jcmd <pid> VM.classloader_stats

# Database performance
SHOW PROCESSLIST; # MySQL
db.currentOp(); # MongoDB

# Application metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

#### **3. Common Errors**

| Error | Causa | Solución |
|-------|-------|----------|
| `Connection refused` | Base de datos no disponible | Verificar conectividad y credenciales |
| `401 Unauthorized` | Credenciales incorrectas | Verificar admin:admin, user:password |
| `OutOfMemoryError` | Heap insuficiente | Aumentar `-Xmx` parameter |
| `ConstraintViolationException` | Email duplicado | Verificar unicidad en empleados |

---

### 📞 **SOPORTE Y CONTACTO**

- **Documentación Técnica**: [Swagger UI] http://localhost:8080/swagger-ui.html
- **Repositorio**: https://github.com/your-repo/demomixto
- **Issues**: https://github.com/your-repo/demomixto/issues
- **Wiki**: https://github.com/your-repo/demomixto/wiki

---

**© 2025 DemoMixto Development Team**