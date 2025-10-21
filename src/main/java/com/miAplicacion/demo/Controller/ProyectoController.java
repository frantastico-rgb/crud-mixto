package com.miAplicacion.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miAplicacion.demo.Entity.Empleado;
import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Entity.Tarea;
import com.miAplicacion.demo.Service.ProyectoService;
import com.miAplicacion.demo.Service.EmpleadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 📋 **PROYECTOS CONTROLLER** - Gestión completa de proyectos
 * 
 * **CARACTERÍSTICAS CLAVE PARA INTEGRACIÓN:**
 * ✅ CRUD completo con MongoDB
 * ✅ Gestión de tareas integrada
 * ✅ Reportes y estadísticas avanzadas
 * ✅ Exportación a múltiples formatos
 * ✅ Acceso público (sin autenticación)
 * ✅ Filtros avanzados por empleado/estado
 * 
 * **CASOS DE USO DE INTEGRACIÓN:**
 * 🔗 Project Management Tools
 * 🔗 Time Tracking Systems
 * 🔗 Mobile Apps
 * 🔗 Dashboard Systems
 * 🔗 Reporting Tools
 */
@Tag(name = "📋 Proyectos", description = "API completa para gestión de proyectos y tareas (acceso público)")
@Controller // Usa Controller para vistas, pero principalmente REST
@RequestMapping("/proyectos") // Todas las rutas empiezan con /proyectos
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;
    
    @Autowired
    private EmpleadoService empleadoService;

    /**
     * Crea un nuevo proyecto (POST desde formulario web)
     */
    @PostMapping
    public String crear(@ModelAttribute Proyecto proyecto) {
        try {
            proyectoService.crearProyecto(proyecto);
        } catch (RuntimeException e) {
            return "redirect:/proyectos?error=" + e.getMessage();
        }
        return "redirect:/proyectos";
    }

    /**
     * Lista proyectos con filtros opcionales
     * @param empleadoId Filtrar por empleado específico
     * @param completado Filtrar por estado de completado
     * @param busqueda Búsqueda por nombre
     */
    @GetMapping
    public String obtenerTodos(
            @RequestParam(value = "empleadoId", required = false) Long empleadoId,
            @RequestParam(value = "completado", required = false) Boolean completado,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {
        
        List<Proyecto> proyectos;
        
        // Aplicar filtros según parámetros
        if (empleadoId != null && completado != null) {
            proyectos = proyectoService.obtenerProyectosEmpleadoPorEstado(empleadoId, completado);
        } else if (empleadoId != null) {
            proyectos = proyectoService.obtenerProyectosPorEmpleado(empleadoId);
        } else if (completado != null) {
            proyectos = proyectoService.obtenerProyectosPorEstado(completado);
        } else if (busqueda != null && !busqueda.trim().isEmpty()) {
            proyectos = proyectoService.buscarProyectosPorNombre(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else {
            proyectos = proyectoService.obtenerTodosProyectos();
        }
        
        // Agregar lista de empleados para filtros en la vista
        model.addAttribute("empleados", empleadoService.obtenerTodosEmpleados());
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("empleadoIdFiltro", empleadoId);
        model.addAttribute("completadoFiltro", completado);
        
        return "proyectos";
    }

    @GetMapping("/crear")
    public String mostrarCrear(Model model) {
        model.addAttribute("empleados", empleadoService.obtenerTodosEmpleados());
        return "proyecto-crear";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable String id, Model model) {
        try {
            Optional<Proyecto> proyectoOpt = proyectoService.obtenerProyectoPorId(id);
            if (proyectoOpt.isPresent()) {
                model.addAttribute("proyecto", proyectoOpt.get());
                model.addAttribute("empleados", empleadoService.obtenerTodosEmpleados());
            } else {
                model.addAttribute("proyecto", null);
                model.addAttribute("error", "Proyecto no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            model.addAttribute("proyecto", null);
            model.addAttribute("error", "Error al cargar el proyecto: " + e.getMessage());
        }
        return "proyecto-editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizarProyecto(@PathVariable String id, Proyecto proyecto, Model model) {
        try {
            // Debug: Log de datos recibidos
            System.out.println("=== DEBUG ACTUALIZACIÓN ===");
            System.out.println("ID: " + id);
            System.out.println("Proyecto recibido: ");
            System.out.println("  - Nombre: " + proyecto.getNombre());
            System.out.println("  - Descripción: " + proyecto.getDescripcion());
            System.out.println("  - EmpleadoId: " + proyecto.getEmpleadoId());
            System.out.println("  - FechaInicio: " + proyecto.getFechaInicio());
            System.out.println("  - Completado: " + proyecto.isCompletado());
            
            // Asegurar que el ID del proyecto coincida con el de la URL
            proyecto.setId(id);
            
            // Actualizar el proyecto
            Proyecto proyectoActualizado = proyectoService.actualizarProyecto(id, proyecto);
            
            if (proyectoActualizado != null) {
                System.out.println("Proyecto actualizado exitosamente: " + proyectoActualizado.getNombre());
                return "redirect:/proyectos";
            } else {
                System.out.println("Error: proyecto actualizado es null");
                model.addAttribute("error", "No se pudo actualizar el proyecto");
                model.addAttribute("proyecto", proyecto);
                model.addAttribute("empleados", empleadoService.obtenerTodosEmpleados());
                return "proyecto-editar";
            }
        } catch (Exception e) {
            System.out.println("Error en actualización: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al actualizar: " + e.getMessage());
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("empleados", empleadoService.obtenerTodosEmpleados());
            return "proyecto-editar";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarWeb(@PathVariable String id) {
        proyectoService.eliminarProyecto(id);
        return "redirect:/proyectos";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerPorId(@PathVariable String id) {
        return proyectoService.obtenerProyectoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable String id, @ModelAttribute Proyecto proyecto) {
        proyectoService.actualizarProyecto(id, proyecto);
        return "redirect:/proyectos";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.noContent().build();
    }
    
    // ===============================
    // GESTIÓN DE TAREAS
    // ===============================
    
    /**
     * API REST: Agregar nueva tarea a un proyecto
     * POST /proyectos/{id}/tareas
     */
    @PostMapping("/{id}/tareas")
    @ResponseBody
    public ResponseEntity<Proyecto> agregarTarea(
            @PathVariable String id, 
            @RequestBody Tarea tarea) {
        try {
            Proyecto proyectoActualizado = proyectoService.agregarTarea(id, tarea);
            return ResponseEntity.ok(proyectoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * API REST: Actualizar tarea específica
     * PUT /proyectos/{id}/tareas/{tareaIndex}
     */
    @PostMapping("/{id}/tareas/{tareaIndex}")
    @ResponseBody
    public ResponseEntity<Proyecto> actualizarTarea(
            @PathVariable String id,
            @PathVariable int tareaIndex,
            @RequestBody Tarea tarea) {
        try {
            Proyecto proyectoActualizado = proyectoService.actualizarTarea(id, tareaIndex, tarea);
            return ResponseEntity.ok(proyectoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * API REST: Eliminar tarea específica
     * DELETE /proyectos/{id}/tareas/{tareaIndex}
     */
    @DeleteMapping("/{id}/tareas/{tareaIndex}")
    @ResponseBody
    public ResponseEntity<Proyecto> eliminarTarea(
            @PathVariable String id,
            @PathVariable int tareaIndex) {
        try {
            Proyecto proyectoActualizado = proyectoService.eliminarTarea(id, tareaIndex);
            return ResponseEntity.ok(proyectoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // ===============================
    // BÚSQUEDAS Y FILTROS REST API
    // ===============================
    
    /**
     * API REST: Buscar proyectos por nombre
     * GET /proyectos/buscar?nombre=sistema
     */
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<Proyecto>> buscarProyectos(
            @RequestParam("nombre") String nombre) {
        List<Proyecto> proyectos = proyectoService.buscarProyectosPorNombre(nombre);
        return ResponseEntity.ok(proyectos);
    }
    
    /**
     * API REST: Obtener proyectos por estado
     * GET /proyectos/estado?completado=true
     */
    @GetMapping("/estado")
    @ResponseBody
    public ResponseEntity<List<Proyecto>> obtenerPorEstado(
            @RequestParam("completado") boolean completado) {
        List<Proyecto> proyectos = proyectoService.obtenerProyectosPorEstado(completado);
        return ResponseEntity.ok(proyectos);
    }
    
    /**
     * API REST: Obtener proyectos de un empleado
     * GET /proyectos/empleado/{empleadoId}
     */
    @GetMapping("/empleado/{empleadoId}")
    @ResponseBody
    public ResponseEntity<List<Proyecto>> obtenerProyectosPorEmpleado(
            @PathVariable Long empleadoId) {
        List<Proyecto> proyectos = proyectoService.obtenerProyectosPorEmpleado(empleadoId);
        return ResponseEntity.ok(proyectos);
    }
    
    // ===============================
    // REPORTES Y ESTADÍSTICAS
    // ===============================
    
    /**
     * Vista de reportes y estadísticas de proyectos
     * GET /proyectos/reportes
     */
    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        // Estadísticas generales
        List<Proyecto> todosLosProyectos = proyectoService.obtenerTodosProyectos();
        long totalProyectos = todosLosProyectos.size();
        long proyectosCompletados = todosLosProyectos.stream()
            .filter(Proyecto::isCompletado)
            .count();
        long proyectosActivos = totalProyectos - proyectosCompletados;
        
        // Estadísticas por empleado
        Map<String, Long> proyectosPorEmpleado = new HashMap<>();
        List<Empleado> empleados = empleadoService.obtenerTodosEmpleados();
        
        for (Empleado empleado : empleados) {
            long cantidadProyectos = proyectoService.obtenerProyectosPorEmpleado(empleado.getId()).size();
            proyectosPorEmpleado.put(empleado.getNombre(), cantidadProyectos);
        }
        
        model.addAttribute("totalProyectos", totalProyectos);
        model.addAttribute("proyectosCompletados", proyectosCompletados);
        model.addAttribute("proyectosActivos", proyectosActivos);
        model.addAttribute("proyectosPorEmpleado", proyectosPorEmpleado);
        model.addAttribute("empleados", empleados);
        model.addAttribute("proyectos", todosLosProyectos); // Agregar la lista de proyectos
        
        return "proyecto-reportes";
    }
    
    /**
     * API REST: Estadísticas generales de proyectos
     * GET /proyectos/estadisticas
     */
    @GetMapping("/estadisticas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
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
        
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Exportar reporte de proyectos a CSV
     * GET /proyectos/exportar/csv
     */
    @GetMapping("/exportar/csv")
    public ResponseEntity<String> exportarCSV(
            @RequestParam(value = "empleadoId", required = false) Long empleadoId) {
        
        List<Proyecto> proyectos;
        if (empleadoId != null) {
            proyectos = proyectoService.obtenerProyectosPorEmpleado(empleadoId);
        } else {
            proyectos = proyectoService.obtenerTodosProyectos();
        }
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nombre,Descripcion,EmpleadoID,FechaInicio,Completado,CantidadTareas,TareasCompletadas\n");
        
        for (Proyecto proyecto : proyectos) {
            long tareasCompletadas = proyecto.getTareas().stream()
                .filter(t -> "completo".equals(t.getEstado()))
                .count();
            
            csv.append(String.format("\"%s\",\"%s\",\"%s\",%s,\"%s\",%s,%d,%d\n",
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion() != null ? proyecto.getDescripcion() : "",
                proyecto.getEmpleadoId() != null ? proyecto.getEmpleadoId() : "",
                proyecto.getFechaInicio() != null ? proyecto.getFechaInicio() : "",
                proyecto.isCompletado(),
                proyecto.getTareas().size(),
                tareasCompletadas
            ));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", 
            "proyectos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString());
    }
    
    /**
     * Exportar reporte de proyectos a Excel
     * GET /proyectos/exportar/excel
     */
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(value = "empleadoId", required = false) Long empleadoId) {
        
        List<Proyecto> proyectos;
        if (empleadoId != null) {
            proyectos = proyectoService.obtenerProyectosPorEmpleado(empleadoId);
        } else {
            proyectos = proyectoService.obtenerTodosProyectos();
        }
        
        try {
            // Crear libro de Excel
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Proyectos");
            
            // Crear estilos
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            // Crear encabezados
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Descripción", "Empleado ID", "Fecha Inicio", "Estado", "Total Tareas", "Tareas Completadas"};
            
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Proyecto proyecto : proyectos) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                
                long tareasCompletadas = proyecto.getTareas().stream()
                    .filter(tarea -> "completo".equals(tarea.getEstado()))
                    .count();
                
                row.createCell(0).setCellValue(proyecto.getId());
                row.createCell(1).setCellValue(proyecto.getNombre());
                row.createCell(2).setCellValue(proyecto.getDescripcion() != null ? proyecto.getDescripcion() : "");
                row.createCell(3).setCellValue(proyecto.getEmpleadoId() != null ? proyecto.getEmpleadoId().toString() : "Sin asignar");
                row.createCell(4).setCellValue(proyecto.getFechaInicio() != null ? proyecto.getFechaInicio().toString() : "");
                row.createCell(5).setCellValue(proyecto.isCompletado() ? "Completado" : "En Progreso");
                row.createCell(6).setCellValue(proyecto.getTareas().size());
                row.createCell(7).setCellValue((double) tareasCompletadas);
            }
            
            // Ajustar anchos de columna
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convertir a bytes
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeaders.setContentDispositionFormData("attachment", 
                "proyectos_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(out.toByteArray());
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Exportar reporte detallado con empleados (JSON para procesamiento adicional)
     * GET /proyectos/exportar/detallado
     */
    @GetMapping("/exportar/detallado")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> exportarReporteDetallado() {
        List<Proyecto> proyectos = proyectoService.obtenerTodosProyectos();
        
        List<Map<String, Object>> reporteDetallado = proyectos.stream().map(proyecto -> {
            Map<String, Object> item = new HashMap<>();
            item.put("proyecto", proyecto);
            
            // Agregar información del empleado si existe
            if (proyecto.getEmpleadoId() != null) {
                empleadoService.obtenerEmpleadoPorId(proyecto.getEmpleadoId())
                    .ifPresent(empleado -> item.put("empleado", empleado));
            }
            
            // Estadísticas de tareas
            long tareasTotal = proyecto.getTareas().size();
            long tareasCompletadas = proyecto.getTareas().stream()
                .filter(t -> "completo".equals(t.getEstado()))
                .count();
            
            item.put("estadisticasTareas", Map.of(
                "total", tareasTotal,
                "completadas", tareasCompletadas,
                "pendientes", tareasTotal - tareasCompletadas,
                "porcentajeCompletado", tareasTotal > 0 ? (tareasCompletadas * 100.0 / tareasTotal) : 0
            ));
            
            return item;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(reporteDetallado);
    }
}