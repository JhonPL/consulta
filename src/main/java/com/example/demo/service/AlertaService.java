package com.example.demo.service;

import com.example.demo.entity.Alerta;

import java.util.List;

public interface AlertaService {
    List<Alerta> listar();
    Alerta obtenerPorId(Integer id);
    Alerta crear(Alerta alerta);
    Alerta actualizar(Integer id, Alerta alerta);
    void eliminar(Integer id);

    List<Alerta> listarPorInstancia(Integer instanciaId);
}
