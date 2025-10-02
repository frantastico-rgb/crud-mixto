package com.miAplicacion.demo.Entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.math.BigDecimal;

@Entity
@Data
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String cargo;
    private BigDecimal salario;
    @Column(unique = true)
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
