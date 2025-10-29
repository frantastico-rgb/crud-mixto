package com.miAplicacion.demo.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Entity.Tarea;
import com.miAplicacion.demo.Repository.EmpleadoRepository;
import com.miAplicacion.demo.Repository.ProyectoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private ProyectoService proyectoService;

    @Test
    public void agregarActualizarEliminarTarea_flow() {
        // Preparar proyecto vacío
        Proyecto p = new Proyecto();
        p.setId("p1");
        p.setTareas(new ArrayList<>());

        when(proyectoRepository.findById("p1")).thenReturn(Optional.of(p));
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(inv -> inv.getArgument(0));

        // Agregar tarea
        Tarea t1 = new Tarea("Tarea 1", Tarea.EstadoTarea.PENDIENTE);
        Proyecto afterAdd = proyectoService.agregarTarea("p1", t1);
        assertNotNull(afterAdd);
        assertEquals(1, afterAdd.getTareas().size());
        assertEquals("Tarea 1", afterAdd.getTareas().get(0).getTitulo());

        // Actualizar tarea (marcar completada)
        Tarea updated = new Tarea("Tarea 1 - actualizada", Tarea.EstadoTarea.COMPLETADA);
        Proyecto afterUpdate = proyectoService.actualizarTarea("p1", 0, updated);
        assertNotNull(afterUpdate);
        assertEquals(1, afterUpdate.getTareas().size());
        assertEquals("Tarea 1 - actualizada", afterUpdate.getTareas().get(0).getTitulo());
        assertEquals(Tarea.EstadoTarea.COMPLETADA, afterUpdate.getTareas().get(0).getEstado());

        // Eliminar tarea
        Proyecto afterDelete = proyectoService.eliminarTarea("p1", 0);
        assertNotNull(afterDelete);
        assertEquals(0, afterDelete.getTareas().size());
    }

    @Test
    public void proyecto_getTareasCompletadas_countsCorrectly() {
        Proyecto p = new Proyecto();
        List<Tarea> tareas = new ArrayList<>();
        tareas.add(new Tarea("A", Tarea.EstadoTarea.COMPLETADA));
        tareas.add(new Tarea("B", Tarea.EstadoTarea.PENDIENTE));
        tareas.add(new Tarea("C", Tarea.EstadoTarea.COMPLETADA));
        p.setTareas(tareas);

        assertEquals(2, p.getTareasCompletadas());
    }
}
