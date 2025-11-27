package com.example.demo.repository;

import com.example.demo.entity.InstanciaReporte;
import com.example.demo.entity.Reporte;
import com.example.demo.entity.EstadoCumplimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanciaReporteRepository extends JpaRepository<InstanciaReporte, Integer> {

    List<InstanciaReporte> findByReporte(Reporte reporte);

    List<InstanciaReporte> findByEstado(EstadoCumplimiento estado);
}
