package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "entidades")
@Data
public class Entidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidad")
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nit;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "pagina_web", length = 255)
    private String paginaWeb;

    @Column(columnDefinition = "text")
    private String baseLegal;

    private boolean activo = true;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;
}
