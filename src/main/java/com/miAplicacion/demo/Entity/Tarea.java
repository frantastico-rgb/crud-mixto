
package com.miAplicacion.demo.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
     */
    private EstadoTarea estado;

    // Otros campos recomendados
    private String descripcion;
    private Long empleadoId;
    private java.time.LocalDate fechaInicio;
    private java.time.LocalDate fechaVencimiento;
    private java.time.LocalDate fechaFinalizacion;
    private String comentarios;

    // Constructores explícitos para compatibilidad
    public Tarea() {}

    public Tarea(String titulo, EstadoTarea estado) {
        this.titulo = titulo;
        this.estado = estado;
    }

    // Getters y setters explícitos para compatibilidad
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }

    // Compatibilidad: aceptar también un setter que reciba String (por ejemplo desde formularios antiguos)
    public void setEstado(String estadoStr) {
        this.estado = EstadoTarea.from(estadoStr);
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

    public java.time.LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(java.time.LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public java.time.LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(java.time.LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public java.time.LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(java.time.LocalDate fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    /**
     * Enum para los estados posibles de la tarea
     */
    public enum EstadoTarea {
        PENDIENTE,
        EN_PROGRESO,
        EN_REVISION,
        COMPLETADA,
        CANCELADA;
        ;

        @JsonCreator
        public static EstadoTarea fromString(String value) {
            if (value == null) return null;
            String s = value.trim().toLowerCase();
            // Normalizar variantes comunes en español/inglés
            if (s.contains("complet")) return COMPLETADA; // completo, completada
            if (s.contains("pend")) return PENDIENTE; // pendiente
            if (s.contains("rev")) return EN_REVISION; // revision, en_revision
            if (s.contains("cancel")) return CANCELADA; // cancelada
            if (s.contains("progres") || s.contains("proceso") || s.contains("progreso") ) return EN_PROGRESO; // en progreso / en proceso

            // Try direct name match (allow underscores/hyphens/space)
            String normalized = value.trim().toUpperCase().replace(" ", "_").replace('-', '_');
            try {
                return EstadoTarea.valueOf(normalized);
            } catch (IllegalArgumentException ex) {
                // Fallback: return PENDIENTE para valores desconocidos
                return PENDIENTE;
            }
        }

        @JsonValue
        public String toValue() {
            return this.name();
        }


    // Setter que acepta String para binding desde formularios o documentos antiguos
    public void setEstado(String estado) {
        this.estado = EstadoTarea.fromString(estado);
    }
        @JsonCreator
        public static EstadoTarea from(String value) {
            if (value == null) return null;
            String v = value.trim().toUpperCase().replace(" ", "_").replace("-", "_");
            // Accept common Spanish inputs
            if (v.equals("EN_PROCESO") || v.equals("ENPROCESO")) v = "EN_PROGRESO";
            if (v.equals("COMPLETO")) v = "COMPLETADA";
            try {
                return EstadoTarea.valueOf(v);
            } catch (IllegalArgumentException ex) {
                return null; // unknown value -> null (caller can handle)
            }
        }

        @JsonValue
        public String toValue() {
            return this.name();
        }
    }
}