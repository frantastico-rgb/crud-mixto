## 🛠️ **GUÍA DE DESARROLLO - DemoMixto**

### 🎯 **Para Desarrolladores y Teams de Integración**

Esta guía está dirigida a developers que necesiten **integrar con DemoMixto**, extender funcionalidades o contribuir al proyecto. Incluye arquitectura, patrones de código y mejores prácticas.

---

### 🏗️ **ARQUITECTURA DE DESARROLLO**

#### **1. Stack Tecnológico**

```
📚 BACKEND STACK:
├── ☕ Spring Boot 3.5.6 (Framework principal)
├── 🔐 Spring Security (Autenticación & Autorización)
├── 📊 Spring Data JPA (MySQL ORM)
├── 🍃 Spring Data MongoDB (NoSQL ODM)
├── 🖼️ Thymeleaf (Server-side templates)
├── 📝 Lombok (Boilerplate reduction)
├── 📋 Swagger/OpenAPI 3 (API Documentation)
└── 🧪 JUnit 5 + Mockito (Testing)

🗄️ DATABASE STACK:
├── 🐬 MySQL 8.0+ (Empleados - Datos relacionales)
└── 🍃 MongoDB Atlas (Proyectos - Datos documentales)

🔧 TOOLS & CI/CD:
├── ⚡ Maven (Build management)
├── 🐳 Docker (Containerización)
├── 🚀 GitHub Actions (CI/CD)
├── 🎯 SonarQube (Code quality)
└── 📊 Codecov (Test coverage)
```

#### **2. Patrón de Arquitectura**

```
┌─────────────────────────────────────────────────┐
│                PRESENTATION                      │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │  @Controller    │  │    @RestController      ││
│  │  (Thymeleaf)    │  │    (JSON API)           ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────┐
│                 BUSINESS                         │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │   @Service      │  │     @Service            ││
│  │ (Business Logic)│  │  (Business Logic)       ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────┐
│               PERSISTENCE                        │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │ @Repository     │  │   @Repository           ││
│  │ JpaRepository   │  │ MongoRepository         ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────┐
│                DATABASE                          │
│  ┌─────────────────┐  ┌─────────────────────────┐│
│  │     MySQL       │  │     MongoDB             ││
│  │   (ACID)        │  │   (Document)            ││
│  └─────────────────┘  └─────────────────────────┘│
└─────────────────────────────────────────────────┘
```

---

### 💻 **SETUP DEL ENTORNO DE DESARROLLO**

#### **1. Clone y Setup Inicial**

```bash
# 1. Clonar repositorio
git clone https://github.com/your-repo/demomixto.git
cd demomixto

# 2. Crear archivo de configuración local
cp src/main/resources/application.properties.example src/main/resources/application-dev.properties

# 3. Configurar bases de datos
# MySQL local
docker run --name mysql-dev -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=empresa -p 3306:3306 -d mysql:8.0

# MongoDB Atlas - obtener URI desde MongoDB Atlas console
# mongodb+srv://username:password@cluster.mongodb.net/empresa

# 4. Ejecutar aplicación
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### **2. IDE Configuration (IntelliJ IDEA)**

```xml
<!-- .idea/misc.xml -->
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="JDK_17" project-jdk-name="17" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target" />
  </component>
</project>

<!-- Plugins recomendados: -->
<!-- - Lombok Plugin -->
<!-- - Spring Boot Plugin -->  
<!-- - Docker Plugin -->
<!-- - MongoDB Plugin -->
```

#### **3. VS Code Configuration**

```json
// .vscode/settings.json
{
  "java.home": "/path/to/jdk-17",
  "java.configuration.updateBuildConfiguration": "automatic",
  "spring-boot.ls.problem.application-properties.unknown-property": "ignore",
  "files.exclude": {
    "target/": true,
    ".mvn/": true
  }
}

// .vscode/extensions.json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "pivotal.vscode-spring-boot",
    "ms-vscode.vscode-docker"
  ]
}
```

---

### 🔧 **PATRONES DE DESARROLLO**

#### **1. Entity Pattern - Dual Persistence**

```java
// MYSQL ENTITY PATTERN
@Entity
@Data
@Schema(description = "MySQL entity with auto-generated ID")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated Long ID

    @Column(unique = true)
    @Schema(description = "Unique email constraint")
    private String email;
    
    // Getters/setters para compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}

