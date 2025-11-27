package com.example.demo.service.impl;

import com.example.demo.entity.Alerta;
import com.example.demo.entity.InstanciaReporte;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.AlertaRepository;
import com.example.demo.repository.InstanciaReporteRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.AlertaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertaServiceImpl implements AlertaService {

    private final AlertaRepository repository;
    private final InstanciaReporteRepository instanciaRepo;
    private final UsuarioRepository usuarioRepo;

    public AlertaServiceImpl(AlertaRepository repository,
                             InstanciaReporteRepository instanciaRepo,
                             UsuarioRepository usuarioRepo) {
        this.repository = repository;
        this.instanciaRepo = instanciaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public List<Alerta> listar() {
        return repository.findAll();
    }

    @Override
    public Alerta obtenerPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
    }

    @Override
    public Alerta crear(Alerta alerta) {
        return repository.save(alerta);
    }

    @Override
    public Alerta actualizar(Integer id, Alerta alerta) {
        Alerta existente = obtenerPorId(id);

        existente.setInstancia(alerta.getInstancia());
        existente.setTipo(alerta.getTipo());
        existente.setUsuarioDestino(alerta.getUsuarioDestino());
        existente.setFechaProgramada(alerta.getFechaProgramada());
        existente.setFechaEnviada(alerta.getFechaEnviada());
        existente.setEnviada(alerta.isEnviada());
        existente.setMensaje(alerta.getMensaje());
        existente.setLeida(alerta.isLeida());

        return repository.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Alerta> listarPorInstancia(Integer instanciaId) {
        InstanciaReporte instancia = instanciaRepo.findById(instanciaId)
                .orElseThrow(() -> new RuntimeException("Instancia no encontrada"));

        return repository.findByInstancia(instancia);
    }

    @Override
    public List<Alerta> listarPorUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return repository.findByUsuarioDestino(usuario);
    }

    @Override
    public List<Alerta> listarNoLeidas(Integer usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return repository.findByUsuarioDestinoAndLeidaFalse(usuario);
    }

    @Override
    public Alerta marcarComoLeida(Integer id) {
        Alerta alerta = obtenerPorId(id);
        alerta.setLeida(true);
        return repository.save(alerta);
    }
}
