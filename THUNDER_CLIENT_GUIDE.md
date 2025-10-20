# 🌩️ Guía de Pruebas con Thunder Client - DemoMixto

## 📋 Configuración Inicial

### 1. Instalar Thunder Client
- Abre VS Code
- Ve a Extensions (Ctrl+Shift+X)
- Busca "Thunder Client" 
- Instala la extensión de Ranga Vadhineni

### 2. Importar la Colección
1. Abre Thunder Client desde la sidebar (ícono de rayo ⚡)
2. Click en "Collections" 
3. Click en "Import" → "From File"
4. Selecciona `thunder-tests/thunderclient.json`

### 3. Importar Variables de Entorno
1. En Thunder Client, ve a "Env" (Environments)
2. Click "Import" → "From File" 
3. Selecciona `thunder-tests/thunderEnvironment.json`
4. Activa el environment "DemoMixto Local"

## 🚀 Iniciar la Aplicación

Antes de hacer pruebas, asegúrate de que la app esté corriendo:

```bash
cd c:\java2931811F\demomixto
.\mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 🧪 Ejecutar Pruebas

### Pruebas Individuales

#### 👨‍💼 **EMPLEADOS** (Requiere autenticación: admin/admin)

1. **📋 Listar empleados**
   - Request: `GET /api/empleados`
   - Debería retornar lista con estructura: `{success, message, data}`

2. **➕ Crear empleado**
   - Request: `POST /api/empleados`
   - Body incluido en la colección
   - ✅ **Auto-captura ID** → guardado en variable `nuevoEmpleadoId`

3. **👤 Obtener por ID**
   - Request: `GET /api/empleados/{{nuevoEmpleadoId}}`
   - Usa el ID del empleado recién creado

4. **✏️ Actualizar empleado**
   - Request: `PUT /api/empleados/{{nuevoEmpleadoId}}`
   - Modifica nombre, cargo y salario

5. **🗑️ Eliminar empleado**
   - Request: `DELETE /api/empleados/{{nuevoEmpleadoId}}`

#### 🚀 **PROYECTOS** (Sin autenticación requerida)

1. **📋 Listar proyectos**
   - Request: `GET /api/proyectos`

2. **🔍 Proyectos con filtros**
   - Request: `GET /api/proyectos?empleadoId=1&completado=false`
   - Puedes modificar los parámetros

3. **➕ Crear proyecto**
   - Request: `POST /api/proyectos`
   - ✅ **Auto-captura ID** → guardado en variable `nuevoProyectoId`

4. **✏️ Actualizar proyecto**
   - Request: `PUT /api/proyectos/{{nuevoProyectoId}}`

5. **📊 Estadísticas**
   - Request: `GET /api/proyectos/estadisticas`

#### 📝 **TAREAS** (Sin autenticación)

1. **➕ Agregar tarea**
   - Request: `POST /api/proyectos/{{nuevoProyectoId}}/tareas`
   - Usa el proyecto recién creado

2. **✏️ Actualizar tarea**
   - Request: `PUT /api/proyectos/{{nuevoProyectoId}}/tareas/0`
   - Índice 0 = primera tarea del array

#### 🔍 **BÚSQUEDAS** (Requiere autenticación para empleados)

1. **Buscar empleados**
   - Request: `GET /api/empleados/buscar?termino=developer`

2. **Buscar por salario**
   - Request: `GET /api/empleados/buscar-salario?min=40000&max=60000`

### 🔄 Flujos Automatizados

#### **Flujo Empleado Completo:**
1. Ejecutar "➕ POST - Crear empleado" (captura ID automáticamente)
2. Ejecutar "👤 GET - Empleado por ID" (usa ID capturado)
3. Ejecutar "✏️ PUT - Actualizar empleado" (modifica datos)
4. Ejecutar "🗑️ DELETE - Eliminar empleado" (limpia prueba)

#### **Flujo Proyecto Completo:**
1. Ejecutar "➕ POST - Crear proyecto" (captura ID automáticamente)
2. Ejecutar "➕ POST - Agregar tarea" (usa ID capturado)
3. Ejecutar "✏️ PUT - Actualizar tarea" (modifica primera tarea)
4. Ejecutar "✏️ PUT - Actualizar proyecto" (marca como completado)

## 🧰 Características de Thunder Client

### ✅ **Tests Automáticos Incluidos:**
- Verificación de códigos de respuesta (200, 404, etc.)
- Validación de estructura JSON
- Comprobación de campos específicos
- Captura automática de IDs para usar en otros requests

### 🔄 **Variables Dinámicas:**
- `{{baseUrl}}` - URL base de la aplicación
- `{{empleadoId}}` - ID fijo para pruebas (1)
- `{{nuevoEmpleadoId}}` - Se autocompleta al crear empleado
- `{{nuevoProyectoId}}` - Se autocompleta al crear proyecto

### 🔐 **Autenticación Configurada:**
- **Empleados:** HTTP Basic Auth (`admin:admin`) ya configurado
- **Proyectos:** Sin autenticación requerida

## 🎯 Ejemplos de Datos de Prueba

### Crear Empleado:
```json
{
  "nombre": "Carlos Thunder",
  "cargo": "Backend Developer", 
  "salario": 52000,
  "email": "carlos.thunder@example.com"
}
```

### Crear Proyecto:
```json
{
  "nombre": "Proyecto Thunder Client",
  "descripcion": "Proyecto creado para probar Thunder Client",
  "empleadoId": 1,
  "fechaInicio": "2025-01-20",
  "completado": false
}
```

### Agregar Tarea:
```json
{
  "titulo": "Probar Thunder Client",
  "estado": "pendiente"
}
```

## 🚨 Resolución de Problemas

### **Error 401 (Unauthorized)**
- Verifica que tengas autenticación básica configurada
- Usuario: `admin`, Contraseña: `admin`

### **Error 404 (Not Found)**
- Verifica que la aplicación esté corriendo en puerto 8080
- Confirma que las rutas `/api/*` estén correctas

### **Error de conexión**
- Asegúrate de que la aplicación Spring Boot esté iniciada
- Verifica que `baseUrl` sea `http://localhost:8080`

### **Variables no se actualizan**
- Ejecuta los requests de creación primero
- Las variables `nuevoEmpleadoId` y `nuevoProyectoId` se setean automáticamente

## 🎉 ¡Listo para Probar!

1. ✅ Aplicación corriendo
2. ✅ Thunder Client instalado 
3. ✅ Colección importada
4. ✅ Environment activado
5. 🚀 **¡Empieza a probar los endpoints!**

### Orden Recomendado:
1. **Crear empleado** → captura ID automáticamente
2. **Crear proyecto** → captura ID automáticamente  
3. **Agregar tarea** al proyecto
4. **Probar búsquedas y filtros**
5. **Ver estadísticas**
6. **Limpiar datos** (eliminar registros creados)

¡Thunder Client te mostrará las respuestas JSON formateadas y ejecutará los tests automáticamente! ⚡🎯