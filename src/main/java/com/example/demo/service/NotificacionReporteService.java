package com.example.demo.service;

import com.example.demo.entity.NotificacionReporte;

import java.util.List;

public interface NotificacionReporteService {
    List<NotificacionReporte> listarPorReporte(String reporteId);
    NotificacionReporte crear(NotificacionReporte notificacion);
    void eliminar(Integer id);
}
