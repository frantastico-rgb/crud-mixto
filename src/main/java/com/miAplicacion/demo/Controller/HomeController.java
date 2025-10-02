package com.miAplicacion.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página de inicio del sistema
 * Redirige a los usuarios a la página principal de gestión
 */
@Controller
public class HomeController {
    
    /**
     * Página de inicio - redirige a empleados por defecto
     * GET /
     */
    @GetMapping("/")
    public String inicio() {
        // Redirigir a la gestión de empleados como página principal
        return "redirect:/empleados";
    }
    
    /**
     * Página de inicio alternativa
     * GET /home
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/empleados";
    }
}