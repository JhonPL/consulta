package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false, length = 100)
    private String proceso;

    @Column(nullable = false, length = 100)
    private String cargo;

    @Column(length = 30)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    private boolean activo = true;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;
}
