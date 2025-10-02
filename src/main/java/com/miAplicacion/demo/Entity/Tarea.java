package com.miAplicacion.demo.Entity;

import lombok.Data;

/**
 * Clase que representa una tarea dentro de un proyecto.
 * Esta clase será embebida en los documentos de MongoDB como parte del Proyecto.
 * No necesita @Document ni @Entity porque será parte de otro documento.
 */
@Data                    // Lombok: genera getters, setters, toString, equals, hashCode
public class Tarea {
    
    /**
     * Título descriptivo de la tarea
     */
    private String titulo;
    
    /**
     * Estado actual de la tarea
     * Valores permitidos: "pendiente", "en_progreso", "completo"
     */
    private String estado;
    
    // Constructores explícitos para compatibilidad
    public Tarea() {}
    
    public Tarea(String titulo, String estado) {
        this.titulo = titulo;
        this.estado = estado;
    }
    
    // Getters y setters explícitos para compatibilidad
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}