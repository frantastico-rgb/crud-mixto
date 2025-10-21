package com.miAplicacion.demo.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 📚 CONFIGURACIÓN SWAGGER/OpenAPI
 * 
 * ¿POR QUÉ ES CRÍTICO PARA INTEGRACIÓN CON CLIENTES?
 * ✅ Documentación automática de todos los endpoints
 * ✅ Schemas de datos para request/response
 * ✅ Ejemplos de uso para desarrolladores externos
 * ✅ Cliente SDK auto-generado en múltiples lenguajes
 * ✅ Testing interactivo desde el navegador
 * ✅ Contratos API versionados y mantenibles
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI demoMixtoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DemoMixto API - Sistema de Gestión Híbrido")
                        .description("""
                                🚀 **API REST completa para gestión de empleados y proyectos**
                                
                                ## 🎯 Características Principales
                                - **Dual Persistence**: MySQL (Empleados) + MongoDB (Proyectos)
                                - **Seguridad**: Autenticación HTTP Basic con roles
                                - **CRUD Completo**: Operaciones completas en ambas entidades
                                - **Reportes**: Exportación a Excel y análisis de datos
                                - **Testing**: Suite completa de tests automatizados
                                
                                ## 🔐 Autenticación
                                - **Admin**: `admin:admin` (acceso completo a empleados)
                                - **User**: `user:password` (acceso a proyectos)
                                
                                ## 📊 Casos de Uso de Integración
                                - **ERP Systems**: Sincronización de empleados
                                - **Project Management**: Gestión de proyectos y tareas
                                - **HR Systems**: Integración con sistemas de RRHH
                                - **BI Tools**: Extracción de datos para análisis
                                - **Mobile Apps**: Backend para aplicaciones móviles
                                
                                ## 🛠️ Tecnologías
                                - Spring Boot 3.5.6
                                - Spring Security
                                - JPA + Hibernate (MySQL)
                                - Spring Data MongoDB
                                - Docker Ready
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("DemoMixto Development Team")
                                .email("dev@demomixto.com")
                                .url("https://github.com/your-repo/demomixto"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("🏠 Desarrollo Local"),
                        new Server()
                                .url("https://your-app.herokuapp.com")
                                .description("🚀 Producción Heroku"),
                        new Server()
                                .url("https://staging-app.herokuapp.com")
                                .description("🧪 Staging Environment")
                ))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("🔐 HTTP Basic Authentication\n\n" +
                                                "**Credenciales disponibles:**\n" +
                                                "- Admin: `admin:admin` (acceso completo)\n" +
                                                "- User: `user:password` (solo proyectos)")));
    }
}