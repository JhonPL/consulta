package com.example.demo.service;

import com.example.demo.entity.LogSistema;

import java.util.List;

public interface LogSistemaService {
    List<LogSistema> listar();
    LogSistema crear(LogSistema log);
}
