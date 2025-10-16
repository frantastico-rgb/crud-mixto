# 🐳 Guía de Instalación Docker en Windows - DemoMixto

## 🎯 **Objetivo**
Instalar Docker Desktop y todas las dependencias necesarias para ejecutar DemoMixto en Windows.

---

## 📋 **Prerrequisitos del Sistema**

### ✅ **Verificaciones Previas**
- ✅ Windows 10 versión 2004+ o Windows 11
- ✅ 64-bit processor con traducción de dirección de segundo nivel (SLAT)
- ✅ 4GB de RAM mínimo (8GB recomendado)
- ✅ Virtualización habilitada en BIOS/UEFI

### 🔍 **Verificar Versión de Windows**
1. Presiona `Win + R`
2. Escribe `winver` y presiona Enter
3. Verifica que sea Windows 10 build 19041+ o Windows 11

---

## 🛠️ **Paso 1: Habilitar Características de Windows**

### **Opción A: Mediante PowerShell (Como Administrador)**
```powershell
# Ejecutar PowerShell como Administrador
# Habilitar WSL y Plataforma de Máquina Virtual
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart

# Reiniciar el sistema
Restart-Computer
```

### **Opción B: Mediante Panel de Control**
1. Abrir "Panel de Control" → "Programas" → "Activar o desactivar las características de Windows"
2. Marcar las siguientes casillas:
   - ☑️ **Subsistema de Windows para Linux**
   - ☑️ **Plataforma de máquina virtual**
3. Hacer clic en "Aceptar" y reiniciar cuando se solicite

---

## 🐳 **Paso 2: Instalar WSL 2**

### **Después del reinicio, ejecutar en PowerShell (como Administrador):**
```powershell
# Instalar WSL 2 y distribución Ubuntu por defecto
wsl --install

# Si ya tienes WSL, actualizar a WSL 2
wsl --update
wsl --set-default-version 2
```

### **Verificar instalación:**
```powershell
wsl --list --verbose
```

**Salida esperada:**
```
  NAME                   STATE           VERSION
  Ubuntu                 Running         2
```

---

## 🐳 **Paso 3: Descargar e Instalar Docker Desktop**

### **Descarga:**
1. Ir a: https://docs.docker.com/desktop/install/windows-install/
2. Descargar "Docker Desktop for Windows"
3. El archivo será: `Docker Desktop Installer.exe` (~500MB)

### **Instalación:**
1. Ejecutar el instalador como **Administrador**
2. **Configuración recomendada:**
   - ☑️ **Enable Hyper-V Windows Features** (si está disponible)
   - ☑️ **Install required Windows components for WSL 2**
   - ☑️ **Add shortcut to desktop**
3. Hacer clic en "Ok" para comenzar la instalación
4. **Reiniciar** cuando se solicite

### **Primera Ejecución:**
1. Abrir Docker Desktop desde el escritorio o menú inicio
2. Aceptar los términos de servicio
3. **Configuración inicial recomendada:**
   - Enable WSL 2 based engine: ☑️ **Habilitado**
   - Start Docker Desktop when you log in: ☑️ **Habilitado**
4. Docker Desktop se iniciará automáticamente

---

## ✅ **Paso 4: Verificar Instalación**

### **Abrir PowerShell normal (no administrador) y ejecutar:**
```powershell
# Verificar Docker
docker --version
docker-compose --version

# Test básico
docker run hello-world
```

### **Salida esperada:**
```
Docker version 24.0.x, build xxxxx
Docker Compose version v2.20.x

Hello from Docker!
This message shows that your installation...
```

---

## 🚀 **Paso 5: Ejecutar DemoMixto**

### **Una vez que Docker esté funcionando:**
```powershell
# Navegar al proyecto
cd C:\java2931811F\demomixto

# Verificar archivos Docker
dir Dockerfile
dir docker-compose.yml

# Ejecutar stack completo
docker-compose up -d

# Verificar servicios
docker-compose ps
```

### **Verificar URLs:**
- 📱 **Aplicación**: http://localhost:8080
- 🗄️ **Adminer (MySQL)**: http://localhost:8081  
- 🍃 **Mongo Express**: http://localhost:8082

---

## 🧪 **Paso 6: Executar Tests**

### **Windows Script:**
```cmd
# Ejecutar script de testing
docker\scripts\test-docker.bat
```

---

## 🔧 **Troubleshooting Común**

### **❌ Error: "Hardware assisted virtualization..."**
**Solución:** Habilitar virtualización en BIOS:
1. Reiniciar PC y entrar al BIOS (F2, F12, DEL según fabricante)
2. Buscar "Virtualization Technology" o "VT-x" o "AMD-V"
3. Cambiar a "Enabled"
4. Guardar y salir del BIOS

### **❌ Error: "WSL 2 installation is incomplete"**
**Solución:**
```powershell
# Descargar e instalar kernel WSL 2
# Ir a: https://aka.ms/wsl2kernel
# Ejecutar: wsl_update_x64.msi
wsl --update
```

### **❌ Error: "Docker Desktop failed to start"**
**Soluciones:**
1. Reiniciar Docker Desktop como Administrador
2. Resetear Docker Desktop: Settings → Troubleshoot → Reset to factory defaults
3. Verificar que Hyper-V esté habilitado (Windows Pro/Enterprise)

### **❌ Puertos 8080, 8081, 8082 no responden**
**Soluciones:**
```powershell
# Verificar estado de contenedores
docker-compose ps

# Ver logs de aplicación
docker-compose logs demomixto-app

# Reiniciar servicios
docker-compose restart

# Si persiste, rebuild completo
docker-compose down
docker-compose up --build -d
```

---

## 📊 **Verificación Final**

### **Lista de verificación:**
- [ ] Windows 10/11 versión correcta
- [ ] WSL 2 instalado y funcionando
- [ ] Docker Desktop instalado y ejecutándose
- [ ] `docker --version` funciona
- [ ] `docker-compose --version` funciona
- [ ] `docker run hello-world` exitoso
- [ ] DemoMixto contenedores ejecutándose
- [ ] URLs respondiendo: 8080, 8081, 8082
- [ ] Tests automatizados pasando

---

## 🎉 **¡Listo para usar DemoMixto!**

Una vez completados todos los pasos:
```powershell
cd C:\java2931811F\demomixto
docker-compose up -d
docker\scripts\test-docker.bat
```

**URLs de acceso:**
- 📱 **App**: http://localhost:8080 (admin/admin)
- 🗄️ **MySQL**: http://localhost:8081
- 🍃 **MongoDB**: http://localhost:8082

---

## 📞 **Soporte Adicional**

Si encuentras problemas:
1. Revisar logs: `docker-compose logs`
2. Verificar puertos ocupados: `netstat -an | findstr "8080 8081 8082"`
3. Restart Docker Desktop
4. Reiniciar Windows si es necesario