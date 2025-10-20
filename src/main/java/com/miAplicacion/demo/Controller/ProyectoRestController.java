package com.miAplicacion.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Entity.Tarea;
import com.miAplicacion.demo.Service.ProyectoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API REST pura para proyectos - Solo respuestas JSON
 * Todos los endpoints retornan JSON con estado del proceso
 */
@RestController
@RequestMapping("/api/proyectos")
public class ProyectoRestController {

    @Autowired
    private ProyectoService proyectoService;

    /**
     * GET /api/proyectos - Listar todos los proyectos con filtros opcionales
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodos(
            @RequestParam(value = "empleadoId", required = false) Long empleadoId,
            @RequestParam(value = "completado", required = false) Boolean completado,
            @RequestParam(value = "busqueda", required = false) String busqueda) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Proyecto> proyectos;
            Map<String, Object> filtros = new HashMap<>();
            
            // Aplicar filtros según parámetros
            if (empleadoId != null && completado != null) {
                proyectos = proyectoService.obtenerProyectosEmpleadoPorEstado(empleadoId, completado);
                filtros.put("empleadoId", empleadoId);
                filtros.put("completado", completado);
            } else if (empleadoId != null) {
                proyectos = proyectoService.obtenerProyectosPorEmpleado(empleadoId);
                filtros.put("empleadoId", empleadoId);
            } else if (completado != null) {
                proyectos = proyectoService.obtenerProyectosPorEstado(completado);
                filtros.put("completado", completado);
            } else if (busqueda != null && !busqueda.trim().isEmpty()) {
                proyectos = proyectoService.buscarProyectosPorNombre(busqueda);
                filtros.put("busqueda", busqueda);
            } else {
                proyectos = proyectoService.obtenerTodosProyectos();
            }
            
            response.put("success", true);
            response.put("message", "Proyectos obtenidos exitosamente");
            response.put("data", proyectos);
            response.put("total", proyectos.size());
            
            if (!filtros.isEmpty()) {
                response.put("filtros", filtros);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener proyectos: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/proyectos/{id} - Obtener proyecto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Proyecto> proyecto = proyectoService.obtenerProyectoPorId(id);
            
            if (proyecto.isPresent()) {
                response.put("success", true);
                response.put("message", "Proyecto encontrado");
                response.put("data", proyecto.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                response.put("data", null);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * POST /api/proyectos - Crear nuevo proyecto
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Proyecto proyecto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar datos básicos
            if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El nombre del proyecto es requerido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Proyecto proyectoCreado = proyectoService.crearProyecto(proyecto);
            
            response.put("success", true);
            response.put("message", "Proyecto creado exitosamente");
            response.put("data", proyectoCreado);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al crear proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al crear proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * PUT /api/proyectos/{id} - Actualizar proyecto
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable String id, @RequestBody Proyecto proyecto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar que el proyecto existe
            if (!proyectoService.obtenerProyectoPorId(id).isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                response.put("data", null);
                return ResponseEntity.notFound().build();
            }
            
            // Validar datos básicos
            if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El nombre del proyecto es requerido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Asegurar que el ID coincida
            proyecto.setId(id);
            
            Proyecto proyectoActualizado = proyectoService.actualizarProyecto(id, proyecto);
            
            if (proyectoActualizado != null) {
                response.put("success", true);
                response.put("message", "Proyecto actualizado exitosamente");
                response.put("data", proyectoActualizado);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No se pudo actualizar el proyecto");
                response.put("data", null);
                return ResponseEntity.internalServerError().body(response);
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al actualizar proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al actualizar proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * DELETE /api/proyectos/{id} - Eliminar proyecto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar que el proyecto existe
            Optional<Proyecto> proyectoExistente = proyectoService.obtenerProyectoPorId(id);
            if (!proyectoExistente.isPresent()) {
                response.put("success", false);
                response.put("message", "Proyecto no encontrado con ID: " + id);
                response.put("data", null);
                return ResponseEntity.notFound().build();
            }
            
            proyectoService.eliminarProyecto(id);
            
            response.put("success", true);
            response.put("message", "Proyecto eliminado exitosamente");
            response.put("data", Map.of("id", id, "nombre", proyectoExistente.get().getNombre()));
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al eliminar proyecto: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * POST /api/proyectos/{id}/tareas - Agregar nueva tarea a un proyecto
     */
    @PostMapping("/{id}/tareas")
    public ResponseEntity<Map<String, Object>> agregarTarea(
            @PathVariable String id, 
            @RequestBody Tarea tarea) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar que la tarea tenga título
            if (tarea.getTitulo() == null || tarea.getTitulo().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El título de la tarea es requerido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Proyecto proyectoActualizado = proyectoService.agregarTarea(id, tarea);
            
            response.put("success", true);
            response.put("message", "Tarea agregada exitosamente al proyecto");
            response.put("data", proyectoActualizado);
            response.put("tareaAgregada", tarea);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al agregar tarea: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al agregar tarea: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * PUT /api/proyectos/{id}/tareas/{tareaIndex} - Actualizar tarea específica
     */
    @PutMapping("/{id}/tareas/{tareaIndex}")
    public ResponseEntity<Map<String, Object>> actualizarTarea(
            @PathVariable String id,
            @PathVariable int tareaIndex,
            @RequestBody Tarea tarea) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (tareaIndex < 0) {
                response.put("success", false);
                response.put("message", "Índice de tarea inválido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Proyecto proyectoActualizado = proyectoService.actualizarTarea(id, tareaIndex, tarea);
            
            response.put("success", true);
            response.put("message", "Tarea actualizada exitosamente");
            response.put("data", proyectoActualizado);
            response.put("tareaIndex", tareaIndex);
            response.put("tareaActualizada", tarea);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al actualizar tarea: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al actualizar tarea: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * DELETE /api/proyectos/{id}/tareas/{tareaIndex} - Eliminar tarea específica
     */
    @DeleteMapping("/{id}/tareas/{tareaIndex}")
    public ResponseEntity<Map<String, Object>> eliminarTarea(
            @PathVariable String id,
            @PathVariable int tareaIndex) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (tareaIndex < 0) {
                response.put("success", false);
                response.put("message", "Índice de tarea inválido");
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Proyecto proyectoActualizado = proyectoService.eliminarTarea(id, tareaIndex);
            
            response.put("success", true);
            response.put("message", "Tarea eliminada exitosamente");
            response.put("data", proyectoActualizado);
            response.put("tareaEliminada", tareaIndex);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar tarea: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al eliminar tarea: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * GET /api/proyectos/estadisticas - Estadísticas generales de proyectos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Proyecto> todosLosProyectos = proyectoService.obtenerTodosProyectos();
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalProyectos", todosLosProyectos.size());
            estadisticas.put("proyectosCompletados", 
                todosLosProyectos.stream().filter(Proyecto::isCompletado).count());
            estadisticas.put("proyectosActivos", 
                todosLosProyectos.stream().filter(p -> !p.isCompletado()).count());
            
            // Estadísticas de tareas
            long totalTareas = todosLosProyectos.stream()
                .mapToInt(p -> p.getTareas().size())
                .sum();
            
            long tareasCompletadas = todosLosProyectos.stream()
                .flatMap(p -> p.getTareas().stream())
                .filter(t -> "completo".equals(t.getEstado()))
                .count();
                
            estadisticas.put("totalTareas", totalTareas);
            estadisticas.put("tareasCompletadas", tareasCompletadas);
            estadisticas.put("tareasPendientes", totalTareas - tareasCompletadas);
            
            response.put("success", true);
            response.put("message", "Estadísticas generadas exitosamente");
            response.put("data", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al generar estadísticas: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}