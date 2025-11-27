package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.ReporteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repository;
    private final EntidadRepository entidadRepo;
    private final FrecuenciaRepository frecuenciaRepo;

    public ReporteServiceImpl(ReporteRepository repository,
                              EntidadRepository entidadRepo,
                              FrecuenciaRepository frecuenciaRepo) {
        this.repository = repository;
        this.entidadRepo = entidadRepo;
        this.frecuenciaRepo = frecuenciaRepo;
    }

    @Override
    public List<Reporte> listar() {
        return repository.findAll();
    }

    @Override
    public Reporte obtenerPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    @Override
    public Reporte crear(Reporte reporte) {
        return repository.save(reporte);
    }

    @Override
    public Reporte actualizar(String id, Reporte reporte) {
        Reporte existente = obtenerPorId(id);

        existente.setNombre(reporte.getNombre());
        existente.setEntidad(reporte.getEntidad());
        existente.setBaseLegal(reporte.getBaseLegal());
        existente.setFechaInicioVigencia(reporte.getFechaInicioVigencia());
        existente.setFechaFinVigencia(reporte.getFechaFinVigencia());
        existente.setFrecuencia(reporte.getFrecuencia());
        existente.setDiaVencimiento(reporte.getDiaVencimiento());
        existente.setMesVencimiento(reporte.getMesVencimiento());
        existente.setPlazoAdicionalDias(reporte.getPlazoAdicionalDias());
        existente.setFormatoRequerido(reporte.getFormatoRequerido());
        existente.setLinkInstrucciones(reporte.getLinkInstrucciones());
        existente.setResponsableElaboracion(reporte.getResponsableElaboracion());
        existente.setResponsableSupervision(reporte.getResponsableSupervision());
        existente.setActivo(reporte.isActivo());

        return repository.save(existente);
    }

    @Override
    public void eliminar(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Reporte> listarPorEntidad(Integer entidadId) {
        Entidad e = entidadRepo.findById(entidadId)
                .orElseThrow(() -> new RuntimeException("Entidad no encontrada"));

        return repository.findByEntidad(e);
    }

    @Override
    public List<Reporte> listarPorFrecuencia(Integer frecuenciaId) {
        Frecuencia f = frecuenciaRepo.findById(frecuenciaId)
                .orElseThrow(() -> new RuntimeException("Frecuencia no encontrada"));

        return repository.findByFrecuencia(f);
    }
}
