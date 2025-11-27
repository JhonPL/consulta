package com.example.demo.controller;

import com.example.demo.entity.InstanciaReporte;
import com.example.demo.service.InstanciaReporteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instancias")
@CrossOrigin
public class InstanciaReporteController {

    private final InstanciaReporteService service;

    public InstanciaReporteController(InstanciaReporteService service) {
        this.service = service;
    }

    @GetMapping
    public List<InstanciaReporte> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public InstanciaReporte obtener(@PathVariable Integer id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public InstanciaReporte crear(@RequestBody InstanciaReporte instancia) {
        return service.crear(instancia);
    }

    @PutMapping("/{id}")
    public InstanciaReporte actualizar(@PathVariable Integer id, @RequestBody InstanciaReporte instancia) {
        return service.actualizar(id, instancia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    @GetMapping("/reporte/{reporteId}")
    public List<InstanciaReporte> porReporte(@PathVariable String reporteId) {
        return service.listarPorReporte(reporteId);
    }
}
