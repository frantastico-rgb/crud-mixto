package com.miAplicacion.demo.Repository;

import com.miAplicacion.demo.Entity.Proyecto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository para manejar operaciones CRUD en MongoDB Atlas
 * Spring Data MongoDB genera automáticamente las implementaciones
 * basándose en los nombres de los métodos
 */
@Repository
public interface ProyectoRepository extends MongoRepository<Proyecto, String> {
    
    /**
     * Busca todos los proyectos asignados a un empleado específico
     * Spring Data MongoDB creará automáticamente la consulta:
     * db.proyectos.find({"empleadoId": empleadoId})
     * 
     * @param empleadoId ID del empleado en la base de datos MySQL
     * @return Lista de proyectos asignados a ese empleado
     */
    List<Proyecto> findByEmpleadoId(Long empleadoId);
    
    /**
     * Busca proyectos por nombre (búsqueda parcial, case insensitive)
     * Útil para implementar funcionalidad de búsqueda
     * Consulta MongoDB: db.proyectos.find({"nombre": {$regex: ".*nombre.*", $options: "i"}})
     * 
     * @param nombre Parte del nombre del proyecto a buscar
     * @return Lista de proyectos que contienen el texto en el nombre
     */
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca proyectos por estado de completado
     * Útil para listar solo proyectos activos o completados
     * 
     * @param completado true para proyectos completados, false para activos
     * @return Lista de proyectos filtrados por estado
     */
    List<Proyecto> findByCompletado(boolean completado);
    
    /**
     * Busca proyectos asignados a un empleado Y con un estado específico
     * Combinación de filtros útil para dashboards
     * 
     * @param empleadoId ID del empleado
     * @param completado Estado de completado
     * @return Lista de proyectos que cumplen ambas condiciones
     */
    List<Proyecto> findByEmpleadoIdAndCompletado(Long empleadoId, boolean completado);
}