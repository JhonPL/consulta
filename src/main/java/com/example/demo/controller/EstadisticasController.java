package com.example.demo.controller;

import com.example.demo.dto.EstadisticasDTO;
import com.example.demo.service.EstadisticasService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin
public class EstadisticasController {

    private final EstadisticasService service;

    public EstadisticasController(EstadisticasService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public EstadisticasDTO obtenerDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(3);
        }
        if (fechaFin == null) {
            // Incluir hasta 3 meses en el futuro para ver reportes próximos
            fechaFin = LocalDate.now().plusMonths(3);
        }
        
        return service.obtenerEstadisticas(fechaInicio, fechaFin);
    }

    @GetMapping("/cumplimiento-por-entidad")
    public Map<String, Object> cumplimientoPorEntidad(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        return service.obtenerCumplimientoPorEntidad(fechaInicio, fechaFin);
    }

    @GetMapping("/cumplimiento-por-responsable")
    public Map<String, Object> cumplimientoPorResponsable(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        return service.obtenerCumplimientoPorResponsable(fechaInicio, fechaFin);
    }

    @GetMapping("/tendencia-historica")
    public Map<String, Object> tendenciaHistorica(
            @RequestParam(defaultValue = "6") int meses) {
        
        return service.obtenerTendenciaHistorica(meses);
    }

    @GetMapping("/distribucion-estados")
    public Map<String, Long> distribucionEstados() {
        return service.obtenerDistribucionEstados();
    }

    @GetMapping("/proximos-vencer")
    public Map<String, Object> proximosAVencer(
            @RequestParam(defaultValue = "7") int dias) {
        
        return service.obtenerProximosAVencer(dias);
    }

    @GetMapping("/vencidos")
    public Map<String, Object> reportesVencidos() {
        return service.obtenerReportesVencidos();
    }

    @GetMapping("/top-incumplimiento-entidades")
    public Map<String, Object> topIncumplimientoEntidades(
            @RequestParam(defaultValue = "5") int top) {
        
        return service.obtenerTopIncumplimientoEntidades(top);
    }

    @GetMapping("/top-incumplimiento-responsables")
    public Map<String, Object> topIncumplimientoResponsables(
            @RequestParam(defaultValue = "5") int top) {
        
        return service.obtenerTopIncumplimientoResponsables(top);
    }

    @GetMapping("/resumen-periodo")
    public Map<String, Object> resumenPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        return service.obtenerResumenPorPeriodo(fechaInicio, fechaFin);
    }
}