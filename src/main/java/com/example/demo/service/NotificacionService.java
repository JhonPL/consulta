package com.example.demo.service;

import com.example.demo.entity.Alerta;
import com.example.demo.entity.InstanciaReporte;
import com.example.demo.entity.NotificacionReporte;
import com.example.demo.repository.NotificacionReporteRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private final JavaMailSender mailSender;
    private final NotificacionReporteRepository notificacionRepo;

    public NotificacionService(JavaMailSender mailSender,
                              NotificacionReporteRepository notificacionRepo) {
        this.mailSender = mailSender;
        this.notificacionRepo = notificacionRepo;
    }

    /**
     * Envía notificación por correo cuando se genera una alerta
     */
    public void enviarNotificacionAlerta(Alerta alerta) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(alerta.getUsuarioDestino().getCorreo());
            mensaje.setSubject(generarAsuntoAlerta(alerta));
            mensaje.setText(generarCuerpoAlerta(alerta));
            mensaje.setFrom("reportes@llanogas.com");

            mailSender.send(mensaje);
            System.out.println("✓ Correo enviado a: " + alerta.getUsuarioDestino().getCorreo());

            // Enviar también a correos adicionales configurados en el reporte
            enviarCorreosAdicionales(alerta);

        } catch (Exception e) {
            System.err.println("✗ Error al enviar correo: " + e.getMessage());
            // No lanzar excepción para no interrumpir el proceso de alertas
        }
    }

    /**
     * Envía notificación cuando cambia el estado de una instancia
     */
    public void enviarNotificacionCambioEstado(InstanciaReporte instancia, String estadoAnterior) {
        try {
            String asunto = String.format(
                "Cambio de Estado: %s - %s",
                instancia.getReporte().getNombre(),
                instancia.getPeriodoReportado()
            );

            String cuerpo = String.format(
                "Hola,\n\n" +
                "Se ha actualizado el estado del reporte:\n\n" +
                "📋 Reporte: %s\n" +
                "🏢 Entidad: %s\n" +
                "📅 Período: %s\n" +
                "⏰ Fecha Límite: %s\n\n" +
                "Estado anterior: %s\n" +
                "Estado actual: %s\n\n" +
                "Accede al sistema para más detalles: http://localhost:3000/reportes/%s\n\n" +
                "---\n" +
                "Sistema de Seguimiento de Reportes - Llanogas",
                instancia.getReporte().getNombre(),
                instancia.getReporte().getEntidad().getRazonSocial(),
                instancia.getPeriodoReportado(),
                instancia.getFechaVencimientoCalculada(),
                estadoAnterior,
                instancia.getEstado().getNombre(),
                instancia.getId()
            );

            // Notificar al responsable de elaboración
            enviarCorreo(instancia.getReporte().getResponsableElaboracion().getCorreo(), asunto, cuerpo);

            // Notificar al supervisor
            enviarCorreo(instancia.getReporte().getResponsableSupervision().getCorreo(), asunto, cuerpo);

        } catch (Exception e) {
            System.err.println("✗ Error al enviar notificación de cambio de estado: " + e.getMessage());
        }
    }

    private void enviarCorreosAdicionales(Alerta alerta) {
        List<NotificacionReporte> notificaciones = notificacionRepo
                .findByReporte(alerta.getInstancia().getReporte());

        for (NotificacionReporte notif : notificaciones) {
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(notif.getCorreo());
                mensaje.setSubject(generarAsuntoAlerta(alerta));
                mensaje.setText(generarCuerpoAlerta(alerta));
                mensaje.setFrom("reportes@llanogas.com");

                mailSender.send(mensaje);
                System.out.println("✓ Correo adicional enviado a: " + notif.getCorreo());
            } catch (Exception e) {
                System.err.println("✗ Error al enviar correo adicional a " + notif.getCorreo());
            }
        }
    }

    private void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mensaje.setFrom("reportes@llanogas.com");

            mailSender.send(mensaje);
            System.out.println("✓ Correo enviado a: " + destinatario);
        } catch (Exception e) {
            System.err.println("✗ Error al enviar correo a " + destinatario);
        }
    }

    private String generarAsuntoAlerta(Alerta alerta) {
        String emoji = obtenerEmojiPorTipo(alerta.getTipo().getNombre());
        return String.format(
            "%s %s - %s",
            emoji,
            alerta.getTipo().getNombre(),
            alerta.getInstancia().getReporte().getNombre()
        );
    }

    private String generarCuerpoAlerta(Alerta alerta) {
        InstanciaReporte instancia = alerta.getInstancia();
        
        return String.format(
            "Hola %s,\n\n" +
            "%s\n\n" +
            "Detalles del Reporte:\n" +
            "📋 Nombre: %s\n" +
            "🏢 Entidad: %s\n" +
            "📅 Período: %s\n" +
            "⏰ Fecha Límite: %s\n" +
            "📊 Estado Actual: %s\n" +
            "⚖️ Base Legal: %s\n\n" +
            "Accede al sistema para gestionar este reporte: http://localhost:3000/reportes/%s\n\n" +
            "---\n" +
            "Sistema de Seguimiento de Reportes - Llanogas\n" +
            "Este es un mensaje automático, por favor no responder.",
            alerta.getUsuarioDestino().getNombreCompleto(),
            alerta.getMensaje(),
            instancia.getReporte().getNombre(),
            instancia.getReporte().getEntidad().getRazonSocial(),
            instancia.getPeriodoReportado(),
            instancia.getFechaVencimientoCalculada(),
            instancia.getEstado().getNombre(),
            instancia.getReporte().getBaseLegal(),
            instancia.getId()
        );
    }

    private String obtenerEmojiPorTipo(String tipoNombre) {
        if (tipoNombre.contains("Crítica") || tipoNombre.contains("Vencido")) {
            return "🔴";
        } else if (tipoNombre.contains("Urgente") || tipoNombre.contains("Riesgo")) {
            return "🟠";
        } else if (tipoNombre.contains("Seguimiento") || tipoNombre.contains("Intermedia")) {
            return "🟡";
        } else {
            return "🟢";
        }
    }
}