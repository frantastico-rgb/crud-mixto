# API REST Endpoints - JSON Response Only

## 🚨 PROBLEMA IDENTIFICADO Y SOLUCIONADO

**Problema Original**: Los controladores mezclaban respuestas HTML (`return "redirect:..."`) con JSON, violando principios REST.

**Solución**: Nuevos controladores REST puros bajo `/api/*` que **SIEMPRE** retornan JSON estructurado.

---

## 📋 Formato de Respuesta Estándar

**TODAS** las respuestas siguen este formato JSON:

```json
{
  "success": true|false,
  "message": "Descripción del resultado",
  "data": {...} | null,
  "total": 0,           // Solo en listas
  "filtros": {...},     // Solo cuando aplican filtros  
  "criterio": {...}     // Solo en búsquedas
}
```

### Códigos HTTP
- **200 OK**: Operación exitosa
- **400 Bad Request**: Datos inválidos (success: false)
- **404 Not Found**: Recurso no encontrado (success: false)
- **500 Internal Server Error**: Error del servidor (success: false)

---

## 👨‍💼 EMPLEADOS API REST PURA

### Base: `/api/empleados`
🔒 **Autenticación**: Requiere `admin:admin` (rol ADMIN)

#### 1. GET /api/empleados - Listar empleados
```bash
curl -u admin:admin "http://localhost:8080/api/empleados"

# Con búsqueda
curl -u admin:admin "http://localhost:8080/api/empleados?busqueda=juan"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Empleados obtenidos exitosamente",
  "data": [
    {
      "id": 1,
      "nombre": "Juan Carlos",
      "cargo": "Developer", 
      "salario": 50000.0,
      "email": "juan@example.com"
    }
  ],
  "total": 1
}
```

#### 2. GET /api/empleados/{id} - Obtener por ID
```bash
curl -u admin:admin "http://localhost:8080/api/empleados/1"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Empleado encontrado",
  "data": {
    "id": 1,
    "nombre": "Juan Carlos",
    "cargo": "Developer",
    "salario": 50000.0,
    "email": "juan@example.com"
  }
}
```

**Respuesta Error (404):**
```json
{
  "success": false,
  "message": "Empleado no encontrado con ID: 999",
  "data": null
}
```

#### 3. POST /api/empleados - Crear empleado
```bash
curl -u admin:admin -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana García",
    "cargo": "Designer", 
    "salario": 45000,
    "email": "ana@example.com"
  }' \
  "http://localhost:8080/api/empleados"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Empleado creado exitosamente",
  "data": {
    "id": 5,
    "nombre": "Ana García",
    "cargo": "Designer",
    "salario": 45000.0,
    "email": "ana@example.com"
  }
}
```

**Respuesta Error (400):**
```json
{
  "success": false,
  "message": "El nombre del empleado es requerido",
  "data": null
}
```

#### 4. PUT /api/empleados/{id} - Actualizar empleado
```bash
curl -u admin:admin -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana García López",
    "cargo": "Senior Designer",
    "salario": 55000,
    "email": "ana.garcia@example.com"
  }' \
  "http://localhost:8080/api/empleados/5"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Empleado actualizado exitosamente",
  "data": {
    "id": 5,
    "nombre": "Ana García López",
    "cargo": "Senior Designer",
    "salario": 55000.0,
    "email": "ana.garcia@example.com"
  }
}
```

#### 5. DELETE /api/empleados/{id} - Eliminar empleado
```bash
curl -u admin:admin -X DELETE "http://localhost:8080/api/empleados/5"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Empleado eliminado exitosamente",
  "data": {
    "id": 5,
    "nombre": "Ana García López"
  }
}
```

#### 6. GET /api/empleados/buscar?termino={termino} - Búsqueda general
```bash
curl -u admin:admin "http://localhost:8080/api/empleados/buscar?termino=developer"
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Búsqueda completada",
  "data": [...],
  "total": 3,
  "termino": "developer"
}
```

