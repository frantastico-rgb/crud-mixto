package com.miAplicacion.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miAplicacion.demo.Entity.Empleado;
import com.miAplicacion.demo.Service.EmpleadoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API REST pura para empleados - Solo respuestas JSON
 * Todos los endpoints retornan JSON con estado del proceso
 */
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoRestController {

    @Autowired
    private EmpleadoService empleadoService;

    /**
     * GET /api/empleados - Listar todos los empleados
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodos(
            @RequestParam(value = "busqueda", required = false) String busqueda) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Empleado> empleados;
            
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                empleados = empleadoService.buscarEmpleados(busqueda);
                response.put("filtro", busqueda);
            } else {
                empleados = empleadoService.obtenerTodosEmpleados();
            }
            
            response.put("success", true);
            response.put("message", "Empleados obtenidos exitosamente");
            response.put("data", empleados);
            response.put("total", empleados.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener empleados: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/empleados/{id} - Obtener empleado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Empleado> empleado = empleadoService.obtenerEmpleadoPorId(id);
            
            if (empleado.isPresent()) {
                response.put("success", true);
                response.put("message", "Empleado encontrado");
                response.put("data", empleado.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                response.put("data", null);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * POST /api/empleados - Crear nuevo empleado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Empleado empleado) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar datos básicos
            if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El nombre del empleado es requerido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            if (empleado.getEmail() == null || empleado.getEmail().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El email del empleado es requerido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Empleado empleadoCreado = empleadoService.crearEmpleado(empleado);
            
            response.put("success", true);
            response.put("message", "Empleado creado exitosamente");
            response.put("data", empleadoCreado);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al crear empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al crear empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * PUT /api/empleados/{id} - Actualizar empleado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody Empleado empleado) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar que el empleado existe
            if (!empleadoService.obtenerEmpleadoPorId(id).isPresent()) {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                response.put("data", null);
                return ResponseEntity.notFound().build();
            }
            
            // Validar datos básicos
            if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El nombre del empleado es requerido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Empleado empleadoActualizado = empleadoService.actualizarEmpleado(id, empleado);
            
            if (empleadoActualizado != null) {
                response.put("success", true);
                response.put("message", "Empleado actualizado exitosamente");
                response.put("data", empleadoActualizado);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No se pudo actualizar el empleado");
                response.put("data", null);
                return ResponseEntity.internalServerError().body(response);
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al actualizar empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al actualizar empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * DELETE /api/empleados/{id} - Eliminar empleado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar que el empleado existe
            Optional<Empleado> empleadoExistente = empleadoService.obtenerEmpleadoPorId(id);
            if (!empleadoExistente.isPresent()) {
                response.put("success", false);
                response.put("message", "Empleado no encontrado con ID: " + id);
                response.put("data", null);
                return ResponseEntity.notFound().build();
            }
            
            empleadoService.eliminarEmpleado(id);
            
            response.put("success", true);
            response.put("message", "Empleado eliminado exitosamente");
            response.put("data", Map.of("id", id, "nombre", empleadoExistente.get().getNombre()));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al eliminar empleado: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/empleados/buscar?termino={termino} - Búsqueda general
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarEmpleados(
            @RequestParam("termino") String termino) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (termino == null || termino.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El término de búsqueda es requerido");
                response.put("data", null);
                response.put("total", 0);
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Empleado> empleados = empleadoService.buscarEmpleados(termino);
            
            response.put("success", true);
            response.put("message", "Búsqueda completada");
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("termino", termino);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/empleados/buscar-nombre?nombre={nombre} - Búsqueda por nombre
     */
    @GetMapping("/buscar-nombre")
    public ResponseEntity<Map<String, Object>> buscarPorNombre(
            @RequestParam("nombre") String nombre) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Empleado> empleados = empleadoService.buscarPorNombre(nombre);
            
            response.put("success", true);
            response.put("message", "Búsqueda por nombre completada");
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("criterio", Map.of("campo", "nombre", "valor", nombre));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda por nombre: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/empleados/buscar-cargo?cargo={cargo} - Búsqueda por cargo
     */
    @GetMapping("/buscar-cargo")
    public ResponseEntity<Map<String, Object>> buscarPorCargo(
            @RequestParam("cargo") String cargo) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Empleado> empleados = empleadoService.buscarPorCargo(cargo);
            
            response.put("success", true);
            response.put("message", "Búsqueda por cargo completada");
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("criterio", Map.of("campo", "cargo", "valor", cargo));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda por cargo: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/empleados/buscar-salario?min={min}&max={max} - Búsqueda por rango salarial
     */
    @GetMapping("/buscar-salario")
    public ResponseEntity<Map<String, Object>> buscarPorRangoSalarial(
            @RequestParam("min") Double salarioMin,
            @RequestParam("max") Double salarioMax) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (salarioMin < 0 || salarioMax < 0 || salarioMin > salarioMax) {
                response.put("success", false);
                response.put("message", "Rango salarial inválido. Min debe ser >= 0 y menor que Max");
                response.put("data", null);
                response.put("total", 0);
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Empleado> empleados = empleadoService.buscarPorRangoSalarial(salarioMin, salarioMax);
            
            response.put("success", true);
            response.put("message", "Búsqueda por rango salarial completada");
            response.put("data", empleados);
            response.put("total", empleados.size());
            response.put("criterio", Map.of(
                "campo", "salario",
                "min", salarioMin,
                "max", salarioMax
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda por salario: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}