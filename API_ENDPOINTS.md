# API REST Documentation - DemoMixto Spring Boot Application

Esta documentación lista todos los endpoints REST disponibles en la aplicación **DemoMixto** con ejemplos de peticiones HTTP usando curl.

## Base URL
```
http://localhost:8080
```

## Autenticación
- **Empleados**: Requiere rol ADMIN (usuario: `admin`, contraseña: `admin`)
- **Proyectos**: Acceso público (sin autenticación requerida)

---

## 📊 EMPLEADOS API (MySQL/JPA)

### CRUD Básico

#### 1. Listar todos los empleados
```bash
GET /empleados
curl -u admin:admin "http://localhost:8080/empleados"
```

#### 2. Obtener empleado por ID (REST API)
```bash
GET /empleados/{id}
curl -u admin:admin "http://localhost:8080/empleados/1"
```

#### 3. Crear nuevo empleado (Formulario)
```bash
POST /empleados
curl -u admin:admin -X POST \
  -d "nombre=Juan Perez&cargo=Developer&salario=50000&email=juan@example.com" \
  "http://localhost:8080/empleados"
```

#### 4. Actualizar empleado (REST API)
```bash
POST /empleados/{id}
curl -u admin:admin -X POST \
  -d "nombre=Juan Carlos&cargo=Senior Developer&salario=60000&email=juan.carlos@example.com" \
  "http://localhost:8080/empleados/1"
```

#### 5. Actualizar empleado desde edición (Formulario)
```bash
POST /empleados/editar/{id}
curl -u admin:admin -X POST \
  -d "nombre=Juan Carlos&cargo=Senior Developer&salario=60000&email=juan.carlos@example.com" \
  "http://localhost:8080/empleados/editar/1"
```

#### 6. Eliminar empleado (REST API)
```bash
DELETE /empleados/{id}
curl -u admin:admin -X DELETE "http://localhost:8080/empleados/1"
```

#### 7. Eliminar empleado (Formulario Web)
```bash
POST /empleados/eliminar/{id}
curl -u admin:admin -X POST "http://localhost:8080/empleados/eliminar/1"
```

### Búsquedas y Filtros

#### 8. Búsqueda general de empleados
```bash
GET /empleados/buscar?termino={termino}
curl -u admin:admin "http://localhost:8080/empleados/buscar?termino=juan"
```

#### 9. Buscar empleados por nombre
```bash
GET /empleados/buscar-nombre?nombre={nombre}
curl -u admin:admin "http://localhost:8080/empleados/buscar-nombre?nombre=Juan"
```

#### 10. Buscar empleados por cargo
```bash
GET /empleados/buscar-cargo?cargo={cargo}
curl -u admin:admin "http://localhost:8080/empleados/buscar-cargo?cargo=Developer"
```

#### 11. Buscar empleados por rango salarial
```bash
GET /empleados/buscar-salario?min={min}&max={max}
curl -u admin:admin "http://localhost:8080/empleados/buscar-salario?min=30000&max=70000"
```

### Relación con Proyectos

#### 12. Obtener proyectos de un empleado (JSON)
```bash
GET /empleados/{id}/proyectos-json
curl -u admin:admin "http://localhost:8080/empleados/1/proyectos-json"
```

### Reportes y Exportación

#### 13. Ver reportes de empleados (HTML)
```bash
GET /empleados/reportes
curl -u admin:admin "http://localhost:8080/empleados/reportes"
```

#### 14. Exportar empleados a CSV
```bash
GET /empleados/exportar/csv
curl -u admin:admin "http://localhost:8080/empleados/exportar/csv" > empleados.csv
```

#### 15. Exportar empleados a Excel
```bash
GET /empleados/exportar/excel
curl -u admin:admin "http://localhost:8080/empleados/exportar/excel" > empleados.xlsx
```

---

## 🚀 PROYECTOS API (MongoDB)

### CRUD Básico

#### 16. Listar todos los proyectos
```bash
GET /proyectos
curl "http://localhost:8080/proyectos"
```

#### 17. Listar proyectos con filtros
```bash
# Por empleado
GET /proyectos?empleadoId={empleadoId}
curl "http://localhost:8080/proyectos?empleadoId=1"

# Por estado
GET /proyectos?completado={true|false}
curl "http://localhost:8080/proyectos?completado=true"

# Por búsqueda
GET /proyectos?busqueda={termino}
curl "http://localhost:8080/proyectos?busqueda=API"

# Combinado
GET /proyectos?empleadoId={id}&completado={boolean}
curl "http://localhost:8080/proyectos?empleadoId=1&completado=false"
```

