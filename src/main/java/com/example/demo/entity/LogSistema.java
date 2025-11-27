package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "logs_sistema")
@Data
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(length = 50)
    private String modulo;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(length = 45)
    private String ip;

    @Column
    private java.time.LocalDateTime fecha = java.time.LocalDateTime.now();
}
