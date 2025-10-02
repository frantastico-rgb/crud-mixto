package com.miAplicacion.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import com.miAplicacion.demo.Entity.Empleado;
import com.miAplicacion.demo.Repository.EmpleadoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de empleados
 * Incluye validaciones, búsquedas y operaciones CRUD avanzadas
 */
@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    /**
     * Crea un nuevo empleado con validación de email único
     * @param empleado Datos del empleado a crear
     * @return Empleado creado con ID asignado
     * @throws RuntimeException si el email ya existe
     */
    public Empleado crearEmpleado(Empleado empleado) {
        // Validar que el email no exista antes de crear
        Optional<Empleado> empleadoExistente = empleadoRepository.findByEmail(empleado.getEmail());
        if (empleadoExistente.isPresent()) {
            throw new RuntimeException("Ya existe un empleado con el email: " + empleado.getEmail());
        }
        
        // Al crear, el ID debe ser nulo (será auto-generado por MySQL)
        empleado.setId(null);
        return empleadoRepository.save(empleado);
    }

    public List<Empleado> obtenerTodosEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> obtenerEmpleadoPorId(Long id) {
        return empleadoRepository.findById(id);
    }
    
    /**
     * Búsqueda inteligente de empleados por término general
     * Busca en nombre Y cargo simultáneamente
     * @param termino Texto a buscar (puede ser nombre o cargo)
     * @return Lista de empleados que coinciden con el término
     */
    public List<Empleado> buscarEmpleados(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return obtenerTodosEmpleados();
        }
        return empleadoRepository.buscarPorNombreOCargo(termino.trim());
    }
    
    /**
     * Búsqueda específica por nombre solamente
     * @param nombre Parte del nombre a buscar
     * @return Lista de empleados cuyo nombre contiene el texto
     */
    public List<Empleado> buscarPorNombre(String nombre) {
        return empleadoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    /**
     * Búsqueda específica por cargo solamente
     * @param cargo Parte del cargo a buscar
     * @return Lista de empleados cuyo cargo contiene el texto
     */
    public List<Empleado> buscarPorCargo(String cargo) {
        return empleadoRepository.findByCargoContainingIgnoreCase(cargo);
    }
    
    /**
     * Búsqueda por rango salarial
     * @param salarioMin Salario mínimo
     * @param salarioMax Salario máximo
     * @return Lista de empleados en el rango salarial especificado
     */
    public List<Empleado> buscarPorRangoSalarial(Double salarioMin, Double salarioMax) {
        return empleadoRepository.findByRangoSalarial(salarioMin, salarioMax);
    }

    /**
     * Actualiza un empleado existente con validación de email único
     * @param id ID del empleado a actualizar
     * @param empleadoDetalles Nuevos datos del empleado
     * @return Empleado actualizado
     * @throws RuntimeException si el ID no existe o el email ya está en uso
     */
    public Empleado actualizarEmpleado(Long id, Empleado empleadoDetalles) {
        
        // 1. Buscar el empleado existente por ID
        Optional<Empleado> empleadoExistente = empleadoRepository.findById(id);
        
        if (empleadoExistente.isPresent()) {
            Empleado empleado = empleadoExistente.get();
            
            // 2. Validar email único (solo si cambió el email)
            if (!empleado.getEmail().equals(empleadoDetalles.getEmail())) {
                Optional<Empleado> empleadoConEmail = empleadoRepository.findByEmail(empleadoDetalles.getEmail());
                if (empleadoConEmail.isPresent()) {
                    throw new RuntimeException("Ya existe un empleado con el email: " + empleadoDetalles.getEmail());
                }
            }
            
            // 3. Actualizar campos (preservando el ID original)
            empleado.setNombre(empleadoDetalles.getNombre());
            empleado.setCargo(empleadoDetalles.getCargo());
            empleado.setSalario(empleadoDetalles.getSalario());
            empleado.setEmail(empleadoDetalles.getEmail());
            
            // 4. Guardar y retornar el empleado actualizado
            return empleadoRepository.save(empleado);
        } else {
            throw new RuntimeException("Empleado no encontrado con ID: " + id);
        }
    }

    public void eliminarEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }
}