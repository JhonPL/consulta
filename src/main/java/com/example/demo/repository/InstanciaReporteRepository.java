package com.example.demo.repository;

import com.example.demo.entity.InstanciaReporte;
import com.example.demo.entity.Reporte;
import com.example.demo.entity.EstadoCumplimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InstanciaReporteRepository extends JpaRepository<InstanciaReporte, Integer> {

    List<InstanciaReporte> findByReporte(Reporte reporte);

    List<InstanciaReporte> findByEstado(EstadoCumplimiento estado);

    List<InstanciaReporte> findByFechaVencimientoCalculadaBetween(LocalDate inicio, LocalDate fin);

    @Query("SELECT i FROM InstanciaReporte i WHERE i.fechaVencimientoCalculada <= :fecha AND i.estado.nombre != 'Enviado'")
    List<InstanciaReporte> findVencidos(@Param("fecha") LocalDate fecha);

    @Query("SELECT i FROM InstanciaReporte i WHERE i.fechaVencimientoCalculada BETWEEN :inicio AND :fin AND i.estado.nombre != 'Enviado'")
    List<InstanciaReporte> findProximosAVencer(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
