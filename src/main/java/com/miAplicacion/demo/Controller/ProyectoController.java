package com.miAplicacion.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.List;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.beans.PropertyEditorSupport;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import java.util.Arrays;

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
     * InitBinder para convertir Strings del formulario a EstadoTarea.
     * Esto permite aceptar valores legacy como "completo" o "en proceso".
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Tarea.EstadoTarea.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(Tarea.EstadoTarea.fromString(text));
            }

            @Override
            public String getAsText() {
                Object v = getValue();
                return v != null ? v.toString() : null;
            }
        });
    }

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
            .filter(t -> t.getEstado() == Tarea.EstadoTarea.COMPLETADA)
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
                .filter(t -> t.getEstado() == Tarea.EstadoTarea.COMPLETADA)
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
                    .filter(tarea -> tarea.getEstado() == Tarea.EstadoTarea.COMPLETADA)
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
                .filter(t -> t.getEstado() == Tarea.EstadoTarea.COMPLETADA)
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
    
    /**
     * 🔄 **ENDPOINT TEMPORAL** - Inicializar datos de ejemplo con estados expandidos
     * 
     * ⚠️ SOLO PARA DEMO - Borra todos los proyectos existentes y crea nuevos con estados expandidos
     * 
     * **URL:** GET /proyectos/init-data
     * **Respuesta:** JSON con proyectos creados
     */
    @GetMapping("/init-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> inicializarDatosEjemplo() {
        try {
            // Limpiar datos existentes
            proyectoService.eliminarTodosProyectos();
            
            // Crear proyectos de ejemplo con estados expandidos
            List<Proyecto> proyectosCreados = crearProyectosEjemplo();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Datos de ejemplo inicializados correctamente");
            response.put("proyectosCreados", proyectosCreados.size());
            response.put("estadosDisponibles", Arrays.asList("PENDIENTE", "EN_PROGRESO", "EN_REVISION", "COMPLETADA", "CANCELADA"));
            response.put("redirect", "/proyectos");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al inicializar datos: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Crear proyectos de ejemplo con todos los estados de tareas
     */
    private List<Proyecto> crearProyectosEjemplo() {
        List<Proyecto> proyectos = new java.util.ArrayList<>();
        
        // Proyecto 1: Sistema de Inventario
        Proyecto proyecto1 = new Proyecto();
        proyecto1.setNombre("Sistema de Gestión de Inventario");
        proyecto1.setDescripcion("Desarrollo de un sistema completo para gestionar inventarios de productos con reportes en tiempo real");
        proyecto1.setEmpleadoId(1L);
        proyecto1.setFechaInicio(LocalDate.of(2025, 1, 15));
        proyecto1.setCompletado(false);
        
        // Tareas con estados expandidos
        Tarea tarea1 = new Tarea("Análisis de requerimientos", Tarea.EstadoTarea.COMPLETADA);
        tarea1.setDescripcion("Reunión con stakeholders y documentación de requisitos");
        
        Tarea tarea2 = new Tarea("Diseño de base de datos", Tarea.EstadoTarea.COMPLETADA);
        tarea2.setDescripcion("Diseño del modelo entidad-relación y optimización");
        
        Tarea tarea3 = new Tarea("Desarrollo API REST", Tarea.EstadoTarea.EN_PROGRESO);
        tarea3.setDescripcion("Implementación de endpoints para CRUD de productos");
        
        Tarea tarea4 = new Tarea("Desarrollo Frontend", Tarea.EstadoTarea.PENDIENTE);
        tarea4.setDescripcion("Interface React para gestión visual del inventario");
        
        Tarea tarea5 = new Tarea("Testing unitario", Tarea.EstadoTarea.PENDIENTE);
        tarea5.setDescripcion("Crear suite de tests para todas las funcionalidades");
        
        proyecto1.setTareas(Arrays.asList(tarea1, tarea2, tarea3, tarea4, tarea5));
        proyectos.add(proyectoService.crearProyecto(proyecto1));
        
        // Proyecto 2: App Mobile Delivery
        Proyecto proyecto2 = new Proyecto();
        proyecto2.setNombre("App Mobile para Delivery");
        proyecto2.setDescripcion("Aplicación móvil nativa para pedidos de comida con geolocalización y pagos");
        proyecto2.setEmpleadoId(2L);
        proyecto2.setFechaInicio(LocalDate.of(2025, 2, 1));
        proyecto2.setCompletado(false);
        
        Tarea tarea6 = new Tarea("Wireframes y mockups", Tarea.EstadoTarea.COMPLETADA);
        tarea6.setDescripcion("Diseño UX/UI completo de todas las pantallas");
        
        Tarea tarea7 = new Tarea("Integración API pagos", Tarea.EstadoTarea.EN_REVISION);
        tarea7.setDescripcion("Integración con Stripe y PayPal para pagos seguros");
        
        Tarea tarea8 = new Tarea("Módulo geolocalización", Tarea.EstadoTarea.EN_PROGRESO);
        tarea8.setDescripcion("Implementar tracking en tiempo real del delivery");
        
        Tarea tarea9 = new Tarea("Testing en dispositivos", Tarea.EstadoTarea.PENDIENTE);
        tarea9.setDescripcion("Pruebas en iOS y Android con diferentes resoluciones");
        
        proyecto2.setTareas(Arrays.asList(tarea6, tarea7, tarea8, tarea9));
        proyectos.add(proyectoService.crearProyecto(proyecto2));
        
        // Proyecto 3: Dashboard Analytics (con algunas tareas canceladas)
        Proyecto proyecto3 = new Proyecto();
        proyecto3.setNombre("Dashboard de Analytics");
        proyecto3.setDescripcion("Panel de control ejecutivo con métricas de negocio y visualizaciones interactivas");
        proyecto3.setEmpleadoId(3L);
        proyecto3.setFechaInicio(LocalDate.of(2025, 1, 10));
        proyecto3.setCompletado(true);
        
        Tarea tarea10 = new Tarea("Conectores base de datos", Tarea.EstadoTarea.COMPLETADA);
        tarea10.setDescripcion("Conexión con múltiples fuentes: MySQL, MongoDB, PostgreSQL");
        
        Tarea tarea11 = new Tarea("Visualizaciones Chart.js", Tarea.EstadoTarea.COMPLETADA);
        tarea11.setDescripcion("Gráficos interactivos con filtros y drill-down");
        
        Tarea tarea12 = new Tarea("Sistema de alertas", Tarea.EstadoTarea.COMPLETADA);
        tarea12.setDescripcion("Notificaciones automáticas por email y push");
        
        Tarea tarea13 = new Tarea("Integración con BI tools", Tarea.EstadoTarea.CANCELADA);
        tarea13.setDescripcion("Conector con Tableau - Cancelado por cambio de prioridades");
        
        Tarea tarea14 = new Tarea("Export PDF/Excel", Tarea.EstadoTarea.COMPLETADA);
        tarea14.setDescripcion("Generación automática de reportes ejecutivos");
        
        proyecto3.setTareas(Arrays.asList(tarea10, tarea11, tarea12, tarea13, tarea14));
        proyectos.add(proyectoService.crearProyecto(proyecto3));
        
        // Proyecto 4: E-commerce B2B
        Proyecto proyecto4 = new Proyecto();
        proyecto4.setNombre("Plataforma E-commerce B2B");
        proyecto4.setDescripcion("Marketplace para empresas con catálogo masivo, cotizaciones y facturación automática");
        proyecto4.setEmpleadoId(4L);
        proyecto4.setFechaInicio(LocalDate.of(2025, 3, 1));
        proyecto4.setCompletado(false);
        
        Tarea tarea15 = new Tarea("Arquitectura microservicios", Tarea.EstadoTarea.EN_REVISION);
        tarea15.setDescripcion("Diseño de arquitectura escalable con Docker y Kubernetes");
        
        Tarea tarea16 = new Tarea("Catálogo de productos", Tarea.EstadoTarea.EN_PROGRESO);
        tarea16.setDescripcion("Sistema de búsqueda avanzada con Elasticsearch");
        
        Tarea tarea17 = new Tarea("Portal de proveedores", Tarea.EstadoTarea.PENDIENTE);
        tarea17.setDescripcion("Panel para que proveedores gestionen sus productos");
        
        Tarea tarea18 = new Tarea("Integración ERP", Tarea.EstadoTarea.PENDIENTE);
        tarea18.setDescripcion("Sincronización con SAP y otros sistemas empresariales");
        
        proyecto4.setTareas(Arrays.asList(tarea15, tarea16, tarea17, tarea18));
        proyectos.add(proyectoService.crearProyecto(proyecto4));
        
        return proyectos;
    }

    /**
     * Página para actualizar proyectos con tareas de ejemplo
     */
    @GetMapping("/actualizar-tareas")
    public String mostrarPaginaActualizar() {
        return "actualizar-tareas";
    }

    /**
     * Endpoint temporal para actualizar proyectos existentes con tareas de ejemplo
     * URL: /proyectos/actualizar-tareas-existentes
     */
    @PostMapping("/actualizar-tareas-existentes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarTareasExistentes() {
        try {
            List<Proyecto> proyectosExistentes = proyectoService.obtenerTodosProyectos();
            int proyectosActualizados = 0;
            
            for (Proyecto proyecto : proyectosExistentes) {
                // Solo actualizar proyectos que no tienen tareas o tienen pocas tareas
                if (proyecto.getTareas() == null || proyecto.getTotalTareas() < 2) {
                    List<Tarea> nuevasTareas = crearTareasEjemplo(proyecto.getNombre());
                    proyecto.setTareas(nuevasTareas);
                    proyectoService.actualizarProyecto(proyecto.getId(), proyecto);
                    proyectosActualizados++;
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Proyectos actualizados con tareas de ejemplo",
                "proyectosActualizados", proyectosActualizados,
                "totalProyectos", proyectosExistentes.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error al actualizar proyectos: " + e.getMessage()
            ));
        }
    }

    /**
     * Crear tareas de ejemplo basadas en el nombre del proyecto
     */
    private List<Tarea> crearTareasEjemplo(String nombreProyecto) {
        List<Tarea> tareas = new ArrayList<>();
        
        if (nombreProyecto != null && nombreProyecto.toLowerCase().contains("api")) {
            // Tareas para proyectos de API
            Tarea tarea1 = new Tarea("Diseño de endpoints", Tarea.EstadoTarea.COMPLETADA);
            tarea1.setDescripcion("Definir estructura y documentación de APIs");
            
            Tarea tarea2 = new Tarea("Implementación CRUD", Tarea.EstadoTarea.EN_PROGRESO);
            tarea2.setDescripcion("Desarrollo de operaciones Create, Read, Update, Delete");
            
            Tarea tarea3 = new Tarea("Testing de integración", Tarea.EstadoTarea.PENDIENTE);
            tarea3.setDescripcion("Pruebas automatizadas de endpoints");
            
            Tarea tarea4 = new Tarea("Documentación Swagger", Tarea.EstadoTarea.EN_REVISION);
            tarea4.setDescripcion("Generar documentación interactiva de API");
            
            tareas.addAll(Arrays.asList(tarea1, tarea2, tarea3, tarea4));
            
        } else if (nombreProyecto != null && nombreProyecto.toLowerCase().contains("inventario")) {
            // Tareas para proyectos de inventario
            Tarea tarea1 = new Tarea("Modelo de datos", Tarea.EstadoTarea.COMPLETADA);
            tarea1.setDescripcion("Diseño de entidades y relaciones");
            
            Tarea tarea2 = new Tarea("Módulo de productos", Tarea.EstadoTarea.COMPLETADA);
            tarea2.setDescripcion("Gestión completa de catálogo de productos");
            
            Tarea tarea3 = new Tarea("Control de stock", Tarea.EstadoTarea.EN_PROGRESO);
            tarea3.setDescripcion("Sistema de entrada y salida de inventario");
            
            Tarea tarea4 = new Tarea("Reportes automáticos", Tarea.EstadoTarea.PENDIENTE);
            tarea4.setDescripcion("Generación de reportes de stock y movimientos");
            
            Tarea tarea5 = new Tarea("Alertas de stock bajo", Tarea.EstadoTarea.EN_REVISION);
            tarea5.setDescripcion("Notificaciones automáticas cuando el stock es bajo");
            
            tareas.addAll(Arrays.asList(tarea1, tarea2, tarea3, tarea4, tarea5));
            
        } else {
            // Tareas genéricas para cualquier proyecto
            Tarea tarea1 = new Tarea("Planificación inicial", Tarea.EstadoTarea.COMPLETADA);
            tarea1.setDescripcion("Análisis de requerimientos y planificación");
            
            Tarea tarea2 = new Tarea("Desarrollo principal", Tarea.EstadoTarea.EN_PROGRESO);
            tarea2.setDescripcion("Implementación de funcionalidades core");
            
            Tarea tarea3 = new Tarea("Testing y validación", Tarea.EstadoTarea.PENDIENTE);
            tarea3.setDescripcion("Pruebas exhaustivas del sistema");
            
            Tarea tarea4 = new Tarea("Revisión de código", Tarea.EstadoTarea.EN_REVISION);
            tarea4.setDescripcion("Code review y optimizaciones");
            
            Tarea tarea5 = new Tarea("Documentación", Tarea.EstadoTarea.CANCELADA);
            tarea5.setDescripcion("Documentación técnica - Pospuesta para siguiente fase");
            
            tareas.addAll(Arrays.asList(tarea1, tarea2, tarea3, tarea4, tarea5));
        }
        
        return tareas;
    }
}