# 📋 API Documentation - CRUD Mixto Spring Boot

## 🏗️ Arquitectura del Sistema

Este proyecto implementa una **arquitectura dual de persistencia**:

- **MySQL (JPA)**: Empleados con IDs auto-generados (`Long`)
- **MongoDB Atlas**: Proyectos con IDs de documento (`String`) 
- **Spring Security**: Autenticación HTTP Basic
- **Thymeleaf**: Renderizado server-side para vistas web

## 🚀 Configuración Inicial

### 1. Importar en Postman

1. **Colección Principal:**
   - Importa: `postman-collection.json`
   - Contiene todos los endpoints organizados por módulos

2. **Variables de Entorno:**
   - Importa: `postman-environment.json`
   - Configura URLs y credenciales

3. **Activar Entorno:**
   - Selecciona "CRUD Mixto - Environment" en Postman

### 2. Verificar Conexión

```bash
GET {{baseUrl}}/
# Debe redirigir a /empleados
```

## 🔐 Autenticación

### Credenciales por Defecto
- **Admin**: `admin:admin` (CRUD completo empleados)
- **User**: `user:password` (Solo lectura empleados)

### Módulos por Seguridad
- **🔒 Empleados**: Requiere autenticación básica
- **🔓 Proyectos**: Acceso público
- **🔓 Home**: Acceso público

## 📊 Endpoints Principales

### 👥 Empleados (MySQL/JPA)

#### CRUD Básico
```http
GET    /empleados              # Lista todos (vista web)
GET    /empleados/{id}         # Obtener por ID (JSON)
POST   /empleados              # Crear empleado
POST   /empleados/{id}         # Actualizar empleado
DELETE /empleados/{id}         # Eliminar empleado
```

#### Búsquedas Avanzadas
```http
GET /empleados/buscar?termino=juan           # Buscar en nombre y cargo
GET /empleados/buscar-nombre?nombre=carlos   # Buscar por nombre específico
GET /empleados/buscar-cargo?cargo=developer  # Buscar por cargo específico
GET /empleados/buscar-salario?min=50000&max=100000  # Rango salarial
```

#### Reportes
```http
GET /empleados/reportes                      # Vista de reportes
GET /empleados/exportar/csv                  # Exportar CSV
GET /empleados/exportar/excel               # Exportar Excel
GET /empleados/{id}/proyectos-json          # Proyectos del empleado
```

### 📋 Proyectos (MongoDB)

#### CRUD Básico
```http
GET    /proyectos              # Lista todos (vista web)
GET    /proyectos/{id}         # Obtener por ID (JSON)
POST   /proyectos              # Crear proyecto
POST   /proyectos/{id}         # Actualizar proyecto
DELETE /proyectos/{id}         # Eliminar proyecto
```

#### Filtros y Búsquedas
```http
GET /proyectos/buscar?nombre=sistema                    # Buscar por nombre
GET /proyectos/estado?completado=true                   # Filtrar por estado
GET /proyectos/empleado/{empleadoId}                    # Por empleado
GET /proyectos?empleadoId=1&completado=false&busqueda=x # Filtros múltiples
```

#### Gestión de Tareas
```http
POST   /proyectos/{id}/tareas                # Agregar tarea
POST   /proyectos/{id}/tareas/{indice}       # Actualizar tarea
DELETE /proyectos/{id}/tareas/{indice}       # Eliminar tarea
```

#### Estadísticas y Reportes
```http
GET /proyectos/reportes                      # Vista de reportes
GET /proyectos/estadisticas                  # Estadísticas JSON
GET /proyectos/exportar/csv                  # Exportar CSV
GET /proyectos/exportar/excel               # Exportar Excel
GET /proyectos/exportar/detallado           # Reporte completo JSON
```

## 📋 Ejemplos de Payloads

### Crear Empleado
```json
Content-Type: application/x-www-form-urlencoded

nombre=Juan Carlos Estrada
cargo=Desarrollador Senior
salario=85000.00
email=juan.estrada@empresa.com
```

### Crear Proyecto
```json
Content-Type: application/x-www-form-urlencoded

nombre=Sistema de Gestión Empresarial
descripcion=Desarrollo de sistema integral para RRHH
empleadoId=1
fechaInicio=2025-01-15
completado=false
```

### Agregar Tarea
```json
Content-Type: application/json

{
    "nombre": "Implementar autenticación",
    "descripcion": "Desarrollar sistema de login con JWT",
    "estado": "pendiente",
    "prioridad": "alta"
}
```

## 🧪 Escenarios de Prueba

