# 🐳 Instalación Docker Desktop en Windows

## 📋 **Instrucciones Paso a Paso para tu Windows 10 Enterprise**

✅ **Tu sistema es compatible**: Windows 10 Enterprise 2009

## **PASO 1: Preparar Windows**

### 🔧 **Habilitar características de Windows**
```powershell
# ⚠️ EJECUTAR PowerShell COMO ADMINISTRADOR
# Clic derecho en PowerShell → "Ejecutar como administrador"

# Habilitar WSL y Virtualización
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart

# Resultado esperado: "La operación se completó correctamente"
```

### 🔄 **Reiniciar Windows (OBLIGATORIO)**
```powershell
# Reiniciar ahora
shutdown /r /t 0
```

## **PASO 2: Instalar WSL 2 Kernel**

### 📥 **Después del reinicio**
```powershell
# Abrir PowerShell como administrador nuevamente

# Descargar e instalar WSL 2 kernel update
# Ir a: https://aka.ms/wsl2kernel
# Descargar: wsl_update_x64.msi
# Ejecutar el instalador descargado

# Configurar WSL 2 como predeterminado
wsl --set-default-version 2
```

## **PASO 3: Descargar Docker Desktop**

### 🌐 **Descargar desde sitio oficial**
- **URL**: https://www.docker.com/products/docker-desktop/
- **Archivo**: Docker Desktop Installer.exe
- **Tamaño**: ~500MB
- **Versión recomendada**: Última stable

## **PASO 4: Instalar Docker Desktop**

### 🛠️ **Ejecutar instalador**
```bash
# Ejecutar: Docker Desktop Installer.exe
# Durante la instalación seleccionar:
✅ "Use WSL 2 instead of Hyper-V (recommended)"
✅ "Add shortcut to desktop"
✅ "Use Docker Compose V2"

# La instalación toma ~5-10 minutos
```

### 🔄 **Reiniciar después de instalación**
```powershell
# Docker pedirá reiniciar
shutdown /r /t 0
```

## **PASO 5: Configurar Docker Desktop**

### 🚀 **Primera ejecución**
```bash
# Después del reinicio:
# 1. Buscar "Docker Desktop" en el menú inicio
# 2. Ejecutar Docker Desktop
# 3. Esperar a que aparezca el whale icon en la bandeja del sistema
# 4. Docker estará listo cuando el icono deje de parpadear
```

### ⚙️ **Configuración recomendada**
```bash
# En Docker Desktop:
# Settings → General
✅ "Use the WSL 2 based engine"
✅ "Start Docker Desktop when you log in"

# Settings → Resources → WSL Integration
✅ "Enable integration with my default WSL distro"
```

## **PASO 6: Verificar Instalación**

### 🧪 **Probar Docker**
```powershell
# Abrir PowerShell normal (no como administrador)
cd c:\java2931811F\demomixto

# Verificar versiones
docker --version
docker-compose --version

# Probar con imagen de prueba
docker run hello-world

# Resultado esperado:
# "Hello from Docker!"
# "This message shows that your installation appears to be working correctly."
```

## **PASO 7: Ejecutar DemoMixto con Docker**

### 🐳 **Ejecutar tu proyecto**
```bash
# En el directorio de tu proyecto
cd c:\java2931811F\demomixto

# ⚠️ IMPORTANTE: Detener DemoApplication.java si está ejecutándose
# Ir a VS Code → Terminal "Run: DemoApplication" → Ctrl+C

# Iniciar stack Docker completo
docker-compose up -d

# Ver estado de servicios
docker-compose ps

# Esperar ~2-3 minutos para que todos los servicios estén UP
```

### 🌐 **Acceder a las aplicaciones**
```bash
# Una vez que todos los servicios estén UP:

✅ http://localhost:8080  - DemoMixto (tu aplicación)
✅ http://localhost:8081  - Adminer (MySQL GUI)
✅ http://localhost:8082  - Mongo Express (MongoDB GUI)

# Credenciales:
# DemoMixto: admin/admin o user/password
# Adminer: demomixto/DemoMixto2025!
# Mongo Express: admin/admin
```

## **PASO 8: Testing Automatizado**

### 🧪 **Ejecutar tests completos**
```bash
# Script de testing incluido
docker\scripts\test-docker.bat

# Este script valida:
✅ Conectividad de todos los servicios
✅ APIs de empleados y proyectos
✅ Interfaces web
✅ Bases de datos MySQL y MongoDB
```

## 🔧 **Troubleshooting Común**

### ❌ **Error: "Docker Desktop requires Windows 10/11"**
- **Solución**: Tu Windows 10 Enterprise es compatible ✅

### ❌ **Error: "WSL 2 installation is incomplete"**
```bash
# Solución:
# 1. Descargar: https://aka.ms/wsl2kernel
# 2. Ejecutar wsl_update_x64.msi
# 3. Reiniciar Docker Desktop
```

### ❌ **Error: "Hardware virtualization is not available"**
```bash
# Solución:
# 1. Reiniciar PC
# 2. Entrar al BIOS/UEFI (F2, F12, Delete)
# 3. Habilitar "Virtualization Technology" o "VT-x"
# 4. Guardar y reiniciar
```

### ❌ **Error: "Port 8080 is already in use"**
```bash
# Solución:
# 1. Detener DemoApplication.java en VS Code
# 2. O cambiar puerto en docker-compose.yml
# 3. Reintentar docker-compose up -d
```

### ❌ **Docker Desktop no inicia**
```bash
# Solución:
# 1. Reiniciar Windows
# 2. Ejecutar como administrador: "Docker Desktop"
# 3. Esperar 2-3 minutos
# 4. Si persiste, reinstalar Docker Desktop
```

## 🎯 **Resumen Final**

### ✅ **Una vez completada la instalación tendrás:**

```
🔥 MODO DESARROLLO (ACTUAL)          🐳 MODO DOCKER (NUEVO)
├── DemoApplication.java             ├── docker-compose up -d
├── Puerto: 8080                     ├── Puertos: 8080, 8081, 8082
├── MySQL local + MongoDB Atlas      ├── MySQL + MongoDB containerizados
└── ✅ YA FUNCIONA                   └── ✅ FUNCIONARÁ DESPUÉS DE INSTALAR
```

### 🎯 **Beneficios del modo Docker:**
- ✅ **Bases de datos incluidas** (no necesitas MySQL local)
- ✅ **GUIs de administración** (Adminer + Mongo Express)
- ✅ **Aislamiento completo** (no interfiere con otros proyectos)
- ✅ **Fácil distribución** (comparte tu entorno exacto)
- ✅ **Testing automatizado** (validación completa incluida)

---

## 📞 **¿Necesitas ayuda durante la instalación?**

Si encuentras algún problema durante cualquier paso:
1. **Captura pantalla** del error específico
2. **Copia el mensaje** completo del error
3. **Indica en qué paso** te quedaste

**Tu configuración Docker ya está lista en el proyecto, solo necesitas Docker Desktop en el PC** 🎯