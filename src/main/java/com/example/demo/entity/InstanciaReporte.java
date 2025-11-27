package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "instancias_reporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanciaReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instancia")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @Column(name = "periodo_reportado", nullable = false, length = 50)
    private String periodoReportado;

    @Column(name = "fecha_vencimiento_calculada")
    private LocalDate fechaVencimientoCalculada;

    @Column(name = "fecha_envio_real")
    private LocalDateTime fechaEnvioReal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoCumplimiento estado;

    @Column(name = "dias_desviacion")
    private Integer diasDesviacion;

    @Column(name = "link_reporte_final", length = 255)
    private String linkReporteFinal;

    @Column(name = "link_evidencia_envio", length = 255)
    private String linkEvidenciaEnvio;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
