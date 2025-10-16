# 📊 1. ANÁLISIS Y DISEÑO - DemoMixto
## Documento de Arquitectura y Especificaciones Técnicas

---

### **📖 Índice**
1. [Requisitos Funcionales y No Funcionales](#requisitos)
2. [Diagrama de Casos de Uso](#casos-uso)
3. [Arquitectura del Sistema](#arquitectura)
4. [Modelo de Datos](#modelo-datos)
5. [Diagrama de Clases](#diagrama-clases)
6. [Patrones de Diseño](#patrones-diseño)
7. [Plan de Pruebas Inicial](#plan-pruebas)

---

## 📋 1. Requisitos Funcionales y No Funcionales {#requisitos}

### **🎯 Requisitos Funcionales**

#### **RF01 - Gestión de Empleados**
- **RF01.1:** El sistema debe permitir crear empleados con datos completos (nombre, cargo, salario, email)
- **RF01.2:** El sistema debe permitir listar todos los empleados registrados
- **RF01.3:** El sistema debe permitir buscar empleados por nombre, cargo o rango salarial
- **RF01.4:** El sistema debe permitir actualizar información de empleados existentes
- **RF01.5:** El sistema debe permitir eliminar empleados del sistema
- **RF01.6:** El sistema debe validar que el email sea único por empleado

#### **RF02 - Gestión de Proyectos**
- **RF02.1:** El sistema debe permitir crear proyectos con información básica (nombre, descripción, empleado asignado, fecha inicio)
- **RF02.2:** El sistema debe permitir listar proyectos con filtros por empleado y estado
- **RF02.3:** El sistema debe permitir marcar proyectos como completados/pendientes
- **RF02.4:** El sistema debe permitir actualizar información de proyectos
- **RF02.5:** El sistema debe permitir eliminar proyectos
- **RF02.6:** El sistema debe generar estadísticas de proyectos (totales, completados, pendientes)

#### **RF03 - Gestión de Tareas**
- **RF03.1:** El sistema debe permitir agregar tareas a proyectos existentes
- **RF03.2:** El sistema debe permitir actualizar estado de tareas (pendiente, en progreso, completo)
- **RF03.3:** El sistema debe permitir eliminar tareas de proyectos
- **RF03.4:** El sistema debe calcular estadísticas de tareas por proyecto

#### **RF04 - Interfaz Web (MVC)**
- **RF04.1:** El sistema debe proporcionar interfaz web para gestión de empleados
- **RF04.2:** El sistema debe proporcionar interfaz web para gestión de proyectos
- **RF04.3:** Las interfaces deben ser responsivas y user-friendly
- **RF04.4:** El sistema debe mostrar mensajes informativos de confirmación/error

#### **RF05 - API REST**
- **RF05.1:** El sistema debe exponer endpoints REST para todas las operaciones CRUD
- **RF05.2:** Las respuestas deben seguir formato JSON estructurado {success, message, data}
- **RF05.3:** El sistema debe manejar errores HTTP apropiados (400, 404, 500)
- **RF05.4:** El sistema debe documentar todos los endpoints disponibles

### **⚙️ Requisitos No Funcionales**

#### **RNF01 - Rendimiento**
- **RNF01.1:** El sistema debe responder a consultas simples en menos de 500ms
- **RNF01.2:** El sistema debe soportar al menos 100 usuarios concurrentes
- **RNF01.3:** Las consultas de base de datos deben estar optimizadas

#### **RNF02 - Seguridad**
- **RNF02.1:** El acceso a empleados debe requerir autenticación de administrador
- **RNF02.2:** El sistema debe usar autenticación HTTP Basic con credenciales encriptadas
- **RNF02.3:** Los proyectos deben ser de acceso público (sin autenticación)
- **RNF02.4:** El sistema debe prevenir inyección SQL y XSS

#### **RNF03 - Disponibilidad**
- **RNF03.1:** El sistema debe tener 99% de disponibilidad durante horario laboral
- **RNF03.2:** El sistema debe manejar fallos de conexión a base de datos gracefully
- **RNF03.3:** Debe existir logging adecuado para monitoreo y debugging

#### **RNF04 - Mantenibilidad**
- **RNF04.1:** El código debe seguir principios SOLID y Clean Code
- **RNF04.2:** La arquitectura debe permitir escalabilidad horizontal
- **RNF04.3:** Los componentes deben estar débilmente acoplados
- **RNF04.4:** Debe existir documentación técnica actualizada

#### **RNF05 - Portabilidad**
- **RNF05.1:** El sistema debe ejecutar en Windows, Linux y macOS
- **RNF05.2:** Debe ser compatible con Java 11+ y Spring Boot 3.x
- **RNF05.3:** Debe soportar contenedorización con Docker

---

## 🎭 2. Diagrama de Casos de Uso {#casos-uso}

### **👥 Actores del Sistema**

#### **🔐 Administrador**
- **Descripción:** Usuario con permisos completos sobre empleados
- **Permisos:** CRUD empleados, acceso a reportes, gestión completa del sistema
- **Autenticación:** Requerida (admin:admin)

#### **👤 Usuario Público**
- **Descripción:** Cualquier usuario que accede al sistema
- **Permisos:** Solo lectura y gestión de proyectos
- **Autenticación:** No requerida para proyectos

### **📋 Casos de Uso Principales**

```
┌─────────────────────────────────────────────────────────────────┐
│                    SISTEMA DEMOMIXTO                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  👤 Usuario Público          🎯 CASOS DE USO         🔐 Admin   │
│                                                                 │
│      │                                                    │     │
│      ├─── Ver Lista Proyectos                             │     │
│      ├─── Filtrar Proyectos                               │     │
│      ├─── Crear Proyecto                                  │     │
│      ├─── Editar Proyecto                                 │     │
│      ├─── Eliminar Proyecto                               │     │
│      ├─── Gestionar Tareas                                │     │
│      ├─── Ver Estadísticas                                │     │
│      │                                                    │     │
│      │                   ┌─────────────────────────────────┤     │
│      │                   │                                │     │
│      │                   │         EMPLEADOS              │     │
│      │                   │                                │     │
│      │                   │  ←──── Ver Lista Empleados ────┤     │
│      │                   │  ←──── Buscar Empleados    ────┤     │
│      │                   │  ←──── Crear Empleado      ────┤     │
│      │                   │  ←──── Actualizar Empleado ────┤     │
│      │                   │  ←──── Eliminar Empleado   ────┤     │
│      │                   │  ←──── Reportes Empleados  ────┤     │
│      │                   │                                │     │
│      │                   └─────────────────────────────────│     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### **📋 Especificación de Casos de Uso**

#### **CU01: Gestionar Empleados**
- **Actor:** Administrador
- **Precondición:** Usuario autenticado como admin
- **Flujo Principal:**
  1. Admin accede a /empleados
  2. Sistema muestra lista de empleados
  3. Admin selecciona operación (crear/editar/eliminar/buscar)
  4. Sistema ejecuta operación y confirma resultado
- **Postcondición:** Cambios reflejados en MySQL

#### **CU02: Gestionar Proyectos**
- **Actor:** Usuario Público
- **Precondición:** Ninguna
- **Flujo Principal:**
  1. Usuario accede a /proyectos o /api/proyectos
  2. Sistema muestra proyectos disponibles
  3. Usuario realiza operación CRUD
  4. Sistema actualiza MongoDB y confirma
- **Postcondición:** Cambios reflejados en MongoDB

---

## 🏗️ 3. Arquitectura del Sistema {#arquitectura}

### **📐 Arquitectura en Capas**

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                        │
├─────────────────────┬───────────────────────────────────────────┤
│   🌐 Web MVC        │           📱 REST API                    │
│                     │                                           │
│ • empleados.html    │     /api/empleados/**                    │
│ • proyectos.html    │     /api/proyectos/**                    │  
│ • Thymeleaf         │     JSON Responses                       │
│ • Bootstrap CSS     │     Thunder Client Testing               │
├─────────────────────┴───────────────────────────────────────────┤
│                    CAPA DE CONTROLADORES                       │
├─────────────────────────────────────────────────────────────────┤
│ EmpleadoController.java    │    EmpleadoRestController.java     │
│ ProyectoController.java    │    ProyectoRestController.java     │
│ HomeController.java        │                                    │
├─────────────────────────────────────────────────────────────────┤
│                     CAPA DE NEGOCIO                            │
├─────────────────────────────────────────────────────────────────┤
│ EmpleadoService.java       │    ProyectoService.java            │
│ • Lógica de negocio        │    • Validaciones                  │
│ • Validaciones             │    • Cálculo estadísticas          │
│ • Transformaciones         │    • Gestión tareas                │
├─────────────────────────────────────────────────────────────────┤
│                   CAPA DE PERSISTENCIA                         │
├──────────────────────────┬──────────────────────────────────────┤
│     📊 MySQL             │         🍃 MongoDB Atlas             │
│                          │                                      │
│ EmpleadoRepository       │    ProyectoRepository                │
│ JpaRepository<Long>      │    MongoRepository<String>           │
│                          │                                      │
│ 🗃️ Empleado Entity       │    📄 Proyecto Document             │
│ • ID Auto-generado       │    • ID ObjectId                    │
│ • Datos estructurados    │    • Tareas embebidas               │
└──────────────────────────┴──────────────────────────────────────┘
```

### **🔧 Componentes Transversales**

```
┌─────────────────────────────────────────────────────────────────┐
│                 COMPONENTES TRANSVERSALES                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  🔐 SecurityConfig.java                                         │
│  ├── HTTP Basic Authentication                                  │
│  ├── Role-based Authorization (ADMIN/USER)                     │
│  ├── CSRF Disabled for APIs                                    │
│  └── Public/Private Route Configuration                        │
│                                                                 │
│  ⚙️ Application Configuration                                  │
│  ├── application.properties (DB connections)                   │
│  ├── Maven Dependencies (pom.xml)                              │
│  └── Spring Boot Auto-configuration                            │
│                                                                 │
│  📊 Data Configuration                                         │
│  ├── MySQL Connection (localhost:3306/empresa)                 │
│  ├── MongoDB Atlas Connection (cloud cluster)                  │
│  └── Dual Repository Configuration                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🗄️ 4. Modelo de Datos {#modelo-datos}

### **📊 Diagrama Entidad-Relación (MySQL)**

```sql
┌─────────────────────────────────────────┐
│                EMPLEADO                 │
├─────────────────────────────────────────┤
│ 🔑 id: BIGINT AUTO_INCREMENT PK        │
│    nombre: VARCHAR(255) NOT NULL       │
│    cargo: VARCHAR(255)                 │
│    salario: DECIMAL(10,2)              │
│    email: VARCHAR(255) UNIQUE          │
├─────────────────────────────────────────┤
│ Índices:                                │
│ • PRIMARY KEY (id)                      │
│ • UNIQUE INDEX (email)                  │
│ • INDEX (cargo)                         │
└─────────────────────────────────────────┘
```

### **🍃 Modelo de Documentos (MongoDB)**

```javascript
// Colección: proyectos
{
  "_id": ObjectId("68d74c896283a9a744f23415"),
  "nombre": "API REST",
  "descripcion": "Desarrollo de API REST para servicios especiales",
  "empleadoId": 1,                    // Referencia a MySQL Empleado.id
  "fechaInicio": "2025-08-27",
  "completado": false,
  "tareas": [                         // Array embebido
    {
      "titulo": "Implementar endpoints",
      "estado": "pendiente"           // pendiente|en-progreso|completo
    },
    {
      "titulo": "Documentar API",
      "estado": "completo"
    }
  ]
}
```

### **🔗 Relaciones Entre Sistemas**

```
MySQL (Empleados)                MongoDB (Proyectos)
┌─────────────────┐             ┌─────────────────────┐
│ Empleado        │             │ Proyecto            │
│ ├─ id: 1        │ ◄─────────── │ ├─ empleadoId: 1    │
│ ├─ nombre       │   FK Ref     │ ├─ nombre           │
│ ├─ cargo        │             │ ├─ descripcion      │
│ └─ ...          │             │ └─ tareas: [...]    │
└─────────────────┘             └─────────────────────┘

Relación: 1 Empleado → N Proyectos (Foreign Key Reference)
Tipo: Loose Coupling entre bases de datos
```

---

## 📐 5. Diagrama de Clases {#diagrama-clases}

### **🏗️ Estructura de Clases Java**

```java
┌─────────────────────────────────────────────────────────────────┐
│                        ENTITIES                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  📊 Empleado.java                    🍃 Proyecto.java           │
│  ├─ @Entity                         ├─ @Document               │
│  ├─ @Data (Lombok)                  ├─ @Data (Lombok)          │
│  ├─ Long id                         ├─ String id               │
│  ├─ String nombre                   ├─ String nombre           │
│  ├─ String cargo                    ├─ String descripcion     │
│  ├─ BigDecimal salario              ├─ Long empleadoId         │
│  └─ String email                    ├─ LocalDate fechaInicio   │
│                                     ├─ Boolean completado      │
│          📝 Tarea.java              └─ List<Tarea> tareas      │
│          ├─ String titulo                                      │
│          └─ String estado                                      │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                      REPOSITORIES                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  EmpleadoRepository                  ProyectoRepository         │
│  extends JpaRepository<Empleado,Long> extends MongoRepository   │
│  ├─ findByNombreContaining()        ├─ findByEmpleadoId()      │
│  ├─ findByCargoContaining()         ├─ findByCompletado()      │
│  └─ findBySalarioBetween()          └─ findByNombreContaining() │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                       SERVICES                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  📊 EmpleadoService.java            🍃 ProyectoService.java     │
│  ├─ @Service                        ├─ @Service                │
│  ├─ listarEmpleados()               ├─ listarProyectos()       │
│  ├─ crearEmpleado()                 ├─ crearProyecto()         │
│  ├─ actualizarEmpleado()            ├─ actualizarProyecto()    │
│  ├─ eliminarEmpleado()              ├─ eliminarProyecto()      │
│  ├─ buscarPorNombre()               ├─ agregarTarea()          │
│  ├─ buscarPorCargo()                ├─ actualizarTarea()       │
│  └─ buscarPorSalario()              └─ generarEstadisticas()   │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                     CONTROLLERS                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  🌐 MVC Controllers                 📱 REST Controllers         │
│  ────────────────────               ──────────────────         │
│  EmpleadoController                 EmpleadoRestController     │
│  ├─ @Controller                     ├─ @RestController          │
│  ├─ @RequestMapping("/empleados")   ├─ @RequestMapping("/api") │
│  ├─ listar() → view                 ├─ listar() → JSON         │
│  ├─ crear() → view                  ├─ crear() → JSON          │
│  └─ actualizar() → redirect         └─ actualizar() → JSON     │
│                                                                 │
│  ProyectoController                 ProyectoRestController     │
│  ├─ @Controller                     ├─ @RestController          │
│  ├─ @RequestMapping("/proyectos")   ├─ @RequestMapping("/api") │
│  └─ Similar structure...            └─ Similar structure...    │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                    CONFIGURATION                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  🔐 SecurityConfig.java                                         │
│  ├─ @Configuration                                              │
│  ├─ @EnableWebSecurity                                          │
│  ├─ passwordEncoder(): BCryptPasswordEncoder                   │
│  ├─ userDetailsService(): InMemoryUserDetailsManager           │
│  └─ securityFilterChain(): HttpSecurity rules                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎨 6. Patrones de Diseño Implementados {#patrones-diseño}

### **🏗️ Arquitectural Patterns**

#### **1. 📐 MVC (Model-View-Controller)**
```java
// Separación clara de responsabilidades
Model:      Empleado.java, Proyecto.java (Entities)
View:       empleados.html, proyectos.html (Thymeleaf)
Controller: EmpleadoController.java, ProyectoController.java
```

#### **2. 🏢 Layered Architecture**
```
Presentation → Business → Persistence
Controllers → Services → Repositories
```

#### **3. 🎯 Repository Pattern**
```java
// Abstracción de acceso a datos
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByNombreContaining(String nombre);
}
```

### **🔧 Creational Patterns**

#### **4. 🏭 Dependency Injection (IoC)**
```java
@Service
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;  // DI por Spring
}
```

#### **5. 🏗️ Builder Pattern (via Lombok)**
```java
@Data  // Genera builder automáticamente
public class Empleado {
    // Permite: Empleado.builder().nombre("Juan").cargo("Dev").build()
}
```

### **🔄 Behavioral Patterns**

#### **6. 📋 Strategy Pattern**
```java
// Diferentes estrategias de persistencia
MySQL Strategy:    JpaRepository<Empleado, Long>
MongoDB Strategy:  MongoRepository<Proyecto, String>
```

#### **7. 🎯 Template Method (Spring)**
```java
// Spring Boot auto-configuration templates
// JpaRepository template methods: save(), findAll(), findById()
```

### **🔧 Structural Patterns**

#### **8. 🔌 Adapter Pattern**
```java
// Adaptadores entre capas
DTO ↔ Entity adapters
HTTP Request ↔ Service method adapters
```

#### **9. 🎭 Facade Pattern**
```java
@RestController
public class EmpleadoRestController {
    // Facade que simplifica múltiples operaciones de servicio
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarEmpleados() {
        // Unifica múltiples llamadas de servicio
    }
}
```

### **🛡️ Security Patterns**

#### **10. 🔐 Authentication/Authorization Pattern**
```java
@Configuration
public class SecurityConfig {
    // Implementa patrones de seguridad estándar
    // Basic Auth + Role-based access control
}
```

---

## 🧪 7. Plan de Pruebas Inicial {#plan-pruebas}

### **📋 Estrategia de Pruebas**

#### **🎯 Objetivos de Testing**
1. **Funcionalidad:** Verificar que todos los CRUDs funcionen correctamente
2. **Integración:** Confirmar comunicación entre capas y bases de datos
3. **Seguridad:** Validar autenticación y autorización
4. **Performance:** Medir tiempos de respuesta de APIs
5. **Usabilidad:** Probar interfaces web y APIs

### **🌩️ Plan de Pruebas con Thunder Client**

#### **Fase 1: Pruebas de Conectividad**
```
✅ TC001: Verificar servidor activo
   GET http://localhost:8080/api/proyectos
   Expected: 200 OK + JSON response

✅ TC002: Verificar autenticación
   GET http://localhost:8080/api/empleados
   Expected: 401 Unauthorized (sin auth)
   
✅ TC003: Verificar autenticación válida
   GET http://localhost:8080/api/empleados
   Auth: Basic admin:admin
   Expected: 200 OK + lista empleados
```

#### **Fase 2: Pruebas CRUD Empleados**
```
🔐 TC004: Crear empleado
   POST http://localhost:8080/api/empleados
   Auth: Basic admin:admin
   Body: {
     "nombre": "Test Thunder",
     "cargo": "QA Tester", 
     "salario": 45000,
     "email": "test@thunder.com"
   }
   Expected: 200 OK + empleado creado
   Validation: Capturar ID para siguientes tests

🔐 TC005: Obtener empleado por ID
   GET http://localhost:8080/api/empleados/{{empleadoId}}
   Auth: Basic admin:admin
   Expected: 200 OK + datos del empleado

🔐 TC006: Actualizar empleado
   PUT http://localhost:8080/api/empleados/{{empleadoId}}
   Auth: Basic admin:admin
   Body: {
     "nombre": "Test Thunder Updated",
     "salario": 55000
   }
   Expected: 200 OK + empleado actualizado

🔐 TC007: Buscar empleados
   GET http://localhost:8080/api/empleados/buscar?termino=Thunder
   Auth: Basic admin:admin
   Expected: 200 OK + empleados encontrados

🔐 TC008: Eliminar empleado
   DELETE http://localhost:8080/api/empleados/{{empleadoId}}
   Auth: Basic admin:admin
   Expected: 200 OK + confirmación eliminación
```

#### **Fase 3: Pruebas CRUD Proyectos**
```
📋 TC009: Listar proyectos
   GET http://localhost:8080/api/proyectos
   Expected: 200 OK + lista proyectos

📋 TC010: Crear proyecto
   POST http://localhost:8080/api/proyectos
   Body: {
     "nombre": "Proyecto Thunder Test",
     "descripcion": "Testing con Thunder Client",
     "empleadoId": 1,
     "fechaInicio": "2025-01-15",
     "completado": false
   }
   Expected: 200 OK + proyecto creado
   Validation: Capturar ID proyecto

📋 TC011: Agregar tarea a proyecto
   POST http://localhost:8080/api/proyectos/{{proyectoId}}/tareas
   Body: {
     "titulo": "Ejecutar plan de pruebas",
     "estado": "pendiente"
   }
   Expected: 200 OK + tarea agregada

📋 TC012: Actualizar tarea
   PUT http://localhost:8080/api/proyectos/{{proyectoId}}/tareas/0
   Body: {
     "titulo": "Ejecutar plan de pruebas - Completado",
     "estado": "completo"
   }
   Expected: 200 OK + tarea actualizada

📋 TC013: Obtener estadísticas
   GET http://localhost:8080/api/proyectos/estadisticas
   Expected: 200 OK + estadísticas actualizadas

📋 TC014: Eliminar proyecto
   DELETE http://localhost:8080/api/proyectos/{{proyectoId}}
   Expected: 200 OK + proyecto eliminado
```

#### **Fase 4: Pruebas de Error Handling**
```
❌ TC015: Empleado inexistente
   GET http://localhost:8080/api/empleados/99999
   Auth: Basic admin:admin
   Expected: 404 Not Found

❌ TC016: Datos inválidos
   POST http://localhost:8080/api/empleados
   Auth: Basic admin:admin
   Body: { "cargo": "Sin nombre" }
   Expected: 400 Bad Request

❌ TC017: Sin autenticación requerida
   POST http://localhost:8080/api/empleados
   Body: { valid data }
   Expected: 401 Unauthorized
```

### **📊 Criterios de Aceptación**

#### **✅ Tests Must Pass:**
- ✅ **Conectividad:** TC001-TC003 (100% éxito)
- ✅ **CRUD Empleados:** TC004-TC008 (100% éxito) 
- ✅ **CRUD Proyectos:** TC009-TC014 (100% éxito)
- ✅ **Error Handling:** TC015-TC017 (comportamiento esperado)

#### **⏱️ Performance Targets:**
- **API Response Time:** < 500ms para operaciones simples
- **Database Queries:** < 200ms para consultas básicas
- **Concurrent Users:** Soportar 10+ requests simultáneos

#### **🔍 Validation Checklist:**
- [ ] JSON format válido en todas las responses
- [ ] Status codes HTTP correctos
- [ ] Estructura {success, message, data} consistente
- [ ] Autenticación funcionando según especificación
- [ ] Datos persistiendo correctamente en ambas bases de datos
- [ ] Manejo de errores apropiado

---

## 📋 Resumen del Documento

### **🎯 Deliverables Completados:**
1. ✅ **Requisitos Funcionales y No Funcionales** definidos
2. ✅ **Casos de Uso** con actores y flujos principales
3. ✅ **Arquitectura en Capas** documentada
4. ✅ **Modelo de Datos** dual (MySQL + MongoDB)
5. ✅ **Diagrama de Clases** completo
6. ✅ **Patrones de Diseño** identificados
7. ✅ **Plan de Pruebas** con Thunder Client

### **🚀 Próximos Pasos:**
- Ejecutar plan de pruebas completo
- Documentar resultados de testing
- Refinar casos de uso basado en pruebas
- Preparar documentación para Paso 2: DESARROLLO

---

**📅 Fecha:** 16 de Octubre 2025  
**👨‍💻 Proyecto:** DemoMixto - Sistema de Gestión Híbrida  
**📋 Versión:** 1.0 - Análisis y Diseño Inicial