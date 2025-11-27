package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "instancias_reporte")
@Data
public class InstanciaReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instancia")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Column(name = "periodo_reportado", nullable = false, length = 50)
    private String periodoReportado;

    @Column(name = "fecha_vencimiento_calculada")
    private java.time.LocalDate fechaVencimientoCalculada;

    @Column(name = "fecha_envio_real")
    private java.time.LocalDateTime fechaEnvioReal;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoCumplimiento estado;

    @Column(name = "dias_desviacion")
    private Integer diasDesviacion;

    @Column(name = "link_reporte_final", length = 255)
    private String linkReporteFinal;

    @Column(name = "link_evidencia_envio", length = 255)
    private String linkEvidenciaEnvio;

    @Column(columnDefinition = "text")
    private String observaciones;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;
}
