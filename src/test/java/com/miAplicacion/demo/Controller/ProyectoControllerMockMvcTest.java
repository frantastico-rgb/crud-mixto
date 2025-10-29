package com.miAplicacion.demo.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miAplicacion.demo.Entity.Proyecto;
import com.miAplicacion.demo.Entity.Tarea;
import com.miAplicacion.demo.Service.EmpleadoService;
import com.miAplicacion.demo.Service.ProyectoService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import org.mockito.ArgumentCaptor;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProyectoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProyectoControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProyectoService proyectoService;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    public void agregarTarea_acceptsLegacyEstadoString_andMapsToEnum() throws Exception {
        String proyectoId = "p1";
        Tarea tarea = new Tarea("Tarea legado", Tarea.EstadoTarea.EN_PROGRESO);
        Proyecto p = new Proyecto();
        p.setId(proyectoId);
        p.setTareas(new ArrayList<>());
        p.getTareas().add(tarea);

        when(proyectoService.agregarTarea(eq(proyectoId), any(Tarea.class))).thenReturn(p);

        // Enviamos estado en formato libre ("en proceso") que debe mapearse a EN_PROGRESO
        String json = "{\"titulo\":\"Tarea legado\", \"estado\": \"en proceso\"}";

        mockMvc.perform(post("/proyectos/" + proyectoId + "/tareas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tareas[0].titulo").value("Tarea legado"))
            .andExpect(jsonPath("$.tareas[0].estado").value("EN_PROGRESO"));
    }

    @Test
    public void actualizarTarea_endpoint_updates() throws Exception {
        String proyectoId = "p1";
        Tarea tarea = new Tarea("Tarea A", Tarea.EstadoTarea.PENDIENTE);
        Proyecto p = new Proyecto();
        p.setId(proyectoId);
        p.setTareas(new ArrayList<>());
        p.getTareas().add(tarea);

        when(proyectoService.actualizarTarea(eq(proyectoId), eq(0), any(Tarea.class))).thenReturn(p);

        String json = objectMapper.writeValueAsString(tarea);

        mockMvc.perform(post("/proyectos/" + proyectoId + "/tareas/0")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tareas[0].titulo").value("Tarea A"));
    }

    @Test
    public void eliminarTarea_endpoint_deletes() throws Exception {
        String proyectoId = "p1";
        Proyecto p = new Proyecto();
        p.setId(proyectoId);
        p.setTareas(new ArrayList<>());

        when(proyectoService.eliminarTarea(eq(proyectoId), eq(0))).thenReturn(p);

        mockMvc.perform(delete("/proyectos/" + proyectoId + "/tareas/0"))
            .andExpect(status().isOk());
    }

    @Test
    public void formBinding_updateProject_acceptsLegacyEstado_andServiceReceivesEnum() throws Exception {
        String proyectoId = "p2";

        when(proyectoService.actualizarProyecto(eq(proyectoId), any(Proyecto.class))).thenAnswer(inv -> inv.getArgument(1));

        mockMvc.perform(post("/proyectos/editar/" + proyectoId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nombre", "Proyecto X")
                .param("descripcion", "Desc")
                .param("tareas[0].titulo", "Tarea Form")
                .param("tareas[0].estado", "completo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/proyectos"));

        ArgumentCaptor<Proyecto> captor = ArgumentCaptor.forClass(Proyecto.class);
        verify(proyectoService).actualizarProyecto(eq(proyectoId), captor.capture());
        Proyecto passed = captor.getValue();
        assertEquals(1, passed.getTareas().size());
        Tarea t = passed.getTareas().get(0);
        assertEquals("Tarea Form", t.getTitulo());
        // legacy "completo" should map to COMPLETADA
        assertEquals(Tarea.EstadoTarea.COMPLETADA, t.getEstado());
    }
}