// MONGODB DOCUMENT PATTERN  
@Document(collection = "proyectos")
@Data
@Schema(description = "MongoDB document with String ID")
public class Proyecto {
    @Id
    private String id; // MongoDB ObjectId as String

    private String empleadoId; // String reference to Empleado.id
    
    @Schema(description = "Embedded tasks array")
    private List<Tarea> tareas; // Embedded documents
}
```

#### **2. Service Layer Pattern**

```java
@Service
@Transactional
public class EmpleadoService {
    
    @Autowired
    private EmpleadoRepository repository;
    
    // ✅ DEFENSIVE UPDATE PATTERN
    public Empleado actualizarEmpleado(Long id, Empleado detalles) {
        Optional<Empleado> existente = repository.findById(id);
        if (existente.isPresent()) {
            Empleado empleado = existente.get();
            // Update individual fields, preserving ID
            empleado.setNombre(detalles.getNombre());
            empleado.setCargo(detalles.getCargo());
            empleado.setSalario(detalles.getSalario());
            empleado.setEmail(detalles.getEmail());
            return repository.save(empleado);
        }
        return null; // Or throw exception
    }
    
    // ✅ SEARCH PATTERN
    public List<Empleado> buscarEmpleados(String termino) {
        return repository.findByNombreContainingIgnoreCaseOrCargoContainingIgnoreCase(
            termino, termino
        );
    }
}
```

#### **3. Controller Pattern - Hybrid MVC/REST**

```java
@Controller
@RequestMapping("/empleados")
@Tag(name = "Empleados", description = "Employee management API")
public class EmpleadoController {
    
    // ✅ MVC ENDPOINT PATTERN
    @GetMapping
    public String obtenerTodos(Model model) {
        model.addAttribute("empleados", empleadoService.obtenerTodosEmpleados());
        return "empleados-lista"; // Return view name
    }
    
    // ✅ REST ENDPOINT PATTERN
    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<Empleado> obtenerPorId(@PathVariable Long id) {
        return empleadoService.obtenerEmpleadoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ FORM SUBMISSION PATTERN
    @PostMapping
    public String crear(@ModelAttribute Empleado empleado) {
        empleadoService.crearEmpleado(empleado);
        return "redirect:/empleados"; // PRG Pattern
    }
}
```

#### **4. Repository Pattern**

```java
// JPA Repository for MySQL
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    // Query methods by convention
    List<Empleado> findByCargo(String cargo);
    List<Empleado> findBySalarioBetween(BigDecimal min, BigDecimal max);
    
    // Custom queries
    @Query("SELECT e FROM Empleado e WHERE e.nombre LIKE %:termino% OR e.cargo LIKE %:termino%")
    List<Empleado> buscarPorTermino(@Param("termino") String termino);
    
    // Exists queries for validation
    boolean existsByEmail(String email);
}

// MongoDB Repository  
@Repository
public interface ProyectoRepository extends MongoRepository<Proyecto, String> {
    
    // Query methods for MongoDB
    List<Proyecto> findByEmpleadoId(String empleadoId);
    List<Proyecto> findByCompletado(boolean completado);
    
    // Date range queries
    List<Proyecto> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    
    // Embedded document queries
    @Query("{'tareas.estado': ?0}")
    List<Proyecto> findByTareaEstado(String estado);
}
```

---

### 🧪 **TESTING STRATEGIES**

#### **1. Test Architecture**

```
📁 src/test/java/
├── 🧪 Unit Tests
│   ├── Service Tests (Business Logic)
│   ├── Repository Tests (Data Layer)
│   └── Utility Tests (Helper Classes)
├── 🔗 Integration Tests  
│   ├── Controller Tests (Web Layer)
│   ├── Database Tests (Full Stack)
│   └── Security Tests (Auth Flow)
└── 🎯 End-to-End Tests
    ├── API Tests (REST Endpoints)
    └── UI Tests (Thymeleaf Views)
```

#### **2. Unit Test Pattern**

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("EmpleadoService Tests")
class EmpleadoServiceTest {
    
    @Mock
    private EmpleadoRepository repository;
    
    @InjectMocks
    private EmpleadoService service;
    
    @Test
    @DisplayName("Should create employee successfully")
    void crearEmpleado_DeberiaRetornarEmpleadoGuardado() {
        // Given
        Empleado empleado = createTestEmpleado();
        when(repository.save(any(Empleado.class))).thenReturn(empleado);
        
        // When
        Empleado resultado = service.crearEmpleado(empleado);
        
        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Test User");
        verify(repository, times(1)).save(empleado);
    }
}
```

