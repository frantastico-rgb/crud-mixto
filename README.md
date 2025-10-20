# 🚀 DemoMixto - Sistema de Gestión Híbrida

> **Aplicación Spring Boot con arquitectura dual de persistencia: MySQL + MongoDB Atlas**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?style=flat-square&logo=mongodb&logoColor=white)](https://www.mongodb.com/atlas)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)

**DemoMixto** es una aplicación de demostración que implementa un **sistema híbrido de gestión empresarial** con persistencia dual, combinando MySQL para datos estructurados (empleados) y MongoDB Atlas para documentos flexibles (proyectos y tareas).

---

## 📑 **Tabla de Contenidos**

- [🎯 Características Principales](#-características-principales)
- [🏗️ Arquitectura](#️-arquitectura)
- [💾 Modelo de Datos](#-modelo-de-datos)
- [🚀 Instalación y Configuración](#-instalación-y-configuración)
- [▶️ Ejecución](#️-ejecución)
- [🌐 Endpoints y API](#-endpoints-y-api)
- [🔐 Seguridad](#-seguridad)
- [📱 Interfaces Web](#-interfaces-web)
- [🧪 Testing con Thunder Client](#-testing-con-thunder-client)
- [� Containerización con Docker](#-containerización-con-docker)
- [�📊 Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [📂 Estructura del Proyecto](#-estructura-del-proyecto)
- [🤝 Contribuciones](#-contribuciones)

---

## 🎯 **Características Principales**

### ✨ **Funcionalidades Core**
- 👥 **Gestión de Empleados** (MySQL/JPA)
  - CRUD completo con validaciones
  - Búsqueda avanzada por nombre, cargo y rango salarial
  - Reportes en Excel exportables
  - Validación de emails únicos

- 📋 **Gestión de Proyectos** (MongoDB Atlas)
  - Sistema de proyectos con documentos flexibles
  - Gestión de tareas embebidas por proyecto
  - Estadísticas en tiempo real
  - Estados de proyecto y tareas

### 🔧 **Características Técnicas**
- **🏗️ Arquitectura Dual**: MySQL + MongoDB en una sola aplicación
- **🌐 Híbrido MVC/REST**: Interfaces web + API JSON
- **🔐 Seguridad Diferenciada**: Empleados requieren auth, proyectos públicos
- **📱 Responsive**: Interfaces web con Bootstrap integrado
- **⚡ Performance**: Repositorios optimizados con Spring Data

---

## 🏗️ **Arquitectura**

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                        │
├─────────────────────┬───────────────────────────────────────────┤
│   🌐 Web MVC        │           📱 REST API                    │
│                     │                                           │
│ • empleados.html    │     /api/empleados/**                    │
│ • proyectos.html    │     /api/proyectos/**                    │  
│ • Thymeleaf + CSS   │     JSON Responses                       │
├─────────────────────┴───────────────────────────────────────────┤
│                    CAPA DE CONTROLADORES                       │
├─────────────────────────────────────────────────────────────────┤
│ EmpleadoController  │  ProyectoController  │  HomeController    │
├─────────────────────────────────────────────────────────────────┤
│                     CAPA DE NEGOCIO                            │
├─────────────────────────────────────────────────────────────────┤
│ EmpleadoService     │         ProyectoService                   │
├─────────────────────────────────────────────────────────────────┤
│                   CAPA DE PERSISTENCIA                         │
├──────────────────────────┬──────────────────────────────────────┤
│     📊 MySQL             │         🍃 MongoDB Atlas             │
│                          │                                      │
│ EmpleadoRepository       │    ProyectoRepository                │
│ JpaRepository<Long>      │    MongoRepository<String>           │
│                          │                                      │
│ 🗃️ Empleado Entity       │    📄 Proyecto Document             │
│ • ID Auto-generado       │    • ID ObjectId                    │
│ • Datos estructurados    │    • Tareas embebidas               │
└──────────────────────────┴──────────────────────────────────────┘
```

### 🔗 **Patrones Implementados**
- **🏗️ MVC Architecture**: Separación clara de responsabilidades
- **📦 Repository Pattern**: Abstracción de acceso a datos
- **🏭 Dependency Injection**: IoC con Spring Framework
- **🎯 Strategy Pattern**: Diferentes estrategias de persistencia
- **🛡️ Security Pattern**: Autenticación y autorización role-based

---

## 💾 **Modelo de Datos**

### 📊 **MySQL - Empleados (Datos Estructurados)**
```sql
┌─────────────────────────────────────────┐
│                EMPLEADO                 │
├─────────────────────────────────────────┤
│ 🔑 id: BIGINT AUTO_INCREMENT PK        │
│    nombre: VARCHAR(255) NOT NULL       │
│    cargo: VARCHAR(255)                 │
│    salario: DECIMAL(10,2)              │
│    email: VARCHAR(255) UNIQUE          │
└─────────────────────────────────────────┘
```

### 🍃 **MongoDB Atlas - Proyectos (Documentos Flexibles)**
```javascript
{
  "_id": ObjectId("..."),
  "nombre": "API REST Desarrollo",
  "descripcion": "Sistema de APIs para servicios empresariales",
  "empleadoId": 1,              // 🔗 Referencia a MySQL
  "fechaInicio": "2025-01-15",
  "completado": false,
  "tareas": [                   // 📝 Documentos embebidos
    {
      "titulo": "Implementar endpoints",
      "estado": "pendiente"     // pendiente|en-progreso|completo
    },
    {
      "titulo": "Documentar API",
      "estado": "completo"
    }
  ]
}
```

### 🔗 **Relación Cross-Database**
```
MySQL Empleados ←→ MongoDB Proyectos
     id: 1      ←→   empleadoId: 1
```

---

## 🚀 **Instalación y Configuración**

### 📋 **Prerrequisitos**
- ☕ **Java 17+** ([OpenJDK](https://openjdk.org/))
- 📦 **Maven 3.6+** ([Apache Maven](https://maven.apache.org/))
- 🗄️ **MySQL 8.0+** ([MySQL Community](https://dev.mysql.com/downloads/))
- 🍃 **MongoDB Atlas Account** ([MongoDB Atlas](https://www.mongodb.com/atlas))

### 🔧 **1. Clonar el Repositorio**
```bash
git clone https://github.com/frantastico-rgb/crud-mixto.git
cd demomixto
```

### 🗄️ **2. Configurar MySQL**
```sql
-- Crear la base de datos
CREATE DATABASE empresa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional)
CREATE USER 'demomixto'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON empresa.* TO 'demomixto'@'localhost';
FLUSH PRIVILEGES;
```

### 🍃 **3. Configurar MongoDB Atlas**
1. Crear cuenta en [MongoDB Atlas](https://www.mongodb.com/atlas)
2. Crear cluster gratuito
3. Configurar acceso de red (IP Whitelist)
4. Obtener string de conexión
5. Crear base de datos `empresa`

### ⚙️ **4. Configurar application.properties**
```properties
# Configuración MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/empresa
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_MYSQL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Configuración MongoDB Atlas
spring.data.mongodb.uri=mongodb+srv://usuario:password@cluster.mongodb.net/empresa?retryWrites=true&w=majority
spring.data.mongodb.database=empresa
```

### 📦 **5. Instalar Dependencias**
```bash
mvn clean install
```

---

## ▶️ **Ejecución**

### 🚀 **Modo Desarrollo**
```bash
mvn spring-boot:run
```

### 📦 **Modo Producción**
```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 🌐 **Acceso a la Aplicación**
- **Web Interface**: [http://localhost:8080](http://localhost:8080)
- **Empleados (Auth Required)**: [http://localhost:8080/empleados](http://localhost:8080/empleados)
- **Proyectos (Public)**: [http://localhost:8080/proyectos](http://localhost:8080/proyectos)

---

## 🌐 **Endpoints y API**

### 👥 **Empleados API** (🔐 Auth Required)
```http
# Listar empleados
GET /api/empleados
Authorization: Basic admin:admin

# Crear empleado
POST /api/empleados
Content-Type: application/json
Authorization: Basic admin:admin
{
  "nombre": "Juan Pérez",
  "cargo": "Desarrollador",
  "salario": 50000,
  "email": "juan@empresa.com"
}

# Obtener empleado por ID
GET /api/empleados/{id}
Authorization: Basic admin:admin

# Actualizar empleado
PUT /api/empleados/{id}
Authorization: Basic admin:admin

# Eliminar empleado
DELETE /api/empleados/{id}
Authorization: Basic admin:admin

# Buscar empleados
GET /api/empleados/buscar?termino=Juan
Authorization: Basic admin:admin

# Reportes en Excel
GET /api/empleados/reporte/excel
Authorization: Basic admin:admin
```

### 📋 **Proyectos API** (🌐 Public Access)
```http
# Listar proyectos
GET /api/proyectos

# Crear proyecto
POST /api/proyectos
Content-Type: application/json
{
  "nombre": "Nuevo Proyecto",
  "descripcion": "Descripción del proyecto",
  "empleadoId": 1,
  "fechaInicio": "2025-01-15",
  "completado": false
}

# Obtener proyecto por ID
GET /api/proyectos/{id}

# Actualizar proyecto
PUT /api/proyectos/{id}

# Eliminar proyecto
DELETE /api/proyectos/{id}

# Gestión de tareas
POST /api/proyectos/{id}/tareas        # Agregar tarea
PUT /api/proyectos/{id}/tareas/{index} # Actualizar tarea
DELETE /api/proyectos/{id}/tareas/{index} # Eliminar tarea

# Estadísticas
GET /api/proyectos/estadisticas
```

### 📊 **Formato de Respuesta JSON**
```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": {
    // Datos del objeto
  }
}
```

---

## 🔐 **Seguridad**

### 👥 **Usuarios Configurados**
```yaml
Usuarios de prueba:
  - Usuario: admin
    Password: admin
    Roles: [ADMIN]
    Acceso: Empleados + Proyectos
    
  - Usuario: user  
    Password: password
    Roles: [USER]
    Acceso: Solo proyectos
```

### 🔒 **Política de Acceso**
- **📊 Empleados**: Requiere autenticación `ADMIN` 
- **📋 Proyectos**: Acceso público (sin autenticación)
- **🌐 APIs**: Mismas reglas que interfaces web
- **🔧 Actuator**: Deshabilitado en producción

### 🛡️ **Configuración de Seguridad**
```java
// Rutas protegidas
.requestMatchers("/empleados/**", "/api/empleados/**")
    .hasRole("ADMIN")
    
// Rutas públicas  
.requestMatchers("/proyectos/**", "/api/proyectos/**", "/")
    .permitAll()
```

---

## 📱 **Interfaces Web**

### 👥 **Gestión de Empleados** (`/empleados`)
- ✅ **Lista de empleados** con búsqueda y filtros
- ➕ **Crear empleado** con validación de formulario
- ✏️ **Editar empleado** con datos pre-cargados
- 🗑️ **Eliminar empleado** con confirmación
- 📊 **Reportes** exportables a Excel
- 🔍 **Búsqueda** por nombre, cargo y rango salarial

### 📋 **Gestión de Proyectos** (`/proyectos`)
- 📋 **Lista de proyectos** con filtros por empleado
- ➕ **Crear proyecto** con selector de empleado
- ✏️ **Editar proyecto** con gestión de tareas inline
- 🗑️ **Eliminar proyecto** con confirmación
- 📝 **Gestión de tareas** (agregar, editar, eliminar)
- 📊 **Estadísticas** de proyectos y tareas

### 🎨 **Características UI/UX**
- 📱 **Responsive Design** (Bootstrap integrado)
- 🎯 **Navegación intuitiva** entre módulos
- ✅ **Mensajes de confirmación** para todas las operaciones
- ⚠️ **Manejo de errores** con mensajes informativos
- 🔄 **Actualización en tiempo real** de estadísticas

---

## 🧪 **Testing con Thunder Client**

### 📋 **Collection de Thunder Client**

#### **🔌 Tests de Conectividad**
```json
{
  "name": "DemoMixto - Conectividad",
  "tests": [
    {
      "name": "Verificar servidor activo",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/proyectos"
      },
      "expected": "200 OK"
    }
  ]
}
```

#### **👥 Tests de Empleados**
```json
{
  "name": "DemoMixto - Empleados",
  "auth": {
    "type": "basic",
    "username": "admin",
    "password": "admin"
  },
  "tests": [
    {
      "name": "Listar empleados",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/empleados"
      }
    },
    {
      "name": "Crear empleado",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/empleados",
        "body": {
          "nombre": "Test Thunder",
          "cargo": "QA Tester",
          "salario": 45000,
          "email": "test@thunder.com"
        }
      }
    }
  ]
}
```

#### **📋 Tests de Proyectos**
```json
{
  "name": "DemoMixto - Proyectos",
  "tests": [
    {
      "name": "Listar proyectos",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/proyectos"
      }
    },
    {
      "name": "Crear proyecto",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/proyectos",
        "body": {
          "nombre": "Proyecto Thunder Test",
          "descripcion": "Testing con Thunder Client",
          "empleadoId": 1,
          "fechaInicio": "2025-01-15",
          "completado": false
        }
      }
    }
  ]
}
```

---

## � **Containerización con Docker**

DemoMixto incluye una **configuración Docker completa** que permite ejecutar toda la aplicación y sus dependencias con un solo comando. La solución incluye stack completo con monitoreo y herramientas de administración.

### 🎯 **IMPORTANTE: Dos Opciones de Ejecución**

```
🔥 OPCIÓN 1: DESARROLLO DIRECTO (ACTUAL - FUNCIONANDO)
├── 📍 Ejecutar: DemoApplication.java desde VS Code/Maven
├── 📍 Configuración: application.properties
├── 📍 Bases de datos: MySQL local + MongoDB Atlas
├── 📍 Puerto: 8080
├── 📍 Instalación: ✅ YA INSTALADO Y FUNCIONANDO
└── 📍 Estado: ✅ OPERATIVO

🐳 OPCIÓN 2: CONTAINERIZADO (REQUIERE INSTALACIÓN EN PC)
├── 📍 Ejecutar: docker-compose up -d
├── 📍 Configuración: application-docker.properties
├── 📍 Bases de datos: MySQL + MongoDB en contenedores
├── 📍 Puertos: 8080, 8081, 8082, 9090, 3000
├── 📍 Instalación: ❌ REQUIERE DOCKER DESKTOP EN EL PC
└── 📍 Estado: ⏳ PENDIENTE INSTALACIÓN
```

### 🚀 **Para usar la Opción Docker:**

**1️⃣ Instalar Docker Desktop en tu PC** (NO en el proyecto):
```bash
# Seguir guía completa en:
# INSTALACION-DOCKER-WINDOWS.md

# Pasos principales:
# 1. Habilitar WSL 2 en Windows
# 2. Descargar Docker Desktop
# 3. Instalar en el PC
# 4. Configurar y verificar
```

**2️⃣ Ejecutar stack completo** (una vez instalado Docker):
```bash
# Detener DemoApplication.java si está ejecutándose
# Después ejecutar:
docker-compose up -d
```

**🎯 URLs disponibles:**
- **📱 Aplicación**: [http://localhost:8080](http://localhost:8080)
- **🗄️ Adminer (MySQL)**: [http://localhost:8081](http://localhost:8081)
- **🍃 Mongo Express**: [http://localhost:8082](http://localhost:8082)
- **📊 Prometheus**: [http://localhost:9090](http://localhost:9090) *(opcional)*
- **📈 Grafana**: [http://localhost:3000](http://localhost:3000) *(opcional)*

### 🏗️ **Arquitectura Docker**

```
┌─────────────────────────────────────────────────────────────────┐
│                    DEMOMIXTO DOCKER STACK                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  🚀 demomixto-app (Spring Boot)                                │
│  ├── Puerto: 8080                                              │
│  ├── Profile: docker                                           │
│  ├── Health Check: /actuator/health                            │
│  └── Logs: /app/logs                                           │
│                                                                 │
│  🗄️ mysql-db (MySQL 8.0)                                      │
│  ├── Puerto: 3306                                              │
│  ├── Database: empresa                                         │
│  ├── Usuario: demomixto                                        │
│  └── Datos: Scripts de inicialización                          │
│                                                                 │
│  🍃 mongo-db (MongoDB 7.0)                                     │
│  ├── Puerto: 27017                                             │
│  ├── Database: empresa                                         │
│  ├── Usuario: demomixto                                        │
│  └── Datos: Proyectos de ejemplo + índices                     │
│                                                                 │
│  🔧 adminer (MySQL GUI)                                        │
│  ├── Puerto: 8081                                              │
│  └── Acceso directo a MySQL                                    │
│                                                                 │
│  🌿 mongo-express (MongoDB GUI)                                │
│  ├── Puerto: 8082                                              │
│  └── Acceso directo a MongoDB                                  │
│                                                                 │
│  📊 prometheus + grafana (Opcional)                            │
│  ├── Prometheus: 9090                                          │
│  ├── Grafana: 3000                                             │
│  └── Métricas de aplicación                                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 📦 **Componentes del Stack Docker**

#### **🐳 Dockerfile Multi-stage**
- **Etapa 1**: Build con Maven + OpenJDK 17
- **Etapa 2**: Runtime optimizado con imagen slim  
- **Etapa 3**: Desarrollo con debugging habilitado
- **Características**: Health check, usuario no-root, optimización JVM

#### **🔧 docker-compose.yml**
- **Stack completo** con 7 servicios
- **Redes aisladas** para comunicación segura
- **Volúmenes persistentes** para datos
- **Health checks** para todos los servicios
- **Variables de entorno** configurables

#### **⚙️ Configuración Específica**
- **application-docker.properties** - Profile optimizado para contenedores
- **Scripts SQL** - Inicialización automática de MySQL con datos de ejemplo
- **Scripts MongoDB** - Configuración de colecciones, índices y datos iniciales
- **Configuración MySQL** - Optimizada para contenedores con memoria limitada

### 🧪 **Testing Automatizado Docker**

DemoMixto incluye scripts de testing completos para validar el funcionamiento:

#### **🐧 Linux/macOS**
```bash
# Hacer ejecutable y ejecutar
chmod +x docker/scripts/test-docker.sh
./docker/scripts/test-docker.sh
```

#### **🪟 Windows**
```cmd
# Ejecutar script interactivo
docker\scripts\test-docker.bat
```

#### **✅ Tests Incluidos**
- **🔌 Conectividad**: Health checks de todos los servicios
- **📋 API Proyectos**: CRUD completo (público)
- **👥 API Empleados**: CRUD completo (autenticado)  
- **🌐 Interfaces Web**: Páginas MVC con autenticación
- **🗄️ Bases de Datos**: Integración MySQL + MongoDB
- **📊 Health Checks**: Componentes UP/DOWN

### 🚀 **Comandos Docker Útiles**

#### **Gestión del Stack**
```bash
# Iniciar stack completo
docker-compose up -d

# Ver estado de servicios
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f demomixto-app

# Reiniciar un servicio específico
docker-compose restart demomixto-app

# Parar stack completo
docker-compose down

# Rebuild completo (después de cambios en código)
docker-compose down
docker-compose up --build -d
```

#### **Debugging y Monitoreo**
```bash
# Acceder al contenedor de la aplicación
docker-compose exec demomixto-app bash

# Ver logs de MySQL
docker-compose logs mysql-db

# Ver logs de MongoDB
docker-compose logs mongo-db

# Ejecutar comando en MySQL
docker-compose exec mysql-db mysql -u demomixto -p empresa

# Ejecutar comando en MongoDB
docker-compose exec mongo-db mongosh -u demomixto -p empresa
```

#### **Gestión de Datos**
```bash
# Backup de volúmenes
docker run --rm -v demomixto_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql-backup.tar.gz /data

# Limpiar volúmenes (⚠️ BORRA TODOS LOS DATOS)
docker-compose down -v
docker volume prune -f
```

### 📊 **Monitoreo y Observabilidad**

#### **Health Checks Disponibles**
- **Aplicación**: `http://localhost:8080/actuator/health`
- **MySQL**: Ping interno del contenedor
- **MongoDB**: Comando ping de MongoDB
- **Servicios Web**: Verificación HTTP

#### **Métricas (Opcional)**
```bash
# Habilitar Prometheus + Grafana
docker-compose --profile monitoring up -d

# Acceder a métricas
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000 (admin/admin)
```

### 🔐 **Configuración de Seguridad Docker**

#### **Credenciales por Defecto**
```yaml
# Aplicación DemoMixto
- Admin: admin / admin
- User: user / password

# MySQL
- Root: RootPassword2025!
- App User: demomixto / DemoMixto2025!

# MongoDB  
- Admin: admin / AdminPassword2025!
- App User: demomixto / DemoMixto2025!

# Mongo Express GUI
- Web Auth: admin / admin
```

#### **🛡️ Buenas Prácticas Implementadas**
- **Usuario no-root** en contenedores de aplicación
- **Redes aisladas** entre servicios
- **Variables de entorno** para credenciales
- **Health checks** para monitoreo
- **Volúmenes nombrados** para persistencia

### 🎯 **Beneficios de la Solución Docker**

- ✅ **Setup en 1 comando**: `docker-compose up -d`
- ✅ **Consistencia total**: Mismo entorno en cualquier máquina
- ✅ **Aislamiento**: Sin conflictos con software local
- ✅ **Escalabilidad**: Fácil replicación y distribución
- ✅ **Portabilidad**: Windows, macOS, Linux
- ✅ **Testing automatizado**: Validación completa incluida
- ✅ **Monitoreo integrado**: Health checks y métricas
- ✅ **Desarrollo ágil**: Hot reload y debugging

---

## �📊 **Tecnologías Utilizadas**

### 🏗️ **Backend Framework**
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Persistencia MySQL
- **Spring Data MongoDB** - Persistencia MongoDB
- **Spring Security** - Autenticación y autorización
- **Spring Web** - APIs REST y controladores MVC

### 🗄️ **Bases de Datos**
- **MySQL 8.0** - Base de datos relacional (empleados)
- **MongoDB Atlas** - Base de datos documental (proyectos)

### 🌐 **Frontend**
- **Thymeleaf** - Motor de plantillas server-side
- **Bootstrap 5** - Framework CSS (integrado en templates)
- **HTML5/CSS3** - Interfaces web responsive

### 🔧 **Herramientas de Desarrollo**
- **Lombok** - Reducción de boilerplate code
- **Maven** - Gestión de dependencias y build
- **Spring DevTools** - Hot reload en desarrollo
- **Apache POI** - Generación de reportes Excel

### ☁️ **Infraestructura**
- **Java 17** - Plataforma de ejecución
- **Embedded Tomcat** - Servidor web
- **HikariCP** - Pool de conexiones MySQL

### 🐳 **Containerización y DevOps**
- **Docker** - Containerización de aplicaciones
- **Docker Compose** - Orquestación de servicios múltiples
- **Multi-stage Dockerfile** - Builds optimizados
- **Health Checks** - Monitoreo de servicios
- **Prometheus + Grafana** - Métricas y dashboards (opcional)

---

## 📂 **Estructura del Proyecto**

```
demomixto/
├── 📋 pom.xml                          # Configuración Maven
├── 📝 README.md                        # Documentación principal
├── � Dockerfile                       # Configuración Docker multi-stage
├── 🐳 docker-compose.yml               # Stack completo Docker
├── 📄 .dockerignore                    # Optimización contexto Docker
├── �📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/miAplicacion/demo/
│   │   │   ├── 🚀 DemoApplication.java         # Clase principal
│   │   │   ├── 📁 Config/
│   │   │   │   └── 🔐 SecurityConfig.java      # Configuración seguridad
│   │   │   ├── 📁 Controller/
│   │   │   │   ├── 🏠 HomeController.java      # Controlador principal
│   │   │   │   ├── 👥 EmpleadoController.java  # MVC Empleados
│   │   │   │   └── 📋 ProyectoController.java  # MVC Proyectos
│   │   │   ├── 📁 Entity/
│   │   │   │   ├── 👤 Empleado.java            # Entidad MySQL
│   │   │   │   ├── 📄 Proyecto.java            # Documento MongoDB
│   │   │   │   └── 📝 Tarea.java               # Subdocumento
│   │   │   ├── 📁 Repository/
│   │   │   │   ├── 👥 EmpleadoRepository.java  # Repo MySQL
│   │   │   │   └── 📋 ProyectoRepository.java  # Repo MongoDB
│   │   │   └── 📁 Service/
│   │   │       ├── 👤 EmpleadoService.java     # Lógica empleados
│   │   │       └── 📊 ProyectoService.java     # Lógica proyectos
│   │   ├── 📁 resources/
│   │   │   ├── ⚙️ application.properties       # Configuración app
│   │   │   ├── ⚙️ application-docker.properties # Configuración Docker
│   │   │   └── 📁 templates/
│   │   │       ├── 🏠 home.html                # Página principal
│   │   │       ├── 👥 empleados-lista.html     # Lista empleados
│   │   │       ├── ➕ empleado-crear.html       # Crear empleado
│   │   │       ├── ✏️ empleado-editar.html      # Editar empleado
│   │   │       ├── 📊 empleado-reportes.html   # Reportes empleados
│   │   │       ├── 📋 proyectos.html           # Lista proyectos
│   │   │       ├── ➕ proyecto-crear.html       # Crear proyecto
│   │   │       ├── ✏️ proyecto-editar.html      # Editar proyecto
│   │   │       └── 📊 proyecto-reportes.html   # Reportes proyectos
│   │   └── 📁 static/
│   │       └── 🎨 [Recursos estáticos CSS/JS]
│   └── 📁 test/
│       └── 📁 java/
│           └── 🧪 DemoApplicationTests.java
├── 📁 target/                          # Compilados Maven
├── 📁 docker/                          # Configuración Docker
│   ├── 📁 mysql/
│   │   ├── 📁 init/                    # Scripts inicialización MySQL
│   │   └── 📁 conf.d/                  # Configuración MySQL
│   ├── 📁 mongo/
│   │   └── 📁 init/                    # Scripts inicialización MongoDB
│   └── 📁 scripts/
│       ├── 🧪 test-docker.sh           # Testing Linux/macOS
│       ├── 🧪 test-docker.bat          # Testing Windows
│       └── 📋 README.md                # Documentación scripts
└── 📁 docs/                            # Documentación técnica
    └── 📋 01-ANALISIS-Y-DISEÑO.md      # Análisis arquitectónico
```

---

## 🤝 **Contribuciones**

### 🔧 **Cómo Contribuir**
1. 🍴 **Fork** el repositorio
2. 🌟 **Crear branch** para tu feature (`git checkout -b feature/AmazingFeature`)
3. 💾 **Commit** tus cambios (`git commit -m 'Add: AmazingFeature'`)
4. 📤 **Push** al branch (`git push origin feature/AmazingFeature`)
5. 🔄 **Abrir Pull Request**

### 📋 **Roadmap de Mejoras**
- [x] 🐳 **Containerización con Docker** ✅
- [x] 🧪 **Scripts de testing automatizado** ✅
- [ ] 🔍 Implementar búsqueda full-text en MongoDB
- [ ] 📊 Dashboard con métricas en tiempo real
- [ ] 🔔 Sistema de notificaciones
- [ ] 📱 API GraphQL como alternativa a REST
- [ ] ☁️ Deploy en Azure/AWS
- [ ] 🧪 Tests unitarios e integración
- [ ] 📚 Documentación API con Swagger

### 🐛 **Reportar Issues**
- Usa el template de issues del repositorio
- Incluye logs completos y pasos para reproducir
- Especifica versiones de Java, Maven y bases de datos

---

## 📜 **Licencia**

Este proyecto está bajo la **Licencia MIT** - ver el archivo [LICENSE](LICENSE) para detalles.

---

## 📞 **Contacto y Soporte**

- 👨‍💻 **Desarrollador**: frantastico-rgb
- 📧 **Email**: [Configurar email de contacto]
- 🐙 **GitHub**: [frantastico-rgb](https://github.com/frantastico-rgb)
- 📋 **Issues**: [Crear Issue](https://github.com/frantastico-rgb/crud-mixto/issues)

---

## 🙏 **Agradecimientos**

- **Spring Team** por el excelente framework
- **MongoDB** por la plataforma Atlas gratuita
- **MySQL** por la base de datos confiable
- **Lombok** por reducir el boilerplate code
- **Bootstrap** por los componentes UI

---

<div align="center">

**⭐ Si este proyecto te resulta útil, considera darle una estrella ⭐**

---

**Desarrollado con ❤️ usando Spring Boot + MySQL + MongoDB**

</div>