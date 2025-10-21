package com.miAplicacion.demo.Service;

import com.miAplicacion.demo.Entity.Empleado;
import com.miAplicacion.demo.Repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleadoTest;

    @BeforeEach
    void setUp() {
        empleadoTest = new Empleado();
        empleadoTest.setId(1L);
        empleadoTest.setNombre("Juan Testing");
        empleadoTest.setCargo("QA Engineer");
        empleadoTest.setSalario(50000.0);
        empleadoTest.setEmail("juan.testing@empresa.com");
    }

    @Test
    void testObtenerTodosLosEmpleados() {
        // Given
        List<Empleado> empleados = Arrays.asList(empleadoTest);
        when(empleadoRepository.findAll()).thenReturn(empleados);

        // When
        List<Empleado> resultado = empleadoService.obtenerTodosLosEmpleados();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Juan Testing", resultado.get(0).getNombre());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerEmpleadoPorId() {
        // Given
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoTest));

        // When
        Optional<Empleado> resultado = empleadoService.obtenerEmpleadoPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Juan Testing", resultado.get().getNombre());
        verify(empleadoRepository, times(1)).findById(1L);
    }

    @Test
    void testCrearEmpleado() {
        // Given
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoTest);

        // When
        Empleado resultado = empleadoService.crearEmpleado(empleadoTest);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Testing", resultado.getNombre());
        assertEquals("QA Engineer", resultado.getCargo());
        verify(empleadoRepository, times(1)).save(empleadoTest);
    }

    @Test
    void testActualizarEmpleado() {
        // Given
        Empleado empleadoActualizado = new Empleado();
        empleadoActualizado.setNombre("Juan Updated");
        empleadoActualizado.setCargo("Senior QA");
        empleadoActualizado.setSalario(60000.0);
        empleadoActualizado.setEmail("juan.updated@empresa.com");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoTest));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoActualizado);

        // When
        Empleado resultado = empleadoService.actualizarEmpleado(1L, empleadoActualizado);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Updated", resultado.getNombre());
        assertEquals("Senior QA", resultado.getCargo());
        assertEquals(60000.0, resultado.getSalario());
        verify(empleadoRepository, times(1)).findById(1L);
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    void testEliminarEmpleado() {
        // Given
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoTest));

        // When
        boolean resultado = empleadoService.eliminarEmpleado(1L);

        // Then
        assertTrue(resultado);
        verify(empleadoRepository, times(1)).findById(1L);
        verify(empleadoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarEmpleadoNoExiste() {
        // Given
        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean resultado = empleadoService.eliminarEmpleado(999L);

        // Then
        assertFalse(resultado);
        verify(empleadoRepository, times(1)).findById(999L);
        verify(empleadoRepository, never()).deleteById(999L);
    }

    @Test
    void testBuscarEmpleadosPorTermino() {
        // Given
        List<Empleado> empleados = Arrays.asList(empleadoTest);
        when(empleadoRepository.findByNombreContainingIgnoreCaseOrCargoContainingIgnoreCase("Juan", "Juan"))
            .thenReturn(empleados);

        // When
        List<Empleado> resultado = empleadoService.buscarEmpleadosPorTermino("Juan");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Juan Testing", resultado.get(0).getNombre());
        verify(empleadoRepository, times(1))
            .findByNombreContainingIgnoreCaseOrCargoContainingIgnoreCase("Juan", "Juan");
    }
}