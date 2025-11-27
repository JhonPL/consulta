package com.example.demo.service;

import com.example.demo.entity.InstanciaReporte;

import java.util.List;

public interface InstanciaReporteService {
    List<InstanciaReporte> listar();
    InstanciaReporte obtenerPorId(Integer id);
    InstanciaReporte crear(InstanciaReporte instancia);
    InstanciaReporte actualizar(Integer id, InstanciaReporte instancia);
    void eliminar(Integer id);

    List<InstanciaReporte> listarPorReporte(String reporteId);
}