#### 7. GET /api/empleados/buscar-nombre?nombre={nombre} - Búsqueda por nombre
```bash
curl -u admin:admin "http://localhost:8080/api/empleados/buscar-nombre?nombre=juan"
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Búsqueda por nombre completada",
  "data": [...],
  "total": 2,
  "criterio": {
    "campo": "nombre",
    "valor": "juan"
  }
}
```

#### 8. GET /api/empleados/buscar-cargo?cargo={cargo} - Búsqueda por cargo
```bash
curl -u admin:admin "http://localhost:8080/api/empleados/buscar-cargo?cargo=developer"
```

#### 9. GET /api/empleados/buscar-salario?min={min}&max={max} - Búsqueda por salario
```bash
curl -u admin:admin "http://localhost:8080/api/empleados/buscar-salario?min=40000&max=60000"
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Búsqueda por rango salarial completada",
  "data": [...],
  "total": 4,
  "criterio": {
    "campo": "salario",
    "min": 40000,
    "max": 60000
  }
}
```

---

## 🚀 PROYECTOS API REST PURA

### Base: `/api/proyectos`
🔓 **Sin autenticación** (acceso público)

#### 1. GET /api/proyectos - Listar proyectos
```bash
curl "http://localhost:8080/api/proyectos"

# Con filtros
curl "http://localhost:8080/api/proyectos?empleadoId=1"
curl "http://localhost:8080/api/proyectos?completado=true"
curl "http://localhost:8080/api/proyectos?busqueda=API"
curl "http://localhost:8080/api/proyectos?empleadoId=1&completado=false"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Proyectos obtenidos exitosamente",
  "data": [
    {
      "id": "68d74c896283a9a744f23415",
      "nombre": "API REST",
      "descripcion": "Desarrollo de API REST...",
      "empleadoId": 1,
      "fechaInicio": "2025-01-15",
      "completado": false,
      "tareas": []
    }
  ],
  "total": 1,
  "filtros": {
    "empleadoId": 1
  }
}
```

#### 2. GET /api/proyectos/{id} - Obtener por ID
```bash
curl "http://localhost:8080/api/proyectos/68d74c896283a9a744f23415"
```

#### 3. POST /api/proyectos - Crear proyecto
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Nuevo Sistema",
    "descripcion": "Sistema de gestión",
    "empleadoId": 2,
    "fechaInicio": "2025-02-01",
    "completado": false
  }' \
  "http://localhost:8080/api/proyectos"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Proyecto creado exitosamente",
  "data": {
    "id": "68e3d80b119e058b5a9d507c",
    "nombre": "Nuevo Sistema",
    "descripcion": "Sistema de gestión",
    "empleadoId": 2,
    "fechaInicio": "2025-02-01",
    "completado": false,
    "tareas": []
  }
}
```

#### 4. PUT /api/proyectos/{id} - Actualizar proyecto
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sistema Actualizado",
    "descripcion": "Descripción actualizada",
    "empleadoId": 2,
    "fechaInicio": "2025-02-05",
    "completado": true
  }' \
  "http://localhost:8080/api/proyectos/68e3d80b119e058b5a9d507c"
```

#### 5. DELETE /api/proyectos/{id} - Eliminar proyecto
```bash
curl -X DELETE "http://localhost:8080/api/proyectos/68e3d80b119e058b5a9d507c"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Proyecto eliminado exitosamente",
  "data": {
    "id": "68e3d80b119e058b5a9d507c",
    "nombre": "Sistema Actualizado"
  }
}
```

#### 6. POST /api/proyectos/{id}/tareas - Agregar tarea
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Implementar API",
    "descripcion": "Crear endpoints REST",
    "estado": "pendiente"
  }' \
  "http://localhost:8080/api/proyectos/68d74c896283a9a744f23415/tareas"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Tarea agregada exitosamente al proyecto",
  "data": {
    "id": "68d74c896283a9a744f23415",
    "nombre": "API REST",
    "tareas": [
      {
        "nombre": "Implementar API",
        "descripcion": "Crear endpoints REST",
        "estado": "pendiente"
      }
    ]
  },
  "tareaAgregada": {
    "nombre": "Implementar API",
    "descripcion": "Crear endpoints REST", 
    "estado": "pendiente"
  }
}
```

#### 7. PUT /api/proyectos/{id}/tareas/{index} - Actualizar tarea
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Implementar API REST",
    "descripcion": "Crear y documentar endpoints",
    "estado": "completo"
  }' \
  "http://localhost:8080/api/proyectos/68d74c896283a9a744f23415/tareas/0"
```

