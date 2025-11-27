package com.example.demo.controller;

import com.example.demo.entity.HistorialCambios;
import com.example.demo.service.HistorialCambiosService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin
public class HistorialCambiosController {

    private final HistorialCambiosService service;

    public HistorialCambiosController(HistorialCambiosService service) {
        this.service = service;
    }

    @GetMapping("/{tabla}/{registroId}")
    public List<HistorialCambios> listar(@PathVariable String tabla, @PathVariable String registroId) {
        return service.listarPorRegistro(tabla, registroId);
    }

    @PostMapping
    public HistorialCambios crear(@RequestBody HistorialCambios historial) {
        return service.crear(historial);
    }
}
