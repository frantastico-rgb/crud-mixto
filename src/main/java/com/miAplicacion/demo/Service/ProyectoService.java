package com.miAplicacion.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Entity.Tarea;
import com.miAplicacion.demo.Repository.ProyectoRepository;
import com.miAplicacion.demo.Repository.EmpleadoRepository;

/**
 * Servicio que maneja la lógica de negocio para proyectos en MongoDB
 * Incluye validaciones de asociación con empleados y gestión de tareas
 */
@Service
public class ProyectoService {
    
    @Autowired
    private ProyectoRepository proyectoRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;

    /**
     * Crea un nuevo proyecto con validación de empleado asignado
     * @param proyecto Datos del proyecto a crear
     * @return Proyecto creado con ID asignado por MongoDB
     * @throws RuntimeException si el empleadoId no existe en MySQL
     */
    public Proyecto crearProyecto(Proyecto proyecto) {
        // Validar que el empleado existe si se proporciona empleadoId
        if (proyecto.getEmpleadoId() != null) {
            if (!empleadoRepository.existsById(proyecto.getEmpleadoId())) {
                throw new RuntimeException("El empleado con ID " + proyecto.getEmpleadoId() + " no existe");
            }
        }
        
        // Asegurar que el ID sea null para que MongoDB lo genere automáticamente
        proyecto.setId(null);
        return proyectoRepository.save(proyecto);
    }

    public List<Proyecto> obtenerTodosProyectos() {
        return proyectoRepository.findAll();
    }

    public Optional<Proyecto> obtenerProyectoPorId(String id) {
        return proyectoRepository.findById(id);
    }
    
    /**
     * Obtiene todos los proyectos asignados a un empleado específico
     * @param empleadoId ID del empleado en MySQL
     * @return Lista de proyectos del empleado
     */
    public List<Proyecto> obtenerProyectosPorEmpleado(Long empleadoId) {
        return proyectoRepository.findByEmpleadoId(empleadoId);
    }
    
    /**
     * Busca proyectos por nombre (búsqueda parcial)
     * @param nombre Parte del nombre del proyecto
     * @return Lista de proyectos que contienen el texto
     */
    public List<Proyecto> buscarProyectosPorNombre(String nombre) {
        return proyectoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    /**
     * Obtiene proyectos filtrados por estado de completado
     * @param completado true para completados, false para activos
     * @return Lista de proyectos filtrados
     */
    public List<Proyecto> obtenerProyectosPorEstado(boolean completado) {
        return proyectoRepository.findByCompletado(completado);
    }
    
    /**
     * Obtiene proyectos de un empleado filtrados por estado
     * @param empleadoId ID del empleado
     * @param completado Estado de completado
     * @return Lista de proyectos que cumplen ambas condiciones
     */
    public List<Proyecto> obtenerProyectosEmpleadoPorEstado(Long empleadoId, boolean completado) {
        return proyectoRepository.findByEmpleadoIdAndCompletado(empleadoId, completado);
    }

    /**
     * Actualiza un proyecto existente con validación de empleado
     * @param id ID del proyecto en MongoDB
     * @param proyectoDetalles Nuevos datos del proyecto
     * @return Proyecto actualizado
     * @throws RuntimeException si el proyecto no existe o el empleadoId es inválido
     */
    public Proyecto actualizarProyecto(String id, Proyecto proyectoDetalles) {
        
        // 1. Buscar el proyecto existente por ID
        Optional<Proyecto> proyectoExistente = proyectoRepository.findById(id);
        
        if (proyectoExistente.isPresent()) {
            Proyecto proyecto = proyectoExistente.get();
            
            // 2. Validar empleado si se proporciona nuevo empleadoId
            if (proyectoDetalles.getEmpleadoId() != null) {
                if (!empleadoRepository.existsById(proyectoDetalles.getEmpleadoId())) {
                    throw new RuntimeException("El empleado con ID " + proyectoDetalles.getEmpleadoId() + " no existe");
                }
            }
            
            // 3. Actualizar todos los campos (preservando el ID original)
            proyecto.setNombre(proyectoDetalles.getNombre());
            proyecto.setDescripcion(proyectoDetalles.getDescripcion());
            proyecto.setEmpleadoId(proyectoDetalles.getEmpleadoId());
            proyecto.setFechaInicio(proyectoDetalles.getFechaInicio());
            proyecto.setCompletado(proyectoDetalles.isCompletado());
            proyecto.setTareas(proyectoDetalles.getTareas());
            
            // 4. Guardar y retornar el proyecto actualizado
            return proyectoRepository.save(proyecto);
        } else {
            throw new RuntimeException("Proyecto no encontrado con ID: " + id);
        }
    }
    
    /**
     * Agrega una nueva tarea a un proyecto existente
     * @param proyectoId ID del proyecto
     * @param tarea Nueva tarea a agregar
     * @return Proyecto actualizado con la nueva tarea
     * @throws RuntimeException si el proyecto no existe
     */
    public Proyecto agregarTarea(String proyectoId, Tarea tarea) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            proyecto.getTareas().add(tarea);
            return proyectoRepository.save(proyecto);
        } else {
            throw new RuntimeException("Proyecto no encontrado con ID: " + proyectoId);
        }
    }
    
    /**
     * Actualiza una tarea específica dentro de un proyecto
     * @param proyectoId ID del proyecto
     * @param tareaIndex Índice de la tarea en la lista (0-based)
     * @param tareaActualizada Nuevos datos de la tarea
     * @return Proyecto actualizado
     * @throws RuntimeException si el proyecto no existe o el índice es inválido
     */
    public Proyecto actualizarTarea(String proyectoId, int tareaIndex, Tarea tareaActualizada) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            if (tareaIndex >= 0 && tareaIndex < proyecto.getTareas().size()) {
                proyecto.getTareas().set(tareaIndex, tareaActualizada);
                return proyectoRepository.save(proyecto);
            } else {
                throw new RuntimeException("Índice de tarea inválido: " + tareaIndex);
            }
        } else {
            throw new RuntimeException("Proyecto no encontrado con ID: " + proyectoId);
        }
    }
    
    /**
     * Elimina una tarea específica de un proyecto
     * @param proyectoId ID del proyecto
     * @param tareaIndex Índice de la tarea a eliminar
     * @return Proyecto actualizado sin la tarea
     * @throws RuntimeException si el proyecto no existe o el índice es inválido
     */
    public Proyecto eliminarTarea(String proyectoId, int tareaIndex) {
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (proyectoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            if (tareaIndex >= 0 && tareaIndex < proyecto.getTareas().size()) {
                proyecto.getTareas().remove(tareaIndex);
                return proyectoRepository.save(proyecto);
            } else {
                throw new RuntimeException("Índice de tarea inválido: " + tareaIndex);
            }
        } else {
            throw new RuntimeException("Proyecto no encontrado con ID: " + proyectoId);
        }
    }

    public void eliminarProyecto(String id) {
        proyectoRepository.deleteById(id);
    }
}