### 1. Flujo Básico Empleados
```bash
# 1. Listar empleados existentes
GET /empleados

# 2. Crear nuevo empleado
POST /empleados (con datos)

# 3. Buscar empleado creado
GET /empleados/buscar?termino=NombreEmpleado

# 4. Actualizar empleado
POST /empleados/{id} (con datos modificados)

# 5. Obtener proyectos del empleado
GET /empleados/{id}/proyectos-json
```

### 2. Flujo Completo Proyectos
```bash
# 1. Crear proyecto
POST /proyectos (asignado a empleado)

# 2. Agregar tareas
POST /proyectos/{id}/tareas (tarea 1)
POST /proyectos/{id}/tareas (tarea 2)

# 3. Actualizar tarea
POST /proyectos/{id}/tareas/0 (marcar completa)

# 4. Verificar estadísticas
GET /proyectos/estadisticas

# 5. Exportar reporte
GET /proyectos/exportar/detallado
```

### 3. Pruebas de Filtros
```bash
# Empleados por rango salarial
GET /empleados/buscar-salario?min=50000&max=100000

# Proyectos activos de empleado específico
GET /proyectos?empleadoId=1&completado=false

# Buscar proyectos por nombre
GET /proyectos/buscar?nombre=sistema
```

## 📋 Estados y Códigos de Respuesta

### Códigos Esperados
- **200 OK**: Operación exitosa (GET, actualizaciones)
- **201 Created**: Recurso creado exitosamente  
- **204 No Content**: Eliminación exitosa
- **302 Found**: Redirección (formularios web)
- **400 Bad Request**: Datos inválidos
- **401 Unauthorized**: Credenciales inválidas
- **404 Not Found**: Recurso no existe
- **500 Internal Error**: Error del servidor

### Estados de Tareas
- `pendiente`: Tarea sin iniciar
- `en_progreso`: Tarea en desarrollo
- `completo`: Tarea finalizada
- `bloqueado`: Tarea con impedimentos

### Prioridades de Tareas
- `baja`: No urgente
- `media`: Prioridad normal
- `alta`: Requiere atención inmediata
- `critica`: Bloquea otros desarrollos

## 🛠️ Troubleshooting

### Problemas Comunes

#### 1. Error 401 en Empleados
```bash
# Verificar credenciales
Authorization: Basic admin:admin (en Base64)
```

#### 2. Error de Conexión MySQL
```bash
# Verificar que MySQL esté corriendo
docker ps | grep mysql
# O verificar logs de la aplicación
```

#### 3. Error de Conexión MongoDB
```bash
# Verificar URI de MongoDB Atlas en application.properties
spring.data.mongodb.uri=mongodb+srv://...
```

#### 4. IDs no válidos
```bash
# MySQL empleados: usar Long (1, 2, 3...)
# MongoDB proyectos: usar ObjectId string (24 caracteres hex)
```

### Variables de Debug
```bash
# Habilitar logs SQL
logging.level.org.hibernate.SQL=DEBUG

# Logs de MongoDB
logging.level.org.springframework.data.mongodb=DEBUG
```

## 📈 Métricas y Monitoreo

### Endpoints de Estadísticas
```http
GET /empleados/reportes        # Estadísticas empleados (HTML)
GET /proyectos/estadisticas    # Estadísticas proyectos (JSON)
```

### Campos de Estadísticas Empleados
- Total empleados registrados
- Salario promedio/mínimo/máximo
- Distribución por cargo
- Proyectos asignados por empleado

### Campos de Estadísticas Proyectos  
- Total proyectos activos/completados
- Total tareas por estado
- Porcentaje de completado
- Distribución por empleado

## 🔄 Integración y Workflows

### Workflow Típico de Desarrollo
1. **Setup**: Crear empleados desarrolladores
2. **Planning**: Crear proyectos y asignar
3. **Execution**: Agregar tareas detalladas  
4. **Tracking**: Actualizar estados de tareas
5. **Reporting**: Generar reportes de progreso

### APIs para Integración Externa
- **CSV Export**: Para hojas de cálculo
- **Excel Export**: Para reportes ejecutivos
- **JSON APIs**: Para dashboards externos
- **Búsquedas**: Para sistemas de terceros

---

## 📝 Notas Técnicas

- **IDs Auto-increment**: MySQL genera automáticamente IDs Long para empleados
- **IDs Documento**: MongoDB usa ObjectId strings para proyectos
- **Validación Email**: Los emails deben ser únicos en empleados
- **Relación Empleado-Proyecto**: Referencia por empleadoId (Long como String en MongoDB)
- **Persistencia Dual**: Datos críticos en MySQL, datos flexibles en MongoDB
- **Cache**: Spring Boot cachea automáticamente consultas frecuentes
- **Transacciones**: JPA maneja transacciones automáticamente para empleados

**Versión API**: 1.0  
**Última Actualización**: Octubre 2025