#!/bin/bash

# 🧪 Script de testing automatizado para DemoMixto Docker
# Este script valida que todos los servicios estén funcionando correctamente

# ============================================================================
# CONFIGURACIÓN
# ============================================================================
BASE_URL="http://localhost:8080"
MYSQL_URL="http://localhost:8081"
MONGO_URL="http://localhost:8082"
TIMEOUT=30
TEST_RESULTS=()

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ============================================================================
# FUNCIONES DE UTILIDAD
# ============================================================================
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}🧪 $1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
    TEST_RESULTS+=("✅ $1")
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
    TEST_RESULTS+=("❌ $1")
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
    TEST_RESULTS+=("⚠️  $1")
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Función para esperar que un servicio esté disponible
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    print_info "Esperando que $service_name esté disponible..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s "$url" > /dev/null 2>&1; then
            print_success "$service_name está disponible"
            return 0
        fi
        
        echo -n "."
        sleep 2
        ((attempt++))
    done
    
    print_error "$service_name no está disponible después de $max_attempts intentos"
    return 1
}

# Función para realizar petición HTTP y validar respuesta
test_http_endpoint() {
    local url=$1
    local expected_status=$2
    local description=$3
    local auth=$4
    
    local auth_header=""
    if [ ! -z "$auth" ]; then
        auth_header="-H \"Authorization: Basic $auth\""
    fi
    
    print_info "Testing: $description"
    
    local response=$(eval curl -s -w "HTTPSTATUS:%{http_code}" $auth_header "$url" 2>/dev/null)
    local body=$(echo "$response" | sed -E 's/HTTPSTATUS:[0-9]{3}$//')
    local status=$(echo "$response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
    
    if [ "$status" = "$expected_status" ]; then
        print_success "$description - Status: $status"
        return 0
    else
        print_error "$description - Expected: $expected_status, Got: $status"
        return 1
    fi
}

# ============================================================================
# TESTS DE CONECTIVIDAD
# ============================================================================
test_connectivity() {
    print_header "TESTS DE CONECTIVIDAD"
    
    # Test 1: Aplicación principal
    if wait_for_service "$BASE_URL/actuator/health" "DemoMixto App"; then
        test_http_endpoint "$BASE_URL/actuator/health" "200" "Health Check Endpoint"
    fi
    
    # Test 2: Adminer (MySQL GUI)
    if curl -f -s "$MYSQL_URL" > /dev/null 2>&1; then
        print_success "Adminer (MySQL GUI) está disponible en $MYSQL_URL"
    else
        print_warning "Adminer no está disponible - puede estar iniciando"
    fi
    
    # Test 3: Mongo Express (MongoDB GUI)
    if curl -f -s "$MONGO_URL" > /dev/null 2>&1; then
        print_success "Mongo Express (MongoDB GUI) está disponible en $MONGO_URL"
    else
        print_warning "Mongo Express no está disponible - puede estar iniciando"
    fi
}

# ============================================================================
# TESTS DE PROYECTOS (PÚBLICO)
# ============================================================================
test_proyectos_api() {
    print_header "TESTS DE API PROYECTOS (PÚBLICO)"
    
    # Test 1: Listar proyectos
    test_http_endpoint "$BASE_URL/api/proyectos" "200" "GET /api/proyectos - Listar proyectos"
    
    # Test 2: Estadísticas de proyectos
    test_http_endpoint "$BASE_URL/api/proyectos/estadisticas" "200" "GET /api/proyectos/estadisticas - Estadísticas"
    
    # Test 3: Crear proyecto (POST)
    print_info "Testing: POST /api/proyectos - Crear proyecto"
    local create_response=$(curl -s -w "HTTPSTATUS:%{http_code}" \
        -H "Content-Type: application/json" \
        -d '{
            "nombre": "Proyecto Test Docker",
            "descripcion": "Proyecto creado por script de testing",
            "empleadoId": 1,
            "fechaInicio": "2025-01-16",
            "completado": false
        }' \
        "$BASE_URL/api/proyectos" 2>/dev/null)
    
    local create_status=$(echo "$create_response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
    local create_body=$(echo "$create_response" | sed -E 's/HTTPSTATUS:[0-9]{3}$//')
    
    if [ "$create_status" = "200" ]; then
        print_success "POST /api/proyectos - Crear proyecto - Status: $create_status"
        
        # Extraer ID del proyecto creado para testing de actualización
        local project_id=$(echo "$create_body" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
        if [ ! -z "$project_id" ]; then
            print_info "Proyecto creado con ID: $project_id"
            
            # Test 4: Obtener proyecto específico
            test_http_endpoint "$BASE_URL/api/proyectos/$project_id" "200" "GET /api/proyectos/$project_id - Obtener proyecto"
            
            # Test 5: Eliminar proyecto
            print_info "Testing: DELETE /api/proyectos/$project_id - Eliminar proyecto"
            local delete_response=$(curl -s -w "HTTPSTATUS:%{http_code}" -X DELETE "$BASE_URL/api/proyectos/$project_id" 2>/dev/null)
            local delete_status=$(echo "$delete_response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
            
            if [ "$delete_status" = "200" ]; then
                print_success "DELETE /api/proyectos/$project_id - Eliminar proyecto - Status: $delete_status"
            else
                print_error "DELETE /api/proyectos/$project_id - Expected: 200, Got: $delete_status"
            fi
        fi
    else
        print_error "POST /api/proyectos - Expected: 200, Got: $create_status"
    fi
}

# ============================================================================
# TESTS DE EMPLEADOS (AUTENTICADO)
# ============================================================================
test_empleados_api() {
    print_header "TESTS DE API EMPLEADOS (AUTENTICADO)"
    
    # Credenciales de admin (admin:admin) en Base64
    local auth_token=$(echo -n "admin:admin" | base64)
    
    # Test 1: Listar empleados (requiere auth)
    test_http_endpoint "$BASE_URL/api/empleados" "200" "GET /api/empleados - Listar empleados" "$auth_token"
    
    # Test 2: Buscar empleados
    test_http_endpoint "$BASE_URL/api/empleados/buscar?termino=Juan" "200" "GET /api/empleados/buscar - Buscar empleados" "$auth_token"
    
    # Test 3: Crear empleado (POST con auth)
    print_info "Testing: POST /api/empleados - Crear empleado"
    local create_response=$(curl -s -w "HTTPSTATUS:%{http_code}" \
        -H "Content-Type: application/json" \
        -H "Authorization: Basic $auth_token" \
        -d '{
            "nombre": "Test Docker Employee",
            "cargo": "QA Tester",
            "salario": 50000,
            "email": "test.docker@demomixto.com"
        }' \
        "$BASE_URL/api/empleados" 2>/dev/null)
    
    local create_status=$(echo "$create_response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
    local create_body=$(echo "$create_response" | sed -E 's/HTTPSTATUS:[0-9]{3}$//')
    
    if [ "$create_status" = "200" ]; then
        print_success "POST /api/empleados - Crear empleado - Status: $create_status"
        
        # Extraer ID del empleado creado
        local employee_id=$(echo "$create_body" | grep -o '"id":[0-9]*' | cut -d':' -f2)
        if [ ! -z "$employee_id" ]; then
            print_info "Empleado creado con ID: $employee_id"
            
            # Test 4: Obtener empleado específico
            test_http_endpoint "$BASE_URL/api/empleados/$employee_id" "200" "GET /api/empleados/$employee_id - Obtener empleado" "$auth_token"
            
            # Test 5: Eliminar empleado
            print_info "Testing: DELETE /api/empleados/$employee_id - Eliminar empleado"
            local delete_response=$(curl -s -w "HTTPSTATUS:%{http_code}" \
                -H "Authorization: Basic $auth_token" \
                -X DELETE "$BASE_URL/api/empleados/$employee_id" 2>/dev/null)
            local delete_status=$(echo "$delete_response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
            
            if [ "$delete_status" = "200" ]; then
                print_success "DELETE /api/empleados/$employee_id - Eliminar empleado - Status: $delete_status"
            else
                print_error "DELETE /api/empleados/$employee_id - Expected: 200, Got: $delete_status"
            fi
        fi
    else
        print_error "POST /api/empleados - Expected: 200, Got: $create_status"
    fi
    
    # Test 6: Acceso sin autenticación (debe fallar)
    print_info "Testing: GET /api/empleados sin autenticación (debe fallar)"
    local unauth_response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/api/empleados" 2>/dev/null)
    local unauth_status=$(echo "$unauth_response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
    
    if [ "$unauth_status" = "401" ]; then
        print_success "GET /api/empleados sin auth - Correctamente denegado - Status: $unauth_status"
    else
        print_error "GET /api/empleados sin auth - Expected: 401, Got: $unauth_status"
    fi
}

# ============================================================================
# TESTS DE INTERFACES WEB
# ============================================================================
test_web_interfaces() {
    print_header "TESTS DE INTERFACES WEB"
    
    # Test 1: Página principal
    test_http_endpoint "$BASE_URL/" "200" "GET / - Página principal"
    
    # Test 2: Lista de proyectos (público)
    test_http_endpoint "$BASE_URL/proyectos" "200" "GET /proyectos - Lista de proyectos"
    
    # Test 3: Lista de empleados (requiere auth)
    local auth_token=$(echo -n "admin:admin" | base64)
    test_http_endpoint "$BASE_URL/empleados" "200" "GET /empleados - Lista de empleados" "$auth_token"
    
    # Test 4: Acceso a empleados sin auth (debe redirigir a login)
    print_info "Testing: GET /empleados sin autenticación (debe redirigir)"
    local unauth_response=$(curl -s -w "HTTPSTATUS:%{http_code}" "$BASE_URL/empleados" 2>/dev/null)
    local unauth_status=$(echo "$unauth_response" | tr -d '\n' | sed -E 's/.*HTTPSTATUS:([0-9]{3})$/\1/')
    
    if [[ "$unauth_status" =~ ^(401|302)$ ]]; then
        print_success "GET /empleados sin auth - Correctamente redirigido - Status: $unauth_status"
    else
        print_warning "GET /empleados sin auth - Status: $unauth_status (puede estar configurado diferente)"
    fi
}

# ============================================================================
# TESTS DE BASES DE DATOS
# ============================================================================
test_database_integration() {
    print_header "TESTS DE INTEGRACIÓN DE BASES DE DATOS"
    
    # Test 1: Verificar que MySQL está respondiendo a través de la app
    print_info "Verificando conectividad MySQL através de la aplicación..."
    local mysql_test=$(curl -s -H "Authorization: Basic $(echo -n 'admin:admin' | base64)" "$BASE_URL/api/empleados" 2>/dev/null)
    
    if echo "$mysql_test" | grep -q "success\|data\|\["; then
        print_success "MySQL está conectado y respondiendo"
    else
        print_error "MySQL no está respondiendo correctamente"
    fi
    
    # Test 2: Verificar que MongoDB está respondiendo a través de la app
    print_info "Verificando conectividad MongoDB através de la aplicación..."
    local mongo_test=$(curl -s "$BASE_URL/api/proyectos" 2>/dev/null)
    
    if echo "$mongo_test" | grep -q "success\|data\|\["; then
        print_success "MongoDB está conectado y respondiendo"
    else
        print_error "MongoDB no está respondiendo correctamente"
    fi
    
    # Test 3: Verificar health check completo
    print_info "Verificando health check completo..."
    local health_response=$(curl -s "$BASE_URL/actuator/health" 2>/dev/null)
    
    if echo "$health_response" | grep -q '"status":"UP"'; then
        print_success "Health check completo - Sistema UP"
        
        # Verificar componentes específicos
        if echo "$health_response" | grep -q '"db"'; then
            if echo "$health_response" | grep -q '"db":{"status":"UP"'; then
                print_success "MySQL health check - UP"
            else
                print_error "MySQL health check - DOWN"
            fi
        fi
        
        if echo "$health_response" | grep -q '"mongo"'; then
            if echo "$health_response" | grep -q '"mongo":{"status":"UP"'; then
                print_success "MongoDB health check - UP"
            else
                print_error "MongoDB health check - DOWN"
            fi
        fi
    else
        print_error "Health check - Sistema DOWN"
    fi
}

# ============================================================================
# FUNCIÓN PRINCIPAL
# ============================================================================
main() {
    print_header "DEMOMIXTO DOCKER - SCRIPT DE TESTING AUTOMATIZADO"
    print_info "Iniciando tests de validación..."
    print_info "Base URL: $BASE_URL"
    echo ""
    
    # Ejecutar tests
    test_connectivity
    echo ""
    test_proyectos_api
    echo ""
    test_empleados_api
    echo ""
    test_web_interfaces
    echo ""
    test_database_integration
    echo ""
    
    # Resumen final
    print_header "RESUMEN DE RESULTADOS"
    
    local success_count=0
    local error_count=0
    local warning_count=0
    
    for result in "${TEST_RESULTS[@]}"; do
        echo -e "$result"
        if [[ $result == ✅* ]]; then
            ((success_count++))
        elif [[ $result == ❌* ]]; then
            ((error_count++))
        elif [[ $result == ⚠️* ]]; then
            ((warning_count++))
        fi
    done
    
    echo ""
    print_info "Estadísticas finales:"
    echo -e "   ${GREEN}✅ Exitosos: $success_count${NC}"
    echo -e "   ${RED}❌ Errores: $error_count${NC}"
    echo -e "   ${YELLOW}⚠️  Advertencias: $warning_count${NC}"
    
    echo ""
    if [ $error_count -eq 0 ]; then
        print_success "🎉 Todos los tests críticos pasaron exitosamente!"
        print_info "🚀 DemoMixto está funcionando correctamente en Docker"
        exit 0
    else
        print_error "💥 Algunos tests fallaron - revisar configuración"
        print_info "📋 Revisar logs de contenedores: docker-compose logs"
        exit 1
    fi
}

# ============================================================================
# EJECUCIÓN
# ============================================================================
# Verificar que curl esté disponible
if ! command -v curl &> /dev/null; then
    echo -e "${RED}❌ curl no está instalado - requerido para los tests${NC}"
    exit 1
fi

# Ejecutar función principal
main "$@"