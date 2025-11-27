package com.example.demo.controller;

import com.example.demo.entity.NotificacionReporte;
import com.example.demo.service.NotificacionReporteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones-reporte")
@CrossOrigin
public class NotificacionReporteController {

    private final NotificacionReporteService service;

    public NotificacionReporteController(NotificacionReporteService service) {
        this.service = service;
    }

    @GetMapping("/{reporteId}")
    public List<NotificacionReporte> listar(@PathVariable String reporteId) {
        return service.listarPorReporte(reporteId);
    }

    @PostMapping
    public NotificacionReporte crear(@RequestBody NotificacionReporte notificacion) {
        return service.crear(notificacion);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
