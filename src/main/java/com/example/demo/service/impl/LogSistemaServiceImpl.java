package com.example.demo.service.impl;

import com.example.demo.entity.LogSistema;
import com.example.demo.repository.LogSistemaRepository;
import com.example.demo.service.LogSistemaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogSistemaServiceImpl implements LogSistemaService {

    private final LogSistemaRepository repository;

    public LogSistemaServiceImpl(LogSistemaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LogSistema> listar() {
        return repository.findAll();
    }

    @Override
    public LogSistema crear(LogSistema log) {
        return repository.save(log);
    }
}
