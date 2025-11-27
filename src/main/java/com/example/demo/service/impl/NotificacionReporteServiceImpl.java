package com.example.demo.service.impl;

import com.example.demo.entity.NotificacionReporte;
import com.example.demo.entity.Reporte;
import com.example.demo.repository.NotificacionReporteRepository;
import com.example.demo.repository.ReporteRepository;
import com.example.demo.service.NotificacionReporteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionReporteServiceImpl implements NotificacionReporteService {

    private final NotificacionReporteRepository repository;
    private final ReporteRepository reporteRepo;

    public NotificacionReporteServiceImpl(NotificacionReporteRepository repository,
                                          ReporteRepository reporteRepo) {
        this.repository = repository;
        this.reporteRepo = reporteRepo;
    }

    @Override
    public List<NotificacionReporte> listarPorReporte(String reporteId) {
        Reporte r = reporteRepo.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        return repository.findByReporte(r);
    }

    @Override
    public NotificacionReporte crear(NotificacionReporte notificacion) {
        return repository.save(notificacion);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
}
