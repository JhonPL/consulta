package com.example.demo.controller;

import com.example.demo.entity.LogSistema;
import com.example.demo.service.LogSistemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin
public class LogSistemaController {

    private final LogSistemaService service;

    public LogSistemaController(LogSistemaService service) {
        this.service = service;
    }

    @GetMapping
    public List<LogSistema> listar() {
        return service.listar();
    }

    @PostMapping
    public LogSistema crear(@RequestBody LogSistema log) {
        return service.crear(log);
    }
}
