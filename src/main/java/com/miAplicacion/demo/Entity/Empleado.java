package com.miAplicacion.demo.Entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 👤 **EMPLEADO ENTITY** - Modelo de datos principal
 * 
 * **CARACTERÍSTICAS PARA INTEGRACIÓN:**
 * ✅ ID auto-generado (MySQL IDENTITY)
 * ✅ Email único (constraint de BD)
 * ✅ Salario con precisión decimal
 * ✅ Validaciones integradas
 * ✅ JSON serializable para APIs REST
 */
@Entity
@Data
@Schema(
    name = "Empleado",
    description = "👤 **Modelo de empleado** - Entidad principal del sistema de RRHH"
)
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "🔑 ID único del empleado (auto-generado)",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;
    
    @Schema(
        description = "👤 Nombre completo del empleado",
        example = "Juan Pérez García",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minLength = 2,
        maxLength = 100
    )
    private String nombre;
    
    @Schema(
        description = "💼 Cargo o posición del empleado",
        example = "Senior Developer",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = {"Developer", "QA Engineer", "DevOps", "Project Manager", "Designer", "Analyst"}
    )
    private String cargo;
    
    @Schema(
        description = "💰 Salario mensual del empleado",
        example = "55000.00",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0"
    )
    private BigDecimal salario;
    
    @Column(unique = true)
    @Schema(
        description = "📧 Email corporativo único del empleado",
        example = "juan.perez@empresa.com",
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "email"
    )
    private String email;
    
    // Getters y setters explícitos para compatibilidad
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