#### **3. Integration Test Pattern**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
class EmpleadoControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private EmpleadoRepository repository;
    
    @Test
    @Order(1)
    void crearEmpleado_DeberiaRetornar201() {
        // Given
        Empleado empleado = createTestEmpleado();
        HttpEntity<Empleado> request = new HttpEntity<>(empleado, createAuthHeaders());
        
        // When
        ResponseEntity<Empleado> response = restTemplate.postForEntity(
            "/api/empleados", request, Empleado.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
    }
    
    private HttpHeaders createAuthHeaders() {
        String auth = "admin:admin";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }
}
```

---

### 🔄 **API INTEGRATION GUIDE**

#### **1. Authentication Pattern**

```javascript
// JavaScript Client Example
const apiClient = {
    baseURL: 'http://localhost:8080/api',
    
    // Basic Auth setup
    getAuthHeaders() {
        const credentials = btoa('admin:admin');
        return {
            'Authorization': `Basic ${credentials}`,
            'Content-Type': 'application/json'
        };
    },
    
    // GET request example
    async getEmpleados() {
        const response = await fetch(`${this.baseURL}/empleados`, {
            method: 'GET',
            headers: this.getAuthHeaders()
        });
        return response.json();
    },
    
    // POST request example
    async createEmpleado(empleado) {
        const response = await fetch(`${this.baseURL}/empleados`, {
            method: 'POST',
            headers: this.getAuthHeaders(),
            body: JSON.stringify(empleado)
        });
        return response.json();
    }
};
```

#### **2. Python Client Example**

```python
import requests
from requests.auth import HTTPBasicAuth

class DemoMixtoClient:
    def __init__(self, base_url="http://localhost:8080/api"):
        self.base_url = base_url
        self.auth = HTTPBasicAuth('admin', 'admin')
    
    def get_empleados(self):
        """Get all employees"""
        response = requests.get(f"{self.base_url}/empleados", auth=self.auth)
        response.raise_for_status()
        return response.json()
    
    def create_empleado(self, empleado_data):
        """Create new employee"""
        response = requests.post(
            f"{self.base_url}/empleados",
            json=empleado_data,
            auth=self.auth
        )
        response.raise_for_status()
        return response.json()
    
    def search_empleados(self, termino):
        """Search employees by term"""
        response = requests.get(
            f"{self.base_url}/empleados/buscar",
            params={'termino': termino},
            auth=self.auth
        )
        response.raise_for_status()
        return response.json()

# Usage example
client = DemoMixtoClient()
empleados = client.get_empleados()
print(f"Total empleados: {len(empleados)}")
```

#### **3. cURL Examples**

```bash
# GET all employees
curl -u admin:admin \
  -H "Content-Type: application/json" \
  http://localhost:8080/api/empleados

# CREATE employee
curl -u admin:admin \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan API Test",
    "cargo": "Developer",
    "salario": 50000,
    "email": "juan.api@test.com"
  }' \
  http://localhost:8080/api/empleados

# SEARCH employees
curl -u admin:admin \
  "http://localhost:8080/api/empleados/buscar?termino=developer"

# GET projects (no auth required)
curl -H "Content-Type: application/json" \
  http://localhost:8080/api/proyectos
```

---

### 🚀 **DEPLOYMENT PARA DEVELOPERS**

#### **1. Local Development**

```bash
# Profile-based development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Hot reload with DevTools
# Automatic restart on classpath changes
# LiveReload browser extension support
```

#### **2. Testing Environments**

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=EmpleadoServiceTest

# Run with coverage
./mvnw test jacoco:report

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

#### **3. Docker Development**

```bash
# Build image
docker build -t demomixto:dev .

# Run with Docker Compose
docker-compose -f docker-compose.dev.yml up

# Development with volume mounting
docker run -v $(pwd):/app -p 8080:8080 demomixto:dev
```

---

### 📋 **CONTRIBUTING GUIDELINES**

#### **1. Code Style**

```java
// ✅ GOOD: Follow Java naming conventions
@Service
public class EmpleadoService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    public Optional<Empleado> buscarPorId(Long id) {
        logger.debug("Buscando empleado con ID: {}", id);
        return empleadoRepository.findById(id);
    }
}

// ❌ BAD: Avoid poor naming and missing docs
@Service
public class ES {
    @Autowired
    private EmpleadoRepository repo;
    
    public Optional<Empleado> find(Long x) {
        return repo.findById(x);
    }
}
```

#### **2. Git Workflow**

```bash
# Feature branch workflow
git checkout -b feature/nueva-funcionalidad
git add .
git commit -m "feat: agregar búsqueda avanzada de empleados"
git push origin feature/nueva-funcionalidad

# Create Pull Request
# Run CI/CD pipeline
# Code review
# Merge to main
```

#### **3. Commit Message Convention**

```
feat: nueva funcionalidad
fix: corrección de bug
docs: actualización de documentación
style: cambios de formato
refactor: refactoring de código
test: agregar tests
chore: tareas de mantenimiento

Examples:
feat: agregar endpoint de estadísticas de empleados
fix: corregir validación de email duplicado
docs: actualizar guía de API integration
test: agregar tests unitarios para ProyectoService
```

---

### 🔧 **EXTENSIBILITY PATTERNS**

#### **1. Adding New Entities**

```java
// 1. Create Entity
@Entity
@Data
@Schema(description = "Nueva entidad del sistema")
public class NuevaEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Fields with Swagger annotations
    @Schema(description = "Campo descripción", example = "Ejemplo")
    private String campo;
}

// 2. Create Repository
@Repository
public interface NuevaEntidadRepository extends JpaRepository<NuevaEntidad, Long> {
    List<NuevaEntidad> findByCampo(String campo);
}

// 3. Create Service
@Service
@Transactional
public class NuevaEntidadService {
    // Business logic here
}

// 4. Create Controller
@Controller
@RequestMapping("/nueva-entidad")
@Tag(name = "Nueva Entidad", description = "API para nueva entidad")
public class NuevaEntidadController {
    // MVC + REST endpoints
}
```

#### **2. Adding Custom Queries**

```java
// JPA Custom Query
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    @Query("SELECT e FROM Empleado e WHERE e.salario > :salario AND e.cargo = :cargo")
    List<Empleado> findHighSalaryByCargo(@Param("salario") BigDecimal salario, 
                                       @Param("cargo") String cargo);
    
    @Query(value = "SELECT * FROM empleado WHERE YEAR(fecha_contratacion) = :year", 
           nativeQuery = true)
    List<Empleado> findByHireYear(@Param("year") int year);
}

// MongoDB Custom Query
@Repository
public interface ProyectoRepository extends MongoRepository<Proyecto, String> {
    
    @Query("{'empleadoId': ?0, 'completado': false}")
    List<Proyecto> findActiveProjectsByEmpleado(String empleadoId);
    
    @Aggregation(pipeline = {
        "{ '$match': { 'completado': true } }",
        "{ '$group': { '_id': '$empleadoId', 'count': { '$sum': 1 } } }"
    })
    List<ProyectoStats> getCompletedProjectsByEmployee();
}
```

---

### 📚 **RECURSOS Y REFERENCIAS**

#### **1. Documentación Oficial**

- 📖 **Spring Boot**: https://spring.io/projects/spring-boot
- 🔒 **Spring Security**: https://spring.io/projects/spring-security
- 📊 **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- 🍃 **Spring Data MongoDB**: https://spring.io/projects/spring-data-mongodb
- 📋 **OpenAPI 3**: https://swagger.io/specification/

#### **2. Tools & Plugins**

- 🔧 **Lombok**: https://projectlombok.org/
- 🧪 **JUnit 5**: https://junit.org/junit5/
- 🎭 **Mockito**: https://mockito.org/
- 🐳 **Docker**: https://docs.docker.com/
- 📊 **SonarQube**: https://www.sonarqube.org/

#### **3. Best Practices**

- 📋 **12 Factor App**: https://12factor.net/
- 🏗️ **Clean Architecture**: https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
- 🧪 **Test Pyramid**: https://martinfowler.com/articles/practical-test-pyramid.html
- 🔄 **API Design**: https://restfulapi.net/

---

### 💬 **COMUNIDAD Y SOPORTE**

- 📧 **Email**: dev-team@demomixto.com
- 💬 **Slack**: #demomixto-dev
- 🐛 **Issues**: https://github.com/your-repo/demomixto/issues
- 📖 **Wiki**: https://github.com/your-repo/demomixto/wiki
- 🎥 **Video Tutorials**: [YouTube Channel]

---

**¡Happy Coding! 🚀**

**© 2025 DemoMixto Development Team**