#### 18. Obtener proyecto por ID (REST API)
```bash
GET /proyectos/{id}
curl "http://localhost:8080/proyectos/68d74c896283a9a744f23415"
```

#### 19. Crear nuevo proyecto (Formulario)
```bash
POST /proyectos
curl -X POST \
  -d "nombre=Nuevo Proyecto&descripcion=Descripcion del proyecto&empleadoId=1&fechaInicio=2025-01-15&completado=false" \
  "http://localhost:8080/proyectos"
```

#### 20. Actualizar proyecto (REST API)
```bash
POST /proyectos/{id}
curl -X POST \
  -d "nombre=Proyecto Actualizado&descripcion=Nueva descripcion&empleadoId=2&fechaInicio=2025-01-20&completado=true" \
  "http://localhost:8080/proyectos/68d74c896283a9a744f23415"
```

#### 21. Actualizar proyecto desde edición (Formulario)
```bash
POST /proyectos/editar/{id}
curl -X POST \
  -d "nombre=Proyecto Editado&descripcion=Descripcion editada&empleadoId=2&fechaInicio=2025-01-25&completado=true" \
  "http://localhost:8080/proyectos/editar/68d74c896283a9a744f23415"
```

#### 22. Eliminar proyecto (REST API)
```bash
DELETE /proyectos/{id}
curl -X DELETE "http://localhost:8080/proyectos/68d74c896283a9a744f23415"
```

#### 23. Eliminar proyecto (Formulario Web)
```bash
POST /proyectos/eliminar/{id}
curl -X POST "http://localhost:8080/proyectos/eliminar/68d74c896283a9a744f23415"
```

### Gestión de Tareas

#### 24. Agregar tarea a proyecto
```bash
POST /proyectos/{id}/tareas
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nueva tarea","descripcion":"Descripcion de la tarea","estado":"pendiente"}' \
  "http://localhost:8080/proyectos/68d74c896283a9a744f23415/tareas"
```

#### 25. Actualizar tarea específica
```bash
POST /proyectos/{id}/tareas/{tareaIndex}
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Tarea actualizada","descripcion":"Nueva descripcion","estado":"completo"}' \
  "http://localhost:8080/proyectos/68d74c896283a9a744f23415/tareas/0"
```

#### 26. Eliminar tarea específica
```bash
DELETE /proyectos/{id}/tareas/{tareaIndex}
curl -X DELETE "http://localhost:8080/proyectos/68d74c896283a9a744f23415/tareas/0"
```

### Búsquedas y Filtros REST

#### 27. Buscar proyectos por nombre
```bash
GET /proyectos/buscar?nombre={nombre}
curl "http://localhost:8080/proyectos/buscar?nombre=API"
```

#### 28. Obtener proyectos por estado
```bash
GET /proyectos/estado?completado={true|false}
curl "http://localhost:8080/proyectos/estado?completado=true"
```

#### 29. Obtener proyectos de un empleado
```bash
GET /proyectos/empleado/{empleadoId}
curl "http://localhost:8080/proyectos/empleado/1"
```

### Reportes y Estadísticas

#### 30. Ver reportes de proyectos (HTML)
```bash
GET /proyectos/reportes
curl "http://localhost:8080/proyectos/reportes"
```

#### 31. Obtener estadísticas generales (JSON)
```bash
GET /proyectos/estadisticas
curl "http://localhost:8080/proyectos/estadisticas"
```

#### 32. Exportar proyectos a CSV
```bash
GET /proyectos/exportar/csv
curl "http://localhost:8080/proyectos/exportar/csv" > proyectos.csv

# Con filtro por empleado
GET /proyectos/exportar/csv?empleadoId={empleadoId}
curl "http://localhost:8080/proyectos/exportar/csv?empleadoId=1" > proyectos_empleado1.csv
```

#### 33. Exportar proyectos a Excel
```bash
GET /proyectos/exportar/excel
curl "http://localhost:8080/proyectos/exportar/excel" > proyectos.xlsx

# Con filtro por empleado
GET /proyectos/exportar/excel?empleadoId={empleadoId}
curl "http://localhost:8080/proyectos/exportar/excel?empleadoId=1" > proyectos_empleado1.xlsx
```

