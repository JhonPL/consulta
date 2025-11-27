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

    /**
     * Dashboard principal con todas las métricas
     */
    @GetMapping("/dashboard")
    public EstadisticasDTO obtenerDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(3);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }
        
        return service.obtenerEstadisticas(fechaInicio, fechaFin);
    }

    /**
     * Cumplimiento por entidad
     */
    @GetMapping("/cumplimiento-por-entidad")
    public Map<String, Object> cumplimientoPorEntidad(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        return service.obtenerCumplimientoPorEntidad(fechaInicio, fechaFin);
    }

    /**
     * Cumplimiento por responsable
     */
    @GetMapping("/cumplimiento-por-responsable")
    public Map<String, Object> cumplimientoPorResponsable(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        return service.obtenerCumplimientoPorResponsable(fechaInicio, fechaFin);
    }

    /**
     * Tendencia histórica mensual
     */
    @GetMapping("/tendencia-historica")
    public Map<String, Object> tendenciaHistorica(
            @RequestParam(defaultValue = "6") int meses) {
        
        return service.obtenerTendenciaHistorica(meses);
    }

    /**
     * Distribución de estados
     */
    @GetMapping("/distribucion-estados")
    public Map<String, Long> distribucionEstados() {
        return service.obtenerDistribucionEstados();
    }

    /**
     * Reportes próximos a vencer
     */
    @GetMapping("/proximos-vencer")
    public Map<String, Object> proximosAVencer(
            @RequestParam(defaultValue = "7") int dias) {
        
        return service.obtenerProximosAVencer(dias);
    }

    /**
     * Reportes vencidos
     */
    @GetMapping("/vencidos")
    public Map<String, Object> reportesVencidos() {
        return service.obtenerReportesVencidos();
    }

    /**
     * Top entidades con mayor incumplimiento
     */
    @GetMapping("/top-incumplimiento-entidades")
    public Map<String, Object> topIncumplimientoEntidades(
            @RequestParam(defaultValue = "5") int top) {
        
        return service.obtenerTopIncumplimientoEntidades(top);
    }

    /**
     * Top responsables con mayor incumplimiento
     */
    @GetMapping("/top-incumplimiento-responsables")
    public Map<String, Object> topIncumplimientoResponsables(
            @RequestParam(defaultValue = "5") int top) {
        
        return service.obtenerTopIncumplimientoResponsables(top);
    }

    /**
     * Resumen por período
     */
    @GetMapping("/resumen-periodo")
    public Map<String, Object> resumenPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        return service.obtenerResumenPorPeriodo(fechaInicio, fechaFin);
    }
}