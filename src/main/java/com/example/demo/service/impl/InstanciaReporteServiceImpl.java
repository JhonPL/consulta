package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.FechaVencimientoCalculator;
import com.example.demo.service.InstanciaReporteService;
import com.example.demo.service.NotificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InstanciaReporteServiceImpl implements InstanciaReporteService {

    private final InstanciaReporteRepository repository;
    private final ReporteRepository reporteRepo;
    private final FechaVencimientoCalculator fechaCalculator;
    private final NotificacionService notificacionService;

    public InstanciaReporteServiceImpl(InstanciaReporteRepository repository,
                                       ReporteRepository reporteRepo,
                                       FechaVencimientoCalculator fechaCalculator,
                                       NotificacionService notificacionService) {
        this.repository = repository;
        this.reporteRepo = reporteRepo;
        this.fechaCalculator = fechaCalculator;
        this.notificacionService = notificacionService;
    }

    @Override
    public List<InstanciaReporte> listar() {
        return repository.findAll();
    }

    @Override
    public InstanciaReporte obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instancia no encontrada"));
    }

    @Override
    @Transactional
    public InstanciaReporte crear(InstanciaReporte instancia) {
        // Calcular automáticamente la fecha de vencimiento
        LocalDate fechaVencimiento = fechaCalculator.calcularFechaVencimiento(
            instancia.getReporte(), 
            instancia.getPeriodoReportado()
        );
        instancia.setFechaVencimientoCalculada(fechaVencimiento);

        // Si no hay fecha de creación, establecerla
        if (instancia.getFechaCreacion() == null) {
            instancia.setFechaCreacion(LocalDateTime.now());
        }

        InstanciaReporte guardada = repository.save(instancia);
        
        System.out.println("✓ Instancia creada: " + guardada.getId() + 
                          " - Vence: " + fechaVencimiento);
        
        return guardada;
    }

    @Override
    @Transactional
    public InstanciaReporte actualizar(Integer id, InstanciaReporte instancia) {
        InstanciaReporte existente = obtenerPorId(id);
        String estadoAnterior = existente.getEstado().getNombre();

        // Actualizar campos
        existente.setPeriodoReportado(instancia.getPeriodoReportado());
        existente.setEstado(instancia.getEstado());
        existente.setLinkReporteFinal(instancia.getLinkReporteFinal());
        existente.setLinkEvidenciaEnvio(instancia.getLinkEvidenciaEnvio());
        existente.setObservaciones(instancia.getObservaciones());

        // Si se marca como enviado, registrar fecha real
        if (instancia.getFechaEnvioReal() != null) {
            existente.setFechaEnvioReal(instancia.getFechaEnvioReal());
            
            // Calcular días de desviación
            int diasDesviacion = fechaCalculator.calcularDiasDesviacion(
                instancia.getFechaEnvioReal().toLocalDate(),
                existente.getFechaVencimientoCalculada()
            );
            existente.setDiasDesviacion(diasDesviacion);
        }

        // Si cambió el período, recalcular fecha de vencimiento
        if (!existente.getPeriodoReportado().equals(instancia.getPeriodoReportado())) {
            LocalDate nuevaFecha = fechaCalculator.calcularFechaVencimiento(
                existente.getReporte(),
                instancia.getPeriodoReportado()
            );
            existente.setFechaVencimientoCalculada(nuevaFecha);
        }

        existente.setFechaActualizacion(LocalDateTime.now());

        InstanciaReporte actualizada = repository.save(existente);

        // Notificar cambio de estado
        if (!estadoAnterior.equals(instancia.getEstado().getNombre())) {
            notificacionService.enviarNotificacionCambioEstado(actualizada, estadoAnterior);
        }

        System.out.println("✓ Instancia actualizada: " + actualizada.getId());
        
        return actualizada;
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<InstanciaReporte> listarPorReporte(String reporteId) {
        Reporte r = reporteRepo.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        return repository.findByReporte(r);
    }

    /**
     * Método adicional: Crear múltiples instancias automáticamente
     * para un reporte en un rango de períodos
     */
    @Transactional
    public List<InstanciaReporte> crearInstanciasAutomaticas(
            String reporteId, 
            List<String> periodos, 
            Integer estadoInicialId) {
        
        Reporte reporte = reporteRepo.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        return periodos.stream()
                .map(periodo -> {
                    InstanciaReporte instancia = new InstanciaReporte();
                    instancia.setReporte(reporte);
                    instancia.setPeriodoReportado(periodo);
                    
                    // Estado inicial
                    EstadoCumplimiento estado = new EstadoCumplimiento();
                    estado.setId(estadoInicialId);
                    instancia.setEstado(estado);
                    
                    return crear(instancia);
                })
                .toList();
    }
}