#### 34. Exportar reporte detallado (JSON)
```bash
GET /proyectos/exportar/detallado
curl "http://localhost:8080/proyectos/exportar/detallado"
```

---

## 🔧 Vistas HTML (No REST)

### Empleados
- `GET /empleados/crear` - Formulario crear empleado
- `GET /empleados/editar/{id}` - Formulario editar empleado
- `GET /empleados/{id}/proyectos` - Vista proyectos del empleado

### Proyectos
- `GET /proyectos/crear` - Formulario crear proyecto
- `GET /proyectos/editar/{id}` - Formulario editar proyecto

---

## 📋 Códigos de Respuesta HTTP

### Exitosas
- **200 OK** - GET exitoso con datos
- **201 Created** - Recurso creado exitosamente
- **204 No Content** - DELETE exitoso
- **302 Found** - Redirect después de POST (formularios web)

### Errores del Cliente
- **400 Bad Request** - Datos de entrada inválidos
- **401 Unauthorized** - Falta autenticación
- **403 Forbidden** - Sin permisos (rol ADMIN requerido para empleados)
- **404 Not Found** - Recurso no encontrado
- **405 Method Not Allowed** - Método HTTP no soportado en la ruta

### Errores del Servidor
- **500 Internal Server Error** - Error interno del servidor

---

## 🔐 Ejemplos de Autenticación

### HTTP Basic Auth
```bash
# Admin (acceso completo a empleados)
curl -u admin:admin "http://localhost:8080/empleados"

# Usuario (solo lectura, sin acceso a empleados)
curl -u user:password "http://localhost:8080/proyectos"
```

### Cabeceras de autenticación manual
```bash
# Base64 encode: admin:admin = YWRtaW46YWRtaW4=
curl -H "Authorization: Basic YWRtaW46YWRtaW4=" "http://localhost:8080/empleados"
```

---

## 💡 Notas Importantes

1. **Empleados** requieren autenticación con rol ADMIN (`admin:admin`)
2. **Proyectos** son de acceso público (sin autenticación)
3. Los **formularios web** usan POST y redirigen con 302
4. Los **endpoints REST** devuelven JSON y códigos HTTP estándar
5. Las **exportaciones** retornan archivos (CSV/Excel) como attachments
6. Los **IDs de empleados** son Long (auto-incremento MySQL)
7. Los **IDs de proyectos** son String (ObjectId de MongoDB)
8. **CSRF está deshabilitado** para facilitar pruebas REST

---

## 🧪 Casos de Prueba Rápidos

### Flujo completo de empleado
```bash
# 1. Crear empleado
curl -u admin:admin -X POST -d "nombre=Test User&cargo=Tester&salario=45000&email=test@example.com" "http://localhost:8080/empleados"

# 2. Listar empleados
curl -u admin:admin "http://localhost:8080/empleados"

# 3. Obtener empleado específico (usar ID del paso 2)
curl -u admin:admin "http://localhost:8080/empleados/5"

# 4. Actualizar empleado
curl -u admin:admin -X POST -d "nombre=Test User Updated&cargo=Senior Tester&salario=55000&email=test.updated@example.com" "http://localhost:8080/empleados/5"

# 5. Eliminar empleado
curl -u admin:admin -X DELETE "http://localhost:8080/empleados/5"
```

### Flujo completo de proyecto
```bash
# 1. Crear proyecto
curl -X POST -d "nombre=Test Project&descripcion=Test Description&empleadoId=1&fechaInicio=2025-01-15&completado=false" "http://localhost:8080/proyectos"

# 2. Listar proyectos
curl "http://localhost:8080/proyectos"

# 3. Obtener proyecto específico (usar ID del paso 2)
curl "http://localhost:8080/proyectos/68e3d80b119e058b5a9d507c"

# 4. Agregar tarea
curl -X POST -H "Content-Type: application/json" -d '{"nombre":"Test Task","descripcion":"Test task description","estado":"pendiente"}' "http://localhost:8080/proyectos/68e3d80b119e058b5a9d507c/tareas"

# 5. Actualizar proyecto
curl -X POST -d "nombre=Updated Project&descripcion=Updated Description&empleadoId=1&fechaInicio=2025-01-20&completado=true" "http://localhost:8080/proyectos/68e3d80b119e058b5a9d507c"

# 6. Eliminar proyecto
curl -X DELETE "http://localhost:8080/proyectos/68e3d80b119e058b5a9d507c"
```