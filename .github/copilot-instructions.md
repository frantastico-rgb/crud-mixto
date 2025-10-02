# AI Coding Instructions for DemoMixto Spring Boot Application

## Architecture Overview

This is a **dual-persistence Spring Boot 3.5.6 application** that demonstrates hybrid data storage patterns:

- **MySQL (JPA)**: Empleados (employees) with auto-generated Long IDs
- **MongoDB Atlas**: Proyectos (projects) with String document IDs  
- **Thymeleaf MVC**: Server-side rendered HTML with embedded CSS
- **Spring Security**: Role-based access control with in-memory users

## Critical Data Model Patterns

### Entity Persistence Strategy
```java
// MySQL entities use JPA annotations
@Entity @Data
public class Empleado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated MySQL primary key
}

// MongoDB documents use Spring Data annotations  
@Document(collection = "proyectos") @Data
public class Proyecto {
    @Id
    private String id; // MongoDB ObjectId as String
}
```

### Controller Dual-Purpose Pattern
Controllers handle **both MVC views AND REST endpoints** in the same class:
- MVC routes: `@GetMapping`, `@PostMapping` → return view names
- REST routes: `@GetMapping("/{id}")`, `@DeleteMapping` → return `ResponseEntity`

## Service Layer Update Pattern

Services implement **defensive updates** that fetch-then-modify:
```java
public Empleado actualizarEmpleado(Long id, Empleado detalles) {
    Optional<Empleado> existente = repository.findById(id);
    if (existente.isPresent()) {
        Empleado empleado = existente.get();
        // Update individual fields, preserving ID
        empleado.setNombre(detalles.getNombre());
        return repository.save(empleado);
    }
    return null; // Handle missing entities
}
```

## Security Configuration

- **Admin required**: `/empleados/**` routes (CRUD operations)
- **Public access**: `/proyectos/**` routes
- **Test credentials**: `admin:admin`, `user:password`
- **Authentication**: HTTP Basic Auth enabled, CSRF disabled for REST

## Development Workflow

### Database Setup
```bash
# MySQL local: empresa database on localhost:3306
# MongoDB Atlas: empresa database via URI in application.properties
```

### Build & Run
```bash
./mvnw spring-boot:run
# Access: http://localhost:8080/empleados or /proyectos
```

### Template Conventions
- Consistent styling with embedded CSS in `<head>`
- Navigation between modules via cross-links
- Form submissions use POST with redirect pattern
- Confirmation dialogs for delete operations: `onclick="return confirm('¿Eliminar?')"`

## Key Package Structure
- `Entity/`: Domain models with persistence annotations
- `Repository/`: Extend `JpaRepository<Entity, Long>` or `MongoRepository<Entity, String>`
- `Service/`: Business logic with defensive update patterns
- `Controller/`: Hybrid MVC/REST endpoints
- `Config/`: Security configuration with role-based access

## Common Patterns
- Lombok `@Data` for entity boilerplate
- `Optional<>` handling in services
- Thymeleaf `th:each` for list rendering
- `@ModelAttribute` for form binding
- Redirect-after-POST pattern: `return "redirect:/path"`