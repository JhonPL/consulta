package com.example.demo.controller;

import com.example.demo.entity.Alerta;
import com.example.demo.service.AlertaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Alerta> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Alerta obtener(@PathVariable Integer id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public Alerta crear(@RequestBody Alerta alerta) {
        return service.crear(alerta);
    }

    @PutMapping("/{id}")
    public Alerta actualizar(@PathVariable Integer id, @RequestBody Alerta alerta) {
        return service.actualizar(id, alerta);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    @GetMapping("/instancia/{instanciaId}")
    public List<Alerta> porInstancia(@PathVariable Integer instanciaId) {
        return service.listarPorInstancia(instanciaId);
    }
}
