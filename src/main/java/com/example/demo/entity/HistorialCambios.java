package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "historial_cambios")
@Data
public class HistorialCambios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String tabla;

    @Column(name = "registro_id", nullable = false, length = 50)
    private String registroId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "campo_modificado", nullable = false, length = 100)
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "text")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "text")
    private String valorNuevo;

    @Column(name = "fecha_modificacion")
    private java.time.LocalDateTime fechaModificacion;
}
