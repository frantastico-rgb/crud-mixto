# 🚀 Scripts de administración Docker para DemoMixto

## 📋 Scripts disponibles

### 🐧 Linux/macOS
- `test-docker.sh` - Script completo de testing automatizado

### 🪟 Windows  
- `test-docker.bat` - Script de testing para Windows con menú interactivo

## 🎯 Uso rápido

### Para Linux/macOS:
```bash
# Hacer ejecutable
chmod +x docker/scripts/test-docker.sh

# Ejecutar tests
./docker/scripts/test-docker.sh
```

### Para Windows:
```cmd
# Ejecutar desde la raíz del proyecto
docker\scripts\test-docker.bat
```

## 🧪 Qué testea

### ✅ Tests de conectividad
- Aplicación principal (puerto 8080)
- Adminer - MySQL GUI (puerto 8081)  
- Mongo Express - MongoDB GUI (puerto 8082)
- Health check endpoints

### 📋 Tests de API Proyectos (Público)
- GET /api/proyectos - Listar proyectos
- GET /api/proyectos/estadisticas - Estadísticas
- POST /api/proyectos - Crear proyecto
- GET /api/proyectos/{id} - Obtener proyecto
- DELETE /api/proyectos/{id} - Eliminar proyecto

### 👥 Tests de API Empleados (Autenticado)
- GET /api/empleados - Listar empleados (con auth)
- GET /api/empleados/buscar - Buscar empleados
- POST /api/empleados - Crear empleado
- GET /api/empleados/{id} - Obtener empleado
- DELETE /api/empleados/{id} - Eliminar empleado
- Verificación de autenticación requerida

### 🌐 Tests de Interfaces Web
- GET / - Página principal
- GET /proyectos - Lista de proyectos (público)
- GET /empleados - Lista de empleados (con auth)
- Verificación de redirección de autenticación

### 🗄️ Tests de Integración de Bases de Datos
- Conectividad MySQL através de la aplicación
- Conectividad MongoDB através de la aplicación
- Health checks específicos de BD
- Verificación de componentes UP/DOWN

## 📊 Resultados

Los scripts muestran un resumen completo con:
- ✅ Tests exitosos
- ❌ Tests fallidos  
- ⚠️ Advertencias
- 📊 Estadísticas finales

## 🔧 Troubleshooting

Si algún test falla:

1. **Verificar que Docker esté corriendo**
   ```bash
   docker-compose ps
   ```

2. **Ver logs de contenedores**
   ```bash
   docker-compose logs demomixto-app
   docker-compose logs mysql-db
   docker-compose logs mongo-db
   ```

3. **Reiniciar servicios**
   ```bash
   docker-compose restart
   ```

4. **Rebuild completo**
   ```bash
   docker-compose down
   docker-compose up --build -d
   ```

## 🎯 URLs de acceso después del testing

- **📱 Aplicación**: http://localhost:8080
- **🗄️ Adminer (MySQL)**: http://localhost:8081
- **🍃 Mongo Express**: http://localhost:8082
- **📊 Prometheus**: http://localhost:9090 (opcional)
- **📈 Grafana**: http://localhost:3000 (opcional)

## 🔐 Credenciales de prueba

- **Admin**: admin / admin
- **User**: user / password