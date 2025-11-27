package com.example.demo.service;

import com.example.demo.entity.Alerta;
import com.example.demo.entity.InstanciaReporte;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String twilioWhatsAppNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        System.out.println("✓ Twilio WhatsApp inicializado correctamente");
    }

    /**
     * Envía notificación de alerta por WhatsApp
     */
    public void enviarNotificacionAlerta(Alerta alerta) {
        String telefono = alerta.getUsuarioDestino().getTelefono();
        
        if (telefono == null || telefono.isEmpty()) {
            System.out.println("⚠️ Usuario no tiene teléfono configurado: " + 
                             alerta.getUsuarioDestino().getNombreCompleto());
            return;
        }

        try {
            String mensaje = generarMensajeWhatsApp(alerta);
            enviarMensaje(telefono, mensaje);
            
            System.out.println("✓ WhatsApp enviado a: " + telefono);
        } catch (Exception e) {
            System.err.println("✗ Error al enviar WhatsApp: " + e.getMessage());
        }
    }

    /**
     * Envía notificación de cambio de estado por WhatsApp
     */
    public void enviarCambioEstado(InstanciaReporte instancia, String estadoAnterior, String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return;
        }

        try {
            String mensaje = String.format(
                "🔔 *Cambio de Estado - Llanogas*\n\n" +
                "📋 Reporte: %s\n" +
                "🏢 Entidad: %s\n" +
                "📅 Período: %s\n" +
                "⏰ Fecha Límite: %s\n\n" +
                "Estado: %s → %s\n\n" +
                "Accede al sistema para más detalles.",
                instancia.getReporte().getNombre(),
                instancia.getReporte().getEntidad().getRazonSocial(),
                instancia.getPeriodoReportado(),
                instancia.getFechaVencimientoCalculada(),
                estadoAnterior,
                instancia.getEstado().getNombre()
            );

            enviarMensaje(telefono, mensaje);
            System.out.println("✓ WhatsApp cambio estado enviado a: " + telefono);
        } catch (Exception e) {
            System.err.println("✗ Error al enviar WhatsApp: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje de WhatsApp genérico
     */
    public void enviarMensaje(String telefonoDestino, String mensaje) {
        try {
            // Asegurar formato correcto del teléfono
            String telefonoFormateado = formatearTelefono(telefonoDestino);
            
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + telefonoFormateado),
                    new PhoneNumber(twilioWhatsAppNumber),
                    mensaje
            ).create();

            System.out.println("✓ Mensaje WhatsApp enviado. SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("✗ Error enviando WhatsApp: " + e.getMessage());
            throw new RuntimeException("Error al enviar WhatsApp", e);
        }
    }

    /**
     * Genera el mensaje de WhatsApp para una alerta
     */
    private String generarMensajeWhatsApp(Alerta alerta) {
        InstanciaReporte instancia = alerta.getInstancia();
        String emoji = obtenerEmojiPorTipo(alerta.getTipo().getNombre());
        
        return String.format(
            "%s *%s - Llanogas*\n\n" +
            "Hola %s,\n\n" +
            "%s\n\n" +
            "📋 Reporte: %s\n" +
            "🏢 Entidad: %s\n" +
            "📅 Período: %s\n" +
            "⏰ Vence: %s\n" +
            "📊 Estado: %s\n\n" +
            "_Mensaje automático del Sistema de Seguimiento de Reportes_",
            emoji,
            alerta.getTipo().getNombre(),
            alerta.getUsuarioDestino().getNombreCompleto(),
            obtenerMensajeResumido(alerta),
            instancia.getReporte().getNombre(),
            instancia.getReporte().getEntidad().getRazonSocial(),
            instancia.getPeriodoReportado(),
            instancia.getFechaVencimientoCalculada(),
            instancia.getEstado().getNombre()
        );
    }

    private String obtenerMensajeResumido(Alerta alerta) {
        String tipoNombre = alerta.getTipo().getNombre().toUpperCase();
        int diasHasta = alerta.getInstancia().getFechaVencimientoCalculada()
                .compareTo(java.time.LocalDate.now());

        if (tipoNombre.contains("VENCIDO") || tipoNombre.contains("CRÍTICA")) {
            return "⚠️ *URGENTE:* Este reporte está VENCIDO. Envíe inmediatamente.";
        } else if (diasHasta <= 1) {
            return "🔶 *ATENCIÓN:* Vence MAÑANA. Complete hoy.";
        } else if (diasHasta <= 5) {
            return "🟡 Recordatorio: Vence en " + diasHasta + " días.";
        } else {
            return "🟢 Inicie la recolección de información.";
        }
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

    /**
     * Formatea el número de teléfono al formato internacional requerido por Twilio
     * Ejemplo: 3001234567 → +573001234567 (Colombia)
     */
    private String formatearTelefono(String telefono) {
        // Remover espacios y caracteres especiales
        telefono = telefono.replaceAll("[^0-9+]", "");
        
        // Si ya tiene +, devolverlo
        if (telefono.startsWith("+")) {
            return telefono;
        }
        
        // Si empieza con 57 (código Colombia), agregar +
        if (telefono.startsWith("57")) {
            return "+" + telefono;
        }
        
        // Si es número local (10 dígitos), agregar código de Colombia
        if (telefono.length() == 10) {
            return "+57" + telefono;
        }
        
        // Si tiene 12 dígitos sin +, agregar +
        if (telefono.length() == 12) {
            return "+" + telefono;
        }
        
        return telefono;
    }

    /**
     * Verifica si el servicio de WhatsApp está disponible
     */
    public boolean estaDisponible() {
        try {
            return accountSid != null && !accountSid.isEmpty() &&
                   authToken != null && !authToken.isEmpty() &&
                   twilioWhatsAppNumber != null && !twilioWhatsAppNumber.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}