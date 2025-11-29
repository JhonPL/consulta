package com.example.demo.service.impl;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.GeneradorInstanciasService;
import com.example.demo.service.ReporteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repository;
    private final EntidadRepository entidadRepo;
    private final FrecuenciaRepository frecuenciaRepo;
    private final GeneradorInstanciasService generadorInstancias;

    public ReporteServiceImpl(ReporteRepository repository,
                              EntidadRepository entidadRepo,
                              FrecuenciaRepository frecuenciaRepo,
                              GeneradorInstanciasService generadorInstancias) {
        this.repository = repository;
        this.entidadRepo = entidadRepo;
        this.frecuenciaRepo = frecuenciaRepo;
        this.generadorInstancias = generadorInstancias;
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
    @Transactional
    public Reporte crear(Reporte reporte) {
        // Guardar el reporte
        Reporte reporteGuardado = repository.save(reporte);
        
        // Generar instancias automáticamente para el año actual y siguiente
        if (reporteGuardado.isActivo()) {
            List<InstanciaReporte> instancias = generadorInstancias.generarInstanciasAnuales(reporteGuardado);
            System.out.println("✓ Se generaron " + instancias.size() + " instancias para el reporte " + reporteGuardado.getId());
        }
        
        return reporteGuardado;
    }

    @Override
    @Transactional
    public Reporte actualizar(String id, Reporte reporte) {
        Reporte existente = obtenerPorId(id);
        
        boolean cambioFrecuencia = !existente.getFrecuencia().getId().equals(reporte.getFrecuencia().getId());
        boolean cambioDia = !java.util.Objects.equals(existente.getDiaVencimiento(), reporte.getDiaVencimiento());
        boolean cambioMes = !java.util.Objects.equals(existente.getMesVencimiento(), reporte.getMesVencimiento());

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

        Reporte reporteActualizado = repository.save(existente);
        
        // Si cambió la frecuencia o las fechas de vencimiento, regenerar instancias futuras
        if (cambioFrecuencia || cambioDia || cambioMes) {
            System.out.println("⚠ Cambios en frecuencia/vencimiento detectados. Considere regenerar instancias.");
        }

        return reporteActualizado;
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
