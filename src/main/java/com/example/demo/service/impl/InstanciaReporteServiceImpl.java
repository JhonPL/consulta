package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.InstanciaReporteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstanciaReporteServiceImpl implements InstanciaReporteService {

    private final InstanciaReporteRepository repository;
    private final ReporteRepository reporteRepo;

    public InstanciaReporteServiceImpl(InstanciaReporteRepository repository,
                                       ReporteRepository reporteRepo) {
        this.repository = repository;
        this.reporteRepo = reporteRepo;
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
    public InstanciaReporte crear(InstanciaReporte instancia) {
        return repository.save(instancia);
    }

    @Override
    public InstanciaReporte actualizar(Integer id, InstanciaReporte instancia) {
        InstanciaReporte existente = obtenerPorId(id);

        existente.setPeriodoReportado(instancia.getPeriodoReportado());
        existente.setFechaVencimientoCalculada(instancia.getFechaVencimientoCalculada());
        existente.setFechaEnvioReal(instancia.getFechaEnvioReal());
        existente.setEstado(instancia.getEstado());
        existente.setDiasDesviacion(instancia.getDiasDesviacion());
        existente.setLinkReporteFinal(instancia.getLinkReporteFinal());
        existente.setLinkEvidenciaEnvio(instancia.getLinkEvidenciaEnvio());
        existente.setObservaciones(instancia.getObservaciones());

        return repository.save(existente);
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
}