#### 8. DELETE /api/proyectos/{id}/tareas/{index} - Eliminar tarea
```bash
curl -X DELETE "http://localhost:8080/api/proyectos/68d74c896283a9a744f23415/tareas/0"
```

#### 9. GET /api/proyectos/estadisticas - Estadísticas generales
```bash
curl "http://localhost:8080/api/proyectos/estadisticas"
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Estadísticas generadas exitosamente",
  "data": {
    "totalProyectos": 4,
    "proyectosCompletados": 1,
    "proyectosActivos": 3,
    "totalTareas": 8,
    "tareasCompletadas": 3,
    "tareasPendientes": 5
  }
}
```

---

## ✅ CASOS DE PRUEBA COMPLETOS

### Test empleados (con JSON responses)
```bash
# 1. Crear empleado
curl -u admin:admin -X POST \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test User","cargo":"Tester","salario":45000,"email":"test@example.com"}' \
  "http://localhost:8080/api/empleados"

# 2. Listar empleados  
curl -u admin:admin "http://localhost:8080/api/empleados"

# 3. Obtener empleado (usar ID del paso 1)
curl -u admin:admin "http://localhost:8080/api/empleados/6"

# 4. Actualizar empleado
curl -u admin:admin -X PUT \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test User Updated","cargo":"Senior Tester","salario":55000,"email":"test.updated@example.com"}' \
  "http://localhost:8080/api/empleados/6"

# 5. Eliminar empleado
curl -u admin:admin -X DELETE "http://localhost:8080/api/empleados/6"
```

### Test proyectos (con JSON responses)
```bash
# 1. Crear proyecto
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test Project","descripcion":"Test Description","empleadoId":1,"fechaInicio":"2025-01-15","completado":false}' \
  "http://localhost:8080/api/proyectos"

# 2. Listar proyectos
curl "http://localhost:8080/api/proyectos"

# 3. Agregar tarea
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test Task","descripcion":"Test description","estado":"pendiente"}' \
  "http://localhost:8080/api/proyectos/PROYECTO_ID/tareas"

# 4. Actualizar proyecto
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Updated Project","descripcion":"Updated Description","empleadoId":1,"fechaInicio":"2025-01-20","completado":true}' \
  "http://localhost:8080/api/proyectos/PROYECTO_ID"

# 5. Eliminar proyecto
curl -X DELETE "http://localhost:8080/api/proyectos/PROYECTO_ID"
```

---

## 🔄 COMPARACIÓN: Endpoints Antiguos vs Nuevos

### ❌ Endpoints Antiguos (Problemáticos)
- Mezclaban HTML y JSON
- `return "redirect:/empleados"` → No es REST
- Sin formato estándar de respuesta
- Manejo de errores inconsistente

### ✅ Endpoints Nuevos (REST Puro)
- **Solo JSON** en todas las respuestas
- Formato estándar: `{success, message, data}`
- Códigos HTTP apropiados
- Manejo de errores consistente
- Validaciones completas

---

## 📋 RESUMEN

### Endpoints REST PUROS creados:
- **9 endpoints** para empleados (`/api/empleados/*`)
- **9 endpoints** para proyectos (`/api/proyectos/*`)

### Características garantizadas:
✅ **Siempre JSON**: Nunca HTML/redirects  
✅ **Formato estándar**: success, message, data  
✅ **Códigos HTTP**: 200, 400, 404, 500  
✅ **Validaciones**: Datos requeridos  
✅ **Manejo de errores**: Try-catch completo  
✅ **Información clara**: Mensajes descriptivos