#!/bin/bash

# =====================================================
# SCRIPT DE PRUEBAS API - CRUD MIXTO SPRING BOOT
# =====================================================
# 
# Este script ejecuta pruebas básicas de los endpoints
# REST usando curl para verificar el funcionamiento
# de la API antes de usar Postman.
#
# Uso: ./test-api.sh [BASE_URL]
# Ejemplo: ./test-api.sh http://localhost:8080
# =====================================================

# Configuración
BASE_URL="${1:-http://localhost:8080}"
ADMIN_AUTH="admin:admin"
USER_AUTH="user:password"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Contadores
TESTS_TOTAL=0
TESTS_PASSED=0
TESTS_FAILED=0

# Función para logging
log() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

success() {
    echo -e "${GREEN}✅ PASS:${NC} $1"
    ((TESTS_PASSED++))
}

fail() {
    echo -e "${RED}❌ FAIL:${NC} $1"
    ((TESTS_FAILED++))
}

warning() {
    echo -e "${YELLOW}⚠️  WARN:${NC} $1"
}

# Función para hacer request HTTP
http_request() {
    local method="$1"
    local url="$2"
    local auth="$3"
    local data="$4"
    local content_type="${5:-application/json}"
    
    local auth_header=""
    if [ -n "$auth" ]; then
        auth_header="-u $auth"
    fi
    
    local data_header=""
    if [ -n "$data" ]; then
        if [ "$content_type" = "application/x-www-form-urlencoded" ]; then
            data_header="-d \"$data\""
        else
            data_header="-d '$data'"
        fi
    fi
    
    local cmd="curl -s -w '%{http_code}' -X $method $auth_header -H 'Content-Type: $content_type' $data_header \"$url\""
    eval $cmd
}

# Función para test HTTP
test_endpoint() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local expected_code="$4"
    local auth="$5"
    local data="$6"
    local content_type="$7"
    
    ((TESTS_TOTAL++))
    log "Testing: $test_name"
    
    local url="$BASE_URL$endpoint"
    local response=$(http_request "$method" "$url" "$auth" "$data" "$content_type")
    local http_code="${response: -3}"
    local body="${response%???}"
    
    if [ "$http_code" = "$expected_code" ]; then
        success "$test_name (HTTP $http_code)"
        if [ -n "$body" ] && [ ${#body} -lt 200 ]; then
            echo "    Response: $body"
        fi
    else
        fail "$test_name (Expected: $expected_code, Got: $http_code)"
        if [ -n "$body" ]; then
            echo "    Response: ${body:0:200}..."
        fi
    fi
    echo
}

# Banner inicial
echo
echo "=============================================="
echo "🚀 CRUD MIXTO API TESTING SUITE"
echo "=============================================="
echo "Base URL: $BASE_URL"
echo "Timestamp: $(date)"
echo "=============================================="
echo

# Verificar que el servidor esté corriendo
log "Verificando conectividad del servidor..."
if ! curl -s --connect-timeout 5 "$BASE_URL" > /dev/null; then
    fail "No se puede conectar al servidor en $BASE_URL"
    echo "❌ Asegúrate de que la aplicación Spring Boot esté corriendo"
    exit 1
fi
success "Servidor accesible en $BASE_URL"
echo

# =====================================================
# PRUEBAS DE ENDPOINTS HOME
# =====================================================
echo "🏠 TESTING HOME ENDPOINTS"
echo "----------------------------------------"

test_endpoint "Home redirect" "GET" "/" "302" "" ""
test_endpoint "Home alternative" "GET" "/home" "302" "" ""

# =====================================================
# PRUEBAS DE EMPLEADOS (MySQL/JPA)
# =====================================================
echo "👥 TESTING EMPLEADOS ENDPOINTS (MySQL/JPA)"
echo "----------------------------------------"

# Tests sin autenticación (deben fallar)
test_endpoint "Empleados sin auth" "GET" "/empleados" "401" "" ""

# Tests con autenticación admin
test_endpoint "Listar empleados (admin)" "GET" "/empleados" "200" "$ADMIN_AUTH" ""
test_endpoint "Empleado por ID (admin)" "GET" "/empleados/1" "404" "$ADMIN_AUTH" ""
test_endpoint "Vista crear empleado" "GET" "/empleados/crear" "200" "$ADMIN_AUTH" ""

# Crear empleado de prueba
EMPLEADO_DATA="nombre=Test Employee&cargo=QA Tester&salario=60000.00&email=test.$(date +%s)@empresa.com"
test_endpoint "Crear empleado" "POST" "/empleados" "302" "$ADMIN_AUTH" "$EMPLEADO_DATA" "application/x-www-form-urlencoded"

# Búsquedas
test_endpoint "Buscar empleados" "GET" "/empleados/buscar?termino=test" "200" "$ADMIN_AUTH" ""
test_endpoint "Buscar por nombre" "GET" "/empleados/buscar-nombre?nombre=test" "200" "$ADMIN_AUTH" ""
test_endpoint "Buscar por cargo" "GET" "/empleados/buscar-cargo?cargo=qa" "200" "$ADMIN_AUTH" ""
test_endpoint "Buscar por salario" "GET" "/empleados/buscar-salario?min=50000&max=70000" "200" "$ADMIN_AUTH" ""

# Reportes
test_endpoint "Vista reportes empleados" "GET" "/empleados/reportes" "200" "$ADMIN_AUTH" ""
test_endpoint "Exportar CSV empleados" "GET" "/empleados/exportar/csv" "200" "$ADMIN_AUTH" ""
test_endpoint "Exportar Excel empleados" "GET" "/empleados/exportar/excel" "200" "$ADMIN_AUTH" ""

# =====================================================
# PRUEBAS DE PROYECTOS (MongoDB)
# =====================================================
echo "📋 TESTING PROYECTOS ENDPOINTS (MongoDB)"
echo "----------------------------------------"

# Tests básicos (acceso público)
test_endpoint "Listar proyectos" "GET" "/proyectos" "200" "" ""
test_endpoint "Vista crear proyecto" "GET" "/proyectos/crear" "200" "" ""
test_endpoint "Proyecto por ID (no existe)" "GET" "/proyectos/507f1f77bcf86cd799439011" "404" "" ""

# Crear proyecto de prueba
PROYECTO_DATA="nombre=Proyecto Test&descripcion=Proyecto de prueba automatizada&empleadoId=1&fechaInicio=2025-10-15&completado=false"
test_endpoint "Crear proyecto" "POST" "/proyectos" "302" "" "$PROYECTO_DATA" "application/x-www-form-urlencoded"

# Búsquedas y filtros
test_endpoint "Buscar proyectos" "GET" "/proyectos/buscar?nombre=test" "200" "" ""
test_endpoint "Proyectos por estado" "GET" "/proyectos/estado?completado=false" "200" "" ""
test_endpoint "Proyectos por empleado" "GET" "/proyectos/empleado/1" "200" "" ""

# Estadísticas y reportes
test_endpoint "Vista reportes proyectos" "GET" "/proyectos/reportes" "200" "" ""
test_endpoint "Estadísticas API" "GET" "/proyectos/estadisticas" "200" "" ""
test_endpoint "Exportar CSV proyectos" "GET" "/proyectos/exportar/csv" "200" "" ""
test_endpoint "Exportar Excel proyectos" "GET" "/proyectos/exportar/excel" "200" "" ""
test_endpoint "Reporte detallado JSON" "GET" "/proyectos/exportar/detallado" "200" "" ""

# Gestión de tareas (requiere ID de proyecto válido)
TAREA_JSON='{"nombre":"Tarea Test","descripcion":"Tarea de prueba","estado":"pendiente","prioridad":"media"}'
test_endpoint "Agregar tarea (ID inválido)" "POST" "/proyectos/507f1f77bcf86cd799439011/tareas" "404" "" "$TAREA_JSON"

# =====================================================
# PRUEBAS DE SEGURIDAD
# =====================================================
echo "🔐 TESTING SECURITY"
echo "----------------------------------------"

# Autenticación con usuario regular
test_endpoint "Empleados (user auth)" "GET" "/empleados" "200" "$USER_AUTH" ""
test_endpoint "Crear empleado (user auth)" "POST" "/empleados" "403" "$USER_AUTH" "$EMPLEADO_DATA" "application/x-www-form-urlencoded"

# Credenciales incorrectas
test_endpoint "Auth inválida" "GET" "/empleados" "401" "invalid:credentials" ""

# =====================================================
# PRUEBAS DE ENDPOINTS INEXISTENTES
# =====================================================
echo "🔍 TESTING ERROR HANDLING"
echo "----------------------------------------"

test_endpoint "Endpoint inexistente" "GET" "/api/inexistente" "404" "" ""
test_endpoint "Método no permitido" "PATCH" "/empleados" "405" "$ADMIN_AUTH" ""

# =====================================================
# RESUMEN FINAL
# =====================================================
echo
echo "=============================================="
echo "📊 RESUMEN DE PRUEBAS"
echo "=============================================="
echo "Total de pruebas:    $TESTS_TOTAL"
echo -e "Pruebas exitosas:    ${GREEN}$TESTS_PASSED${NC}"
echo -e "Pruebas fallidas:    ${RED}$TESTS_FAILED${NC}"
echo

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 ¡TODAS LAS PRUEBAS PASARON!${NC}"
    echo "✅ La API está funcionando correctamente"
    echo "📋 Puedes proceder a usar la colección de Postman"
else
    echo -e "${RED}⚠️  ALGUNAS PRUEBAS FALLARON${NC}"
    echo "🔍 Revisa los logs de la aplicación para más detalles"
    echo "🛠️  Verifica la configuración de base de datos"
fi

echo
echo "=============================================="
echo "📁 ARCHIVOS DISPONIBLES:"
echo "=============================================="
echo "📋 postman-collection.json    - Colección completa de Postman"
echo "🌍 postman-environment.json   - Variables de entorno"
echo "📖 API_DOCUMENTATION.md       - Documentación detallada"
echo "🧪 test-api.sh               - Este script de pruebas"
echo

echo "🚀 SIGUIENTES PASOS:"
echo "1. Importar archivos JSON en Postman"
echo "2. Configurar variables de entorno"
echo "3. Ejecutar pruebas detalladas en Postman"
echo "4. Revisar documentación para casos avanzados"
echo

exit $TESTS_FAILED