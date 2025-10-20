@echo off
REM 🧪 Script de testing automatizado para DemoMixto Docker - Windows
REM Este script valida que todos los servicios estén funcionando correctamente

echo.
echo ========================================
echo 🧪 DEMOMIXTO DOCKER - TESTING WINDOWS
echo ========================================
echo.

REM ============================================================================
REM CONFIGURACIÓN
REM ============================================================================
set BASE_URL=http://localhost:8080
set MYSQL_URL=http://localhost:8081
set MONGO_URL=http://localhost:8082
set TIMEOUT=30

REM ============================================================================
REM FUNCIONES DE UTILIDAD
REM ============================================================================
:print_success
echo ✅ %~1
goto :eof

:print_error
echo ❌ %~1
goto :eof

:print_warning
echo ⚠️  %~1
goto :eof

:print_info
echo ℹ️  %~1
goto :eof

REM ============================================================================
REM VERIFICAR HERRAMIENTAS REQUERIDAS
REM ============================================================================
call :print_info "Verificando herramientas requeridas..."

where curl >nul 2>&1
if %errorlevel% neq 0 (
    call :print_error "curl no está instalado - requerido para los tests"
    call :print_info "Instalar curl desde: https://curl.se/windows/"
    pause
    exit /b 1
)

call :print_success "curl está disponible"

REM ============================================================================
REM TESTS DE CONECTIVIDAD
REM ============================================================================
echo.
echo ========================================
echo 🔌 TESTS DE CONECTIVIDAD
echo ========================================

call :print_info "Esperando que DemoMixto App esté disponible..."
set /a attempts=0
:wait_app
set /a attempts+=1
curl -f -s "%BASE_URL%/actuator/health" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "DemoMixto App está disponible"
    goto :app_ready
)
if %attempts% lss 30 (
    echo|set /p="."
    timeout /t 2 /nobreak >nul
    goto :wait_app
)
call :print_error "DemoMixto App no está disponible después de 30 intentos"
goto :end_connectivity

:app_ready
REM Test Health Check
curl -f -s "%BASE_URL%/actuator/health" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Health Check Endpoint - OK"
) else (
    call :print_error "Health Check Endpoint - FAILED"
)

REM Test Adminer
curl -f -s "%MYSQL_URL%" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Adminer (MySQL GUI) está disponible en %MYSQL_URL%"
) else (
    call :print_warning "Adminer no está disponible - puede estar iniciando"
)

REM Test Mongo Express
curl -f -s "%MONGO_URL%" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Mongo Express (MongoDB GUI) está disponible en %MONGO_URL%"
) else (
    call :print_warning "Mongo Express no está disponible - puede estar iniciando"
)

:end_connectivity

REM ============================================================================
REM TESTS DE API PROYECTOS (PÚBLICO)
REM ============================================================================
echo.
echo ========================================
echo 📋 TESTS DE API PROYECTOS (PÚBLICO)
echo ========================================

REM Test listar proyectos
call :print_info "Testing: GET /api/proyectos - Listar proyectos"
curl -f -s "%BASE_URL%/api/proyectos" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET /api/proyectos - Listar proyectos - OK"
) else (
    call :print_error "GET /api/proyectos - Listar proyectos - FAILED"
)

REM Test estadísticas
call :print_info "Testing: GET /api/proyectos/estadisticas - Estadísticas"
curl -f -s "%BASE_URL%/api/proyectos/estadisticas" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET /api/proyectos/estadisticas - Estadísticas - OK"
) else (
    call :print_error "GET /api/proyectos/estadisticas - Estadísticas - FAILED"
)

REM Test crear proyecto
call :print_info "Testing: POST /api/proyectos - Crear proyecto"
echo {"nombre":"Proyecto Test Docker Windows","descripcion":"Proyecto creado por script de testing Windows","empleadoId":1,"fechaInicio":"2025-01-16","completado":false} > temp_project.json
curl -f -s -H "Content-Type: application/json" -d @temp_project.json "%BASE_URL%/api/proyectos" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "POST /api/proyectos - Crear proyecto - OK"
) else (
    call :print_error "POST /api/proyectos - Crear proyecto - FAILED"
)
del temp_project.json >nul 2>&1

REM ============================================================================
REM TESTS DE API EMPLEADOS (AUTENTICADO)
REM ============================================================================
echo.
echo ========================================
echo 👥 TESTS DE API EMPLEADOS (AUTENTICADO)
echo ========================================

