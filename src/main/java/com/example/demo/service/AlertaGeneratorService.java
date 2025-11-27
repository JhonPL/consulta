package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AlertaGeneratorService {

    private final InstanciaReporteRepository instanciaRepo;
    private final TipoAlertaRepository tipoAlertaRepo;
    private final AlertaRepository alertaRepo;
    private final NotificacionService notificacionService;

    public AlertaGeneratorService(InstanciaReporteRepository instanciaRepo,
                                  TipoAlertaRepository tipoAlertaRepo,
                                  AlertaRepository alertaRepo,
                                  NotificacionService notificacionService) {
        this.instanciaRepo = instanciaRepo;
        this.tipoAlertaRepo = tipoAlertaRepo;
        this.alertaRepo = alertaRepo;
        this.notificacionService = notificacionService;
    }

    /**
     * Ejecuta cada día a las 8:00 AM para verificar y generar alertas
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void generarAlertasAutomaticas() {
        System.out.println("🔔 Iniciando generación de alertas automáticas...");

        List<InstanciaReporte> instanciasPendientes = instanciaRepo.findAll().stream()
                .filter(i -> !i.getEstado().getNombre().equalsIgnoreCase("Enviado") &&
                             !i.getEstado().getNombre().equalsIgnoreCase("Aprobado"))
                .toList();

        LocalDate hoy = LocalDate.now();

        for (InstanciaReporte instancia : instanciasPendientes) {
            LocalDate fechaVencimiento = instancia.getFechaVencimientoCalculada();
            long diasHastaVencimiento = ChronoUnit.DAYS.between(hoy, fechaVencimiento);

            // Obtener tipos de alerta
            List<TipoAlerta> tiposAlerta = tipoAlertaRepo.findAll();

            // Generar alertas según los días hasta el vencimiento
            for (TipoAlerta tipo : tiposAlerta) {
                boolean debeGenerarAlerta = false;

                if (!tipo.isEsPostVencimiento()) {
                    // Alertas previas al vencimiento
                    if (diasHastaVencimiento == tipo.getDiasAntesVencimiento()) {
                        debeGenerarAlerta = true;
                    }
                } else {
                    // Alertas post vencimiento (diarias)
                    if (diasHastaVencimiento < 0) {
                        debeGenerarAlerta = true;
                    }
                }

                if (debeGenerarAlerta) {
                    // Verificar si ya existe una alerta similar
                    boolean alertaExiste = alertaRepo.findByInstancia(instancia).stream()
                            .anyMatch(a -> a.getTipo().getId().equals(tipo.getId()) &&
                                         a.getFechaProgramada().toLocalDate().equals(hoy));

                    if (!alertaExiste) {
                        generarAlerta(instancia, tipo, diasHastaVencimiento);
                    }
                }
            }
        }

        System.out.println("✓ Generación de alertas completada");
    }

    /**
     * Genera y envía una alerta específica
     */
    private void generarAlerta(InstanciaReporte instancia, TipoAlerta tipo, long diasHastaVencimiento) {
        Usuario responsable = instancia.getReporte().getResponsableElaboracion();
        Usuario supervisor = instancia.getReporte().getResponsableSupervision();

        // Crear alerta para responsable
        Alerta alertaResponsable = crearAlerta(instancia, tipo, responsable, diasHastaVencimiento);
        alertaRepo.save(alertaResponsable);

        // Enviar notificación por correo
        notificacionService.enviarNotificacionAlerta(alertaResponsable);

        // Si es alerta crítica, notificar también al supervisor
        if (tipo.isEsPostVencimiento() || tipo.getDiasAntesVencimiento() <= 1) {
            Alerta alertaSupervisor = crearAlerta(instancia, tipo, supervisor, diasHastaVencimiento);
            alertaRepo.save(alertaSupervisor);
            notificacionService.enviarNotificacionAlerta(alertaSupervisor);
        }

        System.out.println("✓ Alerta generada: " + tipo.getNombre() + " para " + responsable.getNombreCompleto());
    }

    private Alerta crearAlerta(InstanciaReporte instancia, TipoAlerta tipo, Usuario destino, long diasHastaVencimiento) {
        Alerta alerta = new Alerta();
        alerta.setInstancia(instancia);
        alerta.setTipo(tipo);
        alerta.setUsuarioDestino(destino);
        alerta.setFechaProgramada(LocalDateTime.now());
        alerta.setEnviada(true);
        alerta.setFechaEnviada(LocalDateTime.now());
        alerta.setLeida(false);
        alerta.setMensaje(generarMensajeAlerta(instancia, tipo, diasHastaVencimiento));

        return alerta;
    }

    private String generarMensajeAlerta(InstanciaReporte instancia, TipoAlerta tipo, long diasHastaVencimiento) {
        String nombreReporte = instancia.getReporte().getNombre();
        String entidad = instancia.getReporte().getEntidad().getRazonSocial();
        String periodo = instancia.getPeriodoReportado();
        LocalDate fechaVencimiento = instancia.getFechaVencimientoCalculada();

        if (tipo.isEsPostVencimiento()) {
            return String.format(
                "🔴 ¡ALERTA CRÍTICA! El reporte '%s' para %s (Período: %s) está VENCIDO desde hace %d días. " +
                "Fecha límite: %s. Envíe de inmediato para evitar sanciones.",
                nombreReporte, entidad, periodo, Math.abs(diasHastaVencimiento), fechaVencimiento
            );
        } else if (tipo.getDiasAntesVencimiento() == 1) {
            return String.format(
                "🟠 ¡URGENTE! El reporte '%s' para %s (Período: %s) vence MAÑANA (%s). " +
                "Por favor, complete y envíe lo antes posible.",
                nombreReporte, entidad, periodo, fechaVencimiento
            );
        } else if (tipo.getDiasAntesVencimiento() <= 5) {
            return String.format(
                "🟡 ATENCIÓN: El reporte '%s' para %s (Período: %s) vence en %d días (%s). " +
                "Asegúrese de avanzar en la elaboración.",
                nombreReporte, entidad, periodo, diasHastaVencimiento, fechaVencimiento
            );
        } else {
            return String.format(
                "🟢 RECORDATORIO: El reporte '%s' para %s (Período: %s) vence en %d días (%s). " +
                "Inicie la recolección de información. Base legal: %s",
                nombreReporte, entidad, periodo, diasHastaVencimiento, fechaVencimiento,
                instancia.getReporte().getBaseLegal()
            );
        }
    }

    /**
     * Genera una alerta manual (útil para pruebas o casos especiales)
     */
    public void generarAlertaManual(Integer instanciaId, Integer tipoAlertaId) {
        InstanciaReporte instancia = instanciaRepo.findById(instanciaId)
                .orElseThrow(() -> new RuntimeException("Instancia no encontrada"));
        TipoAlerta tipo = tipoAlertaRepo.findById(tipoAlertaId)
                .orElseThrow(() -> new RuntimeException("Tipo de alerta no encontrado"));

        LocalDate hoy = LocalDate.now();
        long diasHastaVencimiento = ChronoUnit.DAYS.between(hoy, instancia.getFechaVencimientoCalculada());

        generarAlerta(instancia, tipo, diasHastaVencimiento);
    }
}