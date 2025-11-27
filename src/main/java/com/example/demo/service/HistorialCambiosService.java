package com.example.demo.service;

import com.example.demo.entity.HistorialCambios;

import java.util.List;

public interface HistorialCambiosService {
    List<HistorialCambios> listarPorRegistro(String tabla, String registroId);
    HistorialCambios crear(HistorialCambios historial);
}
