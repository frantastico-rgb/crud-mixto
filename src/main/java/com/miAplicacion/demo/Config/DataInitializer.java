package com.miAplicacion.demo.Config;

import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Entity.Tarea;
import com.miAplicacion.demo.Repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Configuración para crear datos de ejemplo con los nuevos estados de tareas
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    
    private final ProyectoRepository proyectoRepository;
    
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Solo crear datos si no existen proyectos
            if (proyectoRepository.count() == 0) {
                createSampleProjectsWithExpandedTaskStates();
            }
        };
    }
    
    private void createSampleProjectsWithExpandedTaskStates() {
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
        
        // Guardar todos los proyectos
        proyectoRepository.saveAll(Arrays.asList(proyecto1, proyecto2, proyecto3, proyecto4));
        
        System.out.println("✅ Datos de ejemplo creados con estados expandidos de tareas:");
        System.out.println("   📋 " + proyecto1.getNombre() + " - " + proyecto1.getTotalTareas() + " tareas");
        System.out.println("   📱 " + proyecto2.getNombre() + " - " + proyecto2.getTotalTareas() + " tareas"); 
        System.out.println("   📊 " + proyecto3.getNombre() + " - " + proyecto3.getTotalTareas() + " tareas");
        System.out.println("   🛒 " + proyecto4.getNombre() + " - " + proyecto4.getTotalTareas() + " tareas");
        System.out.println("🎯 Estados disponibles: PENDIENTE, EN_PROGRESO, EN_REVISION, COMPLETADA, CANCELADA");
    }
}