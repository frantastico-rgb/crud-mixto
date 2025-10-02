package com.miAplicacion.demo.Repository;

import com.miAplicacion.demo.Entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository para manejar operaciones CRUD en MySQL usando JPA
 * Spring Data JPA genera automáticamente las implementaciones SQL
 * basándose en los nombres de los métodos
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    /**
     * Busca un empleado por su email (para validación de unicidad)
     * Spring Data JPA generará: SELECT * FROM empleados WHERE email = ?
     * 
     * @param email Email del empleado a buscar
     * @return Optional<Empleado> - puede estar vacío si no existe
     */
    Optional<Empleado> findByEmail(String email);
    
    /**
     * Busca empleados por nombre (búsqueda parcial, case insensitive)
     * SQL generado: SELECT * FROM empleados WHERE LOWER(nombre) LIKE LOWER('%nombre%')
     * 
     * @param nombre Parte del nombre a buscar
     * @return Lista de empleados que contienen el texto en el nombre
     */
    List<Empleado> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca empleados por cargo (búsqueda parcial, case insensitive)
     * SQL generado: SELECT * FROM empleados WHERE LOWER(cargo) LIKE LOWER('%cargo%')
     * 
     * @param cargo Parte del cargo a buscar
     * @return Lista de empleados que contienen el texto en el cargo
     */
    List<Empleado> findByCargoContainingIgnoreCase(String cargo);
    
    /**
     * Búsqueda combinada por nombre O cargo usando consulta JPQL personalizada
     * Permite buscar en ambos campos simultáneamente con un solo término
     * 
     * @param termino Texto a buscar en nombre o cargo
     * @return Lista de empleados que contienen el término en nombre O cargo
     */
    @Query("SELECT e FROM Empleado e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(e.cargo) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Empleado> buscarPorNombreOCargo(@Param("termino") String termino);
    
    /**
     * Busca empleados por rango salarial
     * Útil para análisis y reportes de nómina
     * 
     * @param salarioMinimo Salario mínimo del rango
     * @param salarioMaximo Salario máximo del rango
     * @return Lista de empleados dentro del rango salarial
     */
    @Query("SELECT e FROM Empleado e WHERE e.salario BETWEEN :min AND :max ORDER BY e.salario DESC")
    List<Empleado> findByRangoSalarial(@Param("min") Double salarioMinimo, @Param("max") Double salarioMaximo);
}