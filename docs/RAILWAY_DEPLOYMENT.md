# 🚀 Guía de Despliegue en Railway

## 📋 **Pre-requisitos**
1. Cuenta en [Railway.app](https://railway.app/)
2. GitHub repository público o privado
3. MongoDB Atlas configurado

## 🎯 **Despliegue Paso a Paso**

### **1. Crear Proyecto en Railway**
```bash
# Opción A: Desde Dashboard Web
1. Ir a https://railway.app/dashboard
2. Click "New Project"
3. Seleccionar "Deploy from GitHub repo"
4. Autorizar acceso a frantastico-rgb/crud-mixto
5. Seleccionar rama "main"

# Opción B: Desde CLI (opcional)
npm install -g @railway/cli
railway login
railway init
railway link
```

### **2. Configurar Variables de Entorno**
En el dashboard de Railway, agregar:

```env
# Database Configuration
MYSQL_USERNAME=railway_user
MYSQL_PASSWORD=your_secure_password
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/empresa

# Security
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_secure_admin_password

# Spring Profile
SPRING_PROFILES_ACTIVE=railway
```

### **3. Configurar Base de Datos MySQL**
```bash
# En Railway Dashboard:
1. Click "Add Service" → "Database" → "MySQL"
2. Railway generará automáticamente DATABASE_URL
3. La variable se inyectará automáticamente
```

### **4. Configurar Build**
Railway detectará automáticamente el Dockerfile, pero puedes especificar:

```json
# railway.json (ya creado)
{
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile.railway"
  },
  "deploy": {
    "numReplicas": 1,
    "restartPolicyType": "ON_FAILURE"
  }
}
```

### **5. Deploy Automático**
```bash
# Railway se conectará automáticamente a GitHub
# Cada push a main trigger un nuevo deploy
git add .
git commit -m "feat: Railway deployment configuration"
git push origin main
```

## 🔧 **Configuración Avanzada**

### **Custom Domain (Opcional)**
```bash
# En Railway Dashboard > Settings > Domains
1. Click "Generate Domain" para subdominio gratuito
2. O agregar dominio personalizado
```

### **Monitoring & Logs**
```bash
# Ver logs en tiempo real
railway logs

# Métricas en dashboard
- CPU usage
- Memory usage  
- Network traffic
- Response times
```

### **Escalamiento Automático**
```bash
# Railway escala automáticamente basado en:
- CPU usage
- Memory pressure
- Request volume
```

## 📊 **URLs de Acceso Post-Deploy**

```bash
# URL base generada por Railway
https://your-app-name.up.railway.app

# Endpoints principales:
✅ https://your-app-name.up.railway.app/empleados
✅ https://your-app-name.up.railway.app/proyectos  
✅ https://your-app-name.up.railway.app/api/empleados
✅ https://your-app-name.up.railway.app/api/proyectos
✅ https://your-app-name.up.railway.app/swagger-ui.html
```

## 🔍 **Verificación Post-Deploy**

### **Health Checks**
```bash
# Health endpoint
curl https://your-app-name.up.railway.app/actuator/health

# API test
curl -u admin:admin https://your-app-name.up.railway.app/api/empleados

# MongoDB test  
curl https://your-app-name.up.railway.app/api/proyectos
```

### **Thunder Client Testing**
```bash
# Actualizar base_url en collection:
"base_url": "https://your-app-name.up.railway.app"

# Ejecutar todos los tests contra producción
```

## 💰 **Costos y Límites**

### **Plan Gratuito Railway**
- ✅ $5 USD crédito mensual
- ✅ ~500 horas de uptime
- ✅ 1GB RAM por servicio
- ✅ Base de datos MySQL incluida
- ✅ Custom domains
- ✅ SSL automático

### **Optimización de Costos**
```bash
# Configurar sleep mode para desarrollo
"sleepApplication": true  # En railway.json

# Solo para producción real:
"sleepApplication": false
```

## 🚨 **Troubleshooting Común**

### **Error: OOM (Out of Memory)**
```bash
# Solución: Optimizar heap size en Dockerfile
-XX:MaxRAMPercentage=75.0
```

### **Error: Database Connection**
```bash
# Verificar variables de entorno en Railway dashboard
# Verificar whitelist IP en MongoDB Atlas (0.0.0.0/0 para Railway)
```

### **Error: Build Failed**
```bash
# Verificar logs en Railway dashboard
# Verificar Dockerfile.railway existe
# Verificar dependencias en pom.xml
```

## 🎯 **Ventajas Railway vs Otras Plataformas**

| Feature | Railway | Heroku | Azure |
|---------|---------|---------|--------|
| MySQL incluido | ✅ | ❌ | ✅ |
| Deploy desde GitHub | ✅ | ✅ | ✅ |
| Plan gratuito | ✅ ($5) | ✅ (limitado) | ❌ |
| Escalamiento auto | ✅ | ✅ | ✅ |
| SSL automático | ✅ | ✅ | ✅ |
| Logs en tiempo real | ✅ | ✅ | ✅ |

Railway es **ideal** para DemoMixto por su soporte nativo a múltiples bases de datos y facilidad de uso.