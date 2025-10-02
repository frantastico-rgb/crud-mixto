package com.miAplicacion.demo.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Entidad que representa un Proyecto almacenado en MongoDB Atlas.
 * Cada proyecto puede estar asignado a un empleado y contener múltiples tareas.
 */
@Document(collection = "proyectos")  // Se almacena en la colección "proyectos" de MongoDB
@Data                                // Lombok: genera getters, setters, toString, equals, hashCode
public class Proyecto {
    
    /**
     * ID único del proyecto (ObjectId de MongoDB convertido a String)
     */
    @Id
    private String id;
    
    /**
     * Nombre del proyecto
     */
    private String nombre;
    
    /**
     * Descripción detallada del proyecto
     */
    private String descripcion;
    
    /**
     * ID del empleado responsable del proyecto (referencia a la tabla empleados en MySQL)
     * Este campo crea la relación entre MongoDB y MySQL
     */
    private Long empleadoId;
    
    /**
     * Fecha de inicio del proyecto
     */
    private LocalDate fechaInicio;
    
    /**
     * Indica si el proyecto está completado
     */
    private boolean completado;
    
    /**
     * Lista de tareas embebidas dentro del documento del proyecto
     * Se inicializa como ArrayList vacía para evitar NullPointerException
     */
    private List<Tarea> tareas = new ArrayList<>();
    
    // Getters y setters explícitos para compatibilidad
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
    
    public List<Tarea> getTareas() { return tareas; }
    public void setTareas(List<Tarea> tareas) { this.tareas = tareas != null ? tareas : new ArrayList<>(); }
    
    /**
     * Método auxiliar para contar las tareas completadas
     * @return número de tareas con estado "completo"
     */
    public long getTareasCompletadas() {
        if (tareas == null) return 0;
        return tareas.stream()
                    .filter(tarea -> "completo".equals(tarea.getEstado()))
                    .count();
    }
    
    /**
     * Método auxiliar para obtener el total de tareas
     * @return número total de tareas
     */
    public int getTotalTareas() {
        return tareas != null ? tareas.size() : 0;
    }
}
