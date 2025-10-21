package com.miAplicacion.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miAplicacion.demo.Entity.Empleado;
import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Service.EmpleadoService;
import com.miAplicacion.demo.Service.ProyectoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * 👥 **EMPLEADOS CONTROLLER** - Gestión completa de empleados
 * 
 * **CARACTERÍSTICAS CLAVE PARA INTEGRACIÓN:**
 * ✅ CRUD completo con validaciones
 * ✅ Búsquedas avanzadas (nombre, cargo, salario)
 * ✅ Reportes y estadísticas
 * ✅ Exportación a CSV/Excel
 * ✅ Integración con proyectos
 * ✅ Autenticación requerida (admin)
 * 
 * **CASOS DE USO DE INTEGRACIÓN:**
 * 🔗 ERP Systems: Sincronización de empleados
 * 🔗 HR Systems: Gestión de recursos humanos
 * 🔗 Payroll Systems: Datos para nómina
 * 🔗 BI Tools: Análisis de personal
 */
@Tag(name = "👥 Empleados", description = "API completa para gestión de empleados con seguridad admin")
@SecurityRequirement(name = "basicAuth")
@Controller // Usa Controller para renderizar vistas Thymeleaf
@RequestMapping("/empleados") // Todas las rutas empiezan con /empleados
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private ProyectoService proyectoService;

    /**
     * Crea un nuevo empleado (POST desde formulario web)
     */
    @PostMapping
    public String crear(@ModelAttribute Empleado empleado) {
        try {
            empleadoService.crearEmpleado(empleado);
        } catch (RuntimeException e) {
            // En una implementación más completa, manejarías el error en la vista
            return "redirect:/empleados?error=" + e.getMessage();
        }
        return "redirect:/empleados";
    }

    /**
     * Lista empleados con funcionalidad de búsqueda opcional
     * @param busqueda Término de búsqueda opcional (busca en nombre y cargo)
     */
    @GetMapping
    public String obtenerTodos(@RequestParam(value = "busqueda", required = false) String busqueda, Model model) {
        List<Empleado> empleados;
        
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            empleados = empleadoService.buscarEmpleados(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else {
            empleados = empleadoService.obtenerTodosEmpleados();
        }
        
        model.addAttribute("empleados", empleados);
        return "empleados-lista";
    }

    @GetMapping("/crear")
    public String mostrarCrear() {
        return "empleado-crear";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        model.addAttribute("empleado", empleadoService.obtenerEmpleadoPorId(id).orElse(null));
        return "empleado-editar";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarWeb(@PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return "redirect:/empleados";
    }

    /**
     * 👤 **OBTENER EMPLEADO POR ID**
     * Endpoint crítico para integraciones que necesitan datos específicos
     */
    @Operation(
        summary = "👤 Obtener empleado por ID",
        description = """
                **INTEGRACIÓN KEY**: Endpoint fundamental para obtener datos específicos
                - Retorna empleado completo con todos sus campos
                - Esencial para formularios de edición
                - Base para sincronización con sistemas externos
                """,
        tags = {"CRUD Básico"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Empleado encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Empleado.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "❌ Empleado no encontrado"),
        @ApiResponse(responseCode = "401", description = "🔒 No autorizado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerPorId(
            @Parameter(
                description = "ID único del empleado",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        return empleadoService.obtenerEmpleadoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Empleado empleado) {
        empleadoService.actualizarEmpleado(id, empleado);
        return "redirect:/empleados";
    }

    /**
     * Soporte para el formulario Thymeleaf que envía POST a /empleados/editar/{id}
     * (la plantilla actual apunta a /empleados/editar/{id} con method="post")
     * Delegamos en el método de actualización existente.
     */
    @PostMapping("/editar/{id}")
    public String actualizarDesdeEditar(@PathVariable Long id, @ModelAttribute Empleado empleado) {
        return actualizar(id, empleado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }
    
    // ===============================
    // NUEVOS ENDPOINTS DE BÚSQUEDA Y PROYECTOS
    // ===============================
    
    /**
     * 🔍 **BÚSQUEDA GENERAL DE EMPLEADOS**
     * Busca empleados por nombre o cargo usando un término general
     */
    @Operation(
        summary = "🔍 Buscar empleados por término",
        description = """
                **INTEGRACIÓN KEY**: Endpoint principal para búsqueda de empleados
                - Busca en nombre y cargo simultáneamente
                - Útil para autocomplete y filtros dinámicos
                - Respuesta rápida para interfaces client-side
                """,
        tags = {"Búsquedas"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "✅ Empleados encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Empleado.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "🔒 No autorizado - se requiere admin"),
        @ApiResponse(responseCode = "400", description = "❌ Parámetros inválidos")
    })
    @GetMapping("/buscar")
    @ResponseBody
    public ResponseEntity<List<Empleado>> buscarEmpleados(
            @Parameter(
                description = "Término de búsqueda (busca en nombre y cargo)",
                example = "Juan",
                required = true
            )
            @RequestParam("termino") String termino) {
        List<Empleado> empleados = empleadoService.buscarEmpleados(termino);
        return ResponseEntity.ok(empleados);
    }
    
    /**
     * API REST: Buscar empleados por nombre específicamente
     * GET /empleados/buscar-nombre?nombre=juan
     */
    @GetMapping("/buscar-nombre")
    @ResponseBody
    public ResponseEntity<List<Empleado>> buscarPorNombre(
            @RequestParam("nombre") String nombre) {
        List<Empleado> empleados = empleadoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(empleados);
    }
    
    /**
     * API REST: Buscar empleados por cargo específicamente
     * GET /empleados/buscar-cargo?cargo=developer
     */
    @GetMapping("/buscar-cargo")
    @ResponseBody
    public ResponseEntity<List<Empleado>> buscarPorCargo(
            @RequestParam("cargo") String cargo) {
        List<Empleado> empleados = empleadoService.buscarPorCargo(cargo);
        return ResponseEntity.ok(empleados);
    }
    
    /**
     * API REST: Buscar empleados por rango salarial
     * GET /empleados/buscar-salario?min=3000&max=5000
     */
    @GetMapping("/buscar-salario")
    @ResponseBody
    public ResponseEntity<List<Empleado>> buscarPorRangoSalarial(
            @RequestParam("min") Double salarioMin,
            @RequestParam("max") Double salarioMax) {
        List<Empleado> empleados = empleadoService.buscarPorRangoSalarial(salarioMin, salarioMax);
        return ResponseEntity.ok(empleados);
    }
    
    /**
     * Vista web: Mostrar proyectos de un empleado específico
     * GET /empleados/{id}/proyectos
     */
    @GetMapping("/{id}/proyectos")
    public String mostrarProyectosEmpleado(@PathVariable Long id, Model model) {
        // Obtener datos del empleado
        Empleado empleado = empleadoService.obtenerEmpleadoPorId(id)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        
        // Obtener proyectos del empleado
        List<Proyecto> proyectos = proyectoService.obtenerProyectosPorEmpleado(id);
        
        model.addAttribute("empleado", empleado);
        model.addAttribute("proyectos", proyectos);
        return "empleado-proyectos"; // Vista que crearemos más adelante
    }
    
    /**
     * API REST: Obtener proyectos de un empleado
     * GET /empleados/{id}/proyectos-json
     */
    @GetMapping("/{id}/proyectos-json")
    @ResponseBody
    public ResponseEntity<List<Proyecto>> obtenerProyectosEmpleado(@PathVariable Long id) {
        List<Proyecto> proyectos = proyectoService.obtenerProyectosPorEmpleado(id);
        return ResponseEntity.ok(proyectos);
    }
    
    // REPORTES Y ESTADÍSTICAS DE EMPLEADOS
    
    /**
     * Vista de reportes y estadísticas de empleados
     * GET /empleados/reportes
     */
    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        // Estadísticas generales
        List<Empleado> todosLosEmpleados = empleadoService.obtenerTodosEmpleados();
        long totalEmpleados = todosLosEmpleados.size();
        
        // Calcular estadísticas de salarios
        double salarioPromedio = todosLosEmpleados.stream()
            .mapToDouble(emp -> emp.getSalario().doubleValue())
            .average()
            .orElse(0.0);
        
        double salarioMaximo = todosLosEmpleados.stream()
            .mapToDouble(emp -> emp.getSalario().doubleValue())
            .max()
            .orElse(0.0);
        
        double salarioMinimo = todosLosEmpleados.stream()
            .mapToDouble(emp -> emp.getSalario().doubleValue())
            .min()
            .orElse(0.0);
        
        // Estadísticas por cargo
        java.util.Map<String, Long> empleadosPorCargo = todosLosEmpleados.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Empleado::getCargo,
                java.util.stream.Collectors.counting()
            ));
        
        // Estadísticas de proyectos por empleado
        java.util.Map<String, Integer> proyectosPorEmpleado = new java.util.HashMap<>();
        List<Proyecto> todosLosProyectos = proyectoService.obtenerTodosProyectos();
        
        for (Empleado empleado : todosLosEmpleados) {
            long cantidadProyectos = todosLosProyectos.stream()
                .filter(p -> empleado.getId().toString().equals(p.getEmpleadoId()))
                .count();
            proyectosPorEmpleado.put(empleado.getNombre(), (int) cantidadProyectos);
        }
        
        model.addAttribute("totalEmpleados", totalEmpleados);
        model.addAttribute("salarioPromedio", salarioPromedio);
        model.addAttribute("salarioMaximo", salarioMaximo);
        model.addAttribute("salarioMinimo", salarioMinimo);
        model.addAttribute("empleadosPorCargo", empleadosPorCargo);
        model.addAttribute("proyectosPorEmpleado", proyectosPorEmpleado);
        model.addAttribute("empleados", todosLosEmpleados);
        
        return "empleado-reportes";
    }
    
    /**
     * Exportar reporte de empleados a CSV
     * GET /empleados/exportar/csv
     */
    @GetMapping("/exportar/csv")
    @ResponseBody
    public ResponseEntity<String> exportarCSV() {
        List<Empleado> empleados = empleadoService.obtenerTodosEmpleados();
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nombre,Cargo,Salario,Email\n");
        
        for (Empleado empleado : empleados) {
            csv.append(empleado.getId()).append(",")
               .append(empleado.getNombre()).append(",")
               .append(empleado.getCargo()).append(",")
               .append(empleado.getSalario()).append(",")
               .append(empleado.getEmail()).append("\n");
        }
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=empleados-reporte.csv")
                .body(csv.toString());
    }
    
    /**
     * Exportar reporte de empleados a Excel
     * GET /empleados/exportar/excel
     */
    @GetMapping("/exportar/excel")
    @ResponseBody
    public ResponseEntity<byte[]> exportarExcel() {
        List<Empleado> empleados = empleadoService.obtenerTodosEmpleados();
        
        try {
            // Crear libro de Excel
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Empleados");
            
            // Crear estilos
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            
            org.apache.poi.ss.usermodel.CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
            
            // Crear encabezados
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Cargo", "Salario", "Email", "Proyectos Asignados"};
            
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            List<Proyecto> todosLosProyectos = proyectoService.obtenerTodosProyectos();
            
            for (Empleado empleado : empleados) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                
                // Contar proyectos asignados
                long cantidadProyectos = todosLosProyectos.stream()
                    .filter(p -> empleado.getId().equals(p.getEmpleadoId()))
                    .count();
                
                row.createCell(0).setCellValue(empleado.getId().toString());
                row.createCell(1).setCellValue(empleado.getNombre());
                row.createCell(2).setCellValue(empleado.getCargo());
                
                org.apache.poi.ss.usermodel.Cell salarioCell = row.createCell(3);
                salarioCell.setCellValue(empleado.getSalario().doubleValue());
                salarioCell.setCellStyle(currencyStyle);
                
                row.createCell(4).setCellValue(empleado.getEmail());
                row.createCell(5).setCellValue((double) cantidadProyectos);
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
                "empleados_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            
            return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(out.toByteArray());
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}