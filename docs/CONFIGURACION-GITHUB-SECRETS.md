# 🔐 Guía Completa: Configuración GitHub Secrets

## 🎯 **¿Qué son los GitHub Secrets?**

Los **GitHub Secrets** son variables de entorno encriptadas que permiten a GitHub Actions acceder de forma segura a servicios externos como DockerHub, bases de datos y plataformas de deployment.

---

## 📋 **Secrets Requeridos para DemoMixto**

```
🔑 DOCKERHUB_USERNAME  # Tu usuario de DockerHub
🔑 DOCKERHUB_TOKEN     # Token de acceso DockerHub  
🔑 MONGODB_URI         # String conexión MongoDB Atlas
```

---

## 🛠️ **PASO A PASO: Configurar en GitHub**

### **1️⃣ Acceder a la Configuración del Repositorio**

```
1. Ir a tu repositorio: https://github.com/frantastico-rgb/crud-mixto
2. Hacer clic en "⚙️ Settings" (pestaña superior derecha)
3. En el menú izquierdo, buscar "🔐 Secrets and variables"
4. Hacer clic en "Actions"
```

### **2️⃣ Crear los Secrets Uno por Uno**

#### **🐳 DOCKERHUB_USERNAME**
```
1. Hacer clic en "New repository secret"
2. Name: DOCKERHUB_USERNAME
3. Secret: [tu-usuario-dockerhub]  # Ejemplo: frantastico
4. Hacer clic en "Add secret"
```

#### **🐳 DOCKERHUB_TOKEN**
```
⚠️ IMPORTANTE: Necesitas crear un Access Token en DockerHub primero

CREAR TOKEN EN DOCKERHUB:
1. Ir a: https://hub.docker.com/settings/security
2. Hacer clic en "New Access Token"
3. Description: "GitHub Actions CRUD-Mixto"
4. Access permissions: "Read, Write, Delete"
5. Generar y COPIAR el token (solo se muestra una vez)

AGREGAR EN GITHUB:
1. Name: DOCKERHUB_TOKEN
2. Secret: [token-copiado-de-dockerhub]
3. Add secret
```

#### **🍃 MONGODB_URI**
```
1. Ir a MongoDB Atlas: https://cloud.mongodb.com
2. En tu cluster, hacer clic en "Connect"
3. Elegir "Connect your application"
4. Copiar la connection string completa

FORMATO:
mongodb+srv://usuario:password@cluster.mongodb.net/empresa?retryWrites=true&w=majority

AGREGAR EN GITHUB:
1. Name: MONGODB_URI
2. Secret: [connection-string-completa]
3. Add secret
```

### **3️⃣ Verificar Configuración**

```
Al finalizar deberías ver en GitHub > Settings > Secrets:

✅ DOCKERHUB_USERNAME  (Updated now)
✅ DOCKERHUB_TOKEN     (Updated now)  
✅ MONGODB_URI         (Updated now)

🔐 Los valores están encriptados y no se pueden ver
```

---

## 🔗 **PASO B: Conectar GitHub Repo a Render**

### **🌍 Configurar Servicio en Render**

```
1. Ir a: https://render.com
2. Hacer clic en "New +"
3. Elegir "Web Service"
4. Conectar con GitHub:
   - Autorizar acceso a repos
   - Seleccionar: frantastico-rgb/crud-mixto
5. Configuración del servicio:
   - Name: crud-mixto
   - Environment: Docker
   - Branch: main
   - Dockerfile path: ./Dockerfile
6. Environment Variables:
   - SPRING_PROFILES_ACTIVE: render
   - MONGODB_URI: [mismo valor que GitHub Secret]
7. Crear servicio
```

### **🚀 Auto-Deploy Configurado**

```
✅ Una vez conectado, Render:
- Detecta automáticamente push a main branch
- Usa render.yaml para configuración
- Despliega usando Docker
- Ejecuta health checks
- Proporciona URL pública
```

---

## 🚀 **PASO C: Activar Pipeline con Push**

### **📝 Mensaje de Commit Recomendado**

```bash
# Desde VS Code - Control de Código Fuente:

git add .
git commit -m "ci: activar pipeline CI/CD completo

- Configurar GitHub Secrets (DockerHub + MongoDB)
- Conectar repository a Render deployment
- Habilitar auto-deploy en push a main branch
- Pipeline: test → build → push → deploy
- Deployment URLs: Render principal + Railway backup

Status: CI/CD pipeline completamente configurado ✅"

git push origin main
```

### **🔄 Qué Sucede Después del Push**

```
⏳ GITHUB ACTIONS PIPELINE:
├── 1. Trigger: Push detectado
├── 2. Checkout: Descargar código
├── 3. Tests: Ejecutar unit tests (H2)
├── 4. Docker: Build multi-stage
├── 5. Registry: Push a DockerHub  
├── 6. Health: Validar imagen
└── 7. ✅ Pipeline completo

⏳ RENDER AUTO-DEPLOY:
├── 1. Webhook: GitHub notifica cambios
├── 2. Pull: Descargar imagen de DockerHub
├── 3. Deploy: Actualizar servicio
├── 4. Health: /actuator/health check
└── 5. ✅ Live en https://crud-mixto.onrender.com
```

---

## 🔍 **Verificación y Monitoreo**

### **✅ Cómo Saber que Todo Funciona**

```
GITHUB ACTIONS:
1. Ir a: GitHub repo > Actions tab
2. Ver último workflow run con ✅ verde
3. Revisar logs de cada step

RENDER DEPLOYMENT:
1. Dashboard Render > crud-mixto service
2. Ver "Deploy successful" 
3. Status: "Live" con URL activa

APLICACIÓN FUNCIONANDO:
1. Abrir: https://crud-mixto.onrender.com
2. Verificar acceso a empleados y proyectos
3. Health check: /actuator/health
```

### **🚨 Troubleshooting Común**

```
❌ "Docker login failed"
→ Verificar DOCKERHUB_USERNAME y DOCKERHUB_TOKEN

❌ "MongoDB connection timeout"  
→ Verificar MONGODB_URI y whitelist IP

❌ "Render build failed"
→ Revisar logs en Render dashboard

❌ "Health check failing"
→ Verificar /actuator/health endpoint
```

---

## 💡 **Tips Importantes**

```
🔐 SEGURIDAD:
- NUNCA commitear secrets en código
- Usar siempre GitHub Secrets para credenciales
- Regenerar tokens si se comprometen

⚡ PERFORMANCE:
- Pipeline toma ~5-10 minutos total
- Render cold start puede tardar 1-2 minutos
- Health checks evitan downtime

🔄 UPDATES:
- Cada push a main = auto-deploy
- Railway requiere trigger manual [deploy-railway]
- Rollback automático si falla health check
```

---

## 📞 **Soporte**

Si encuentras problemas:
1. Revisar logs en GitHub Actions
2. Verificar Render deployment logs  
3. Consultar esta guía paso a paso
4. Crear issue en GitHub repo para soporte