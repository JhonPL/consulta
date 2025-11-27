package com.example.demo.service.impl;

import com.example.demo.entity.HistorialCambios;
import com.example.demo.repository.HistorialCambiosRepository;
import com.example.demo.service.HistorialCambiosService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialCambiosServiceImpl implements HistorialCambiosService {

    private final HistorialCambiosRepository repository;

    public HistorialCambiosServiceImpl(HistorialCambiosRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<HistorialCambios> listarPorRegistro(String tabla, String registroId) {
        return repository.findByTablaAndRegistroId(tabla, registroId);
    }

    @Override
    public HistorialCambios crear(HistorialCambios historial) {
        return repository.save(historial);
    }
}
