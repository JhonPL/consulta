package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alertas")
@Data
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "instancia_reporte_id", nullable = false)
    private InstanciaReporte instancia;

    @ManyToOne
    @JoinColumn(name = "tipo_alerta_id", nullable = false)
    private TipoAlerta tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_destino_id", nullable = false)
    private Usuario usuarioDestino;

    @Column(name = "fecha_programada")
    private java.time.LocalDateTime fechaProgramada;

    @Column(name = "fecha_enviada")
    private java.time.LocalDateTime fechaEnviada;

    private boolean enviada;

    @Column(columnDefinition = "text")
    private String mensaje;

    private boolean leida;
}