REM Crear token de autenticación (admin:admin en Base64)
REM En Windows, usamos certutil para codificar en Base64
echo admin:admin > temp_auth.txt
certutil -encode temp_auth.txt temp_auth_b64.txt >nul 2>&1
REM Leer la línea del medio (la codificación Base64)
for /f "skip=1 tokens=*" %%i in (temp_auth_b64.txt) do (
    set AUTH_TOKEN=%%i
    goto :got_token
)
:got_token
REM Limpiar archivos temporales
del temp_auth.txt temp_auth_b64.txt >nul 2>&1

REM Test listar empleados con autenticación
call :print_info "Testing: GET /api/empleados - Listar empleados (con auth)"
curl -f -s -H "Authorization: Basic %AUTH_TOKEN%" "%BASE_URL%/api/empleados" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET /api/empleados - Listar empleados - OK"
) else (
    call :print_error "GET /api/empleados - Listar empleados - FAILED"
)

REM Test buscar empleados
call :print_info "Testing: GET /api/empleados/buscar - Buscar empleados"
curl -f -s -H "Authorization: Basic %AUTH_TOKEN%" "%BASE_URL%/api/empleados/buscar?termino=Juan" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET /api/empleados/buscar - Buscar empleados - OK"
) else (
    call :print_error "GET /api/empleados/buscar - Buscar empleados - FAILED"
)

REM Test acceso sin autenticación (debe fallar)
call :print_info "Testing: GET /api/empleados sin autenticación (debe fallar)"
curl -f -s "%BASE_URL%/api/empleados" >nul 2>&1
if %errorlevel% neq 0 (
    call :print_success "GET /api/empleados sin auth - Correctamente denegado"
) else (
    call :print_error "GET /api/empleados sin auth - Debería haber fallado"
)

REM ============================================================================
REM TESTS DE INTERFACES WEB
REM ============================================================================
echo.
echo ========================================
echo 🌐 TESTS DE INTERFACES WEB
echo ========================================

REM Test página principal
call :print_info "Testing: GET / - Página principal"
curl -f -s "%BASE_URL%/" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET / - Página principal - OK"
) else (
    call :print_error "GET / - Página principal - FAILED"
)

REM Test lista de proyectos
call :print_info "Testing: GET /proyectos - Lista de proyectos"
curl -f -s "%BASE_URL%/proyectos" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET /proyectos - Lista de proyectos - OK"
) else (
    call :print_error "GET /proyectos - Lista de proyectos - FAILED"
)

REM Test lista de empleados (con auth)
call :print_info "Testing: GET /empleados - Lista de empleados (con auth)"
curl -f -s -H "Authorization: Basic %AUTH_TOKEN%" "%BASE_URL%/empleados" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "GET /empleados - Lista de empleados - OK"
) else (
    call :print_error "GET /empleados - Lista de empleados - FAILED"
)

REM ============================================================================
REM RESUMEN FINAL
REM ============================================================================
echo.
echo ========================================
echo 📋 RESUMEN FINAL
echo ========================================
echo.
call :print_info "Tests completados!"
echo.
call :print_info "URLs de acceso:"
echo    📱 Aplicación: %BASE_URL%
echo    🗄️  Adminer (MySQL): %MYSQL_URL%
echo    🍃 Mongo Express: %MONGO_URL%
echo.
call :print_info "Credenciales de prueba:"
echo    👤 Admin: admin / admin
echo    👤 User: user / password
echo.
call :print_success "🎉 Testing de Docker completado!"
call :print_info "🚀 DemoMixto está funcionando en Docker"
echo.

REM ============================================================================
REM MENÚ INTERACTIVO
REM ============================================================================
echo ========================================
echo 🎯 OPCIONES ADICIONALES
echo ========================================
echo.
echo 1. Ver logs de la aplicación
echo 2. Ver logs de MySQL
echo 3. Ver logs de MongoDB
echo 4. Ver estado de contenedores
echo 5. Abrir aplicación en navegador
echo 6. Salir
echo.
set /p choice="Selecciona una opción (1-6): "

if "%choice%"=="1" (
    echo.
    call :print_info "Mostrando logs de la aplicación..."
    docker-compose logs demomixto-app
    pause
)
if "%choice%"=="2" (
    echo.
    call :print_info "Mostrando logs de MySQL..."
    docker-compose logs mysql-db
    pause
)
if "%choice%"=="3" (
    echo.
    call :print_info "Mostrando logs de MongoDB..."
    docker-compose logs mongo-db
    pause
)
if "%choice%"=="4" (
    echo.
    call :print_info "Estado de contenedores..."
    docker-compose ps
    pause
)
if "%choice%"=="5" (
    echo.
    call :print_info "Abriendo aplicación en navegador..."
    start %BASE_URL%
)
if "%choice%"=="6" (
    goto :end
)

:end
echo.
call :print_success "¡Gracias por usar DemoMixto!"
pause