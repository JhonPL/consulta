package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reportes")
@Data
public class Reporte {

    @Id
    @Column(name = "id_reporte", length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "entidad_id", nullable = false)
    private Entidad entidad;

    @Column(columnDefinition = "text", name = "base_legal")
    private String baseLegal;

    @Column(name = "fecha_inicio_vigencia")
    private java.time.LocalDate fechaInicioVigencia;

    @Column(name = "fecha_fin_vigencia")
    private java.time.LocalDate fechaFinVigencia;

    @ManyToOne
    @JoinColumn(name = "frecuencia_id", nullable = false)
    private Frecuencia frecuencia;

    @Column(name = "dia_vencimiento")
    private Integer diaVencimiento;

    @Column(name = "mes_vencimiento")
    private Integer mesVencimiento;

    @Column(name = "plazo_adicional_dias")
    private Integer plazoAdicionalDias;

    @Column(name = "formato_requerido", length = 100)
    private String formatoRequerido;

    @Column(name = "link_instrucciones", length = 255)
    private String linkInstrucciones;

    @ManyToOne
    @JoinColumn(name = "responsable_elaboracion_id", nullable = false)
    private Usuario responsableElaboracion;

    @ManyToOne
    @JoinColumn(name = "responsable_supervision_id", nullable = false)
    private Usuario responsableSupervision;

    private boolean activo = true;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;
}
