package com.example.demo.controller;

import com.example.demo.service.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para probar las notificaciones
 * SOLO PARA DESARROLLO - Eliminar o proteger en producción
 */
@RestController
@RequestMapping("/api/test/notificaciones")
@CrossOrigin
public class NotificacionesTestController {

    private final JavaMailSender mailSender;
    private final WhatsAppService whatsAppService;

    public NotificacionesTestController(JavaMailSender mailSender,
                                       WhatsAppService whatsAppService) {
        this.mailSender = mailSender;
        this.whatsAppService = whatsAppService;
    }

    /**
     * Probar envío de email
     * POST /api/test/notificaciones/email
     * Body: {"destinatario": "usuario@llanogas.com", "mensaje": "Prueba"}
     */
    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> probarEmail(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String destinatario = request.get("destinatario");
            String mensaje = request.get("mensaje");

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject("🧪 Prueba de Notificaciones - Llanogas");
            email.setText("Este es un mensaje de prueba del sistema.\n\n" + mensaje);
            email.setFrom("reportes@llanogas.com");

            mailSender.send(email);

            response.put("exito", true);
            response.put("mensaje", "Email enviado correctamente a " + destinatario);
            response.put("canal", "EMAIL");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("exito", false);
            response.put("error", e.getMessage());
            response.put("canal", "EMAIL");
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Probar envío de WhatsApp
     * POST /api/test/notificaciones/whatsapp
     * Body: {"telefono": "+573001234567", "mensaje": "Prueba"}
     */
    @PostMapping("/whatsapp")
    public ResponseEntity<Map<String, Object>> probarWhatsApp(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String telefono = request.get("telefono");
            String mensaje = request.get("mensaje");

            if (!whatsAppService.estaDisponible()) {
                response.put("exito", false);
                response.put("error", "Servicio WhatsApp no está configurado");
                response.put("canal", "WHATSAPP");
                return ResponseEntity.status(503).body(response);
            }

            String mensajeCompleto = String.format(
                "🧪 *Prueba de Notificaciones - Llanogas*\n\n%s\n\n" +
                "_Este es un mensaje de prueba del sistema_",
                mensaje
            );

            whatsAppService.enviarMensaje(telefono, mensajeCompleto);

            response.put("exito", true);
            response.put("mensaje", "WhatsApp enviado correctamente a " + telefono);
            response.put("canal", "WHATSAPP");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("exito", false);
            response.put("error", e.getMessage());
            response.put("canal", "WHATSAPP");
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Verificar estado de los servicios de notificaciones
     * GET /api/test/notificaciones/estado
     */
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> verificarEstado() {
        Map<String, Object> response = new HashMap<>();
        
        // Verificar Email
        boolean emailDisponible = false;
        try {
            mailSender.createMimeMessage();
            emailDisponible = true;
        } catch (Exception e) {
            response.put("emailError", e.getMessage());
        }
        
        // Verificar WhatsApp
        boolean whatsappDisponible = whatsAppService.estaDisponible();
        
        response.put("email", Map.of(
            "disponible", emailDisponible,
            "estado", emailDisponible ? "ACTIVO" : "ERROR"
        ));
        
        response.put("whatsapp", Map.of(
            "disponible", whatsappDisponible,
            "estado", whatsappDisponible ? "ACTIVO" : "NO CONFIGURADO"
        ));
        
        response.put("sistema", "Sistema de Notificaciones Dual");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Probar notificación dual (Email + WhatsApp)
     * POST /api/test/notificaciones/dual
     * Body: {
     *   "email": "usuario@llanogas.com",
     *   "telefono": "+573001234567",
     *   "mensaje": "Prueba dual"
     * }
     */
    @PostMapping("/dual")
    public ResponseEntity<Map<String, Object>> probarDual(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> resultados = new HashMap<>();
        
        String email = request.get("email");
        String telefono = request.get("telefono");
        String mensaje = request.get("mensaje");
        
        // Enviar Email
        boolean emailExito = false;
        try {
            SimpleMailMessage emailMsg = new SimpleMailMessage();
            emailMsg.setTo(email);
            emailMsg.setSubject("🧪 Prueba Dual - Llanogas");
            emailMsg.setText("Mensaje de prueba dual.\n\n" + mensaje);
            emailMsg.setFrom("reportes@llanogas.com");
            mailSender.send(emailMsg);
            emailExito = true;
            resultados.put("email", "✓ Enviado correctamente");
        } catch (Exception e) {
            resultados.put("email", "✗ Error: " + e.getMessage());
        }
        
        // Enviar WhatsApp
        boolean whatsappExito = false;
        try {
            if (whatsAppService.estaDisponible()) {
                whatsAppService.enviarMensaje(telefono, 
                    "🧪 *Prueba Dual - Llanogas*\n\n" + mensaje);
                whatsappExito = true;
                resultados.put("whatsapp", "✓ Enviado correctamente");
            } else {
                resultados.put("whatsapp", "⚠️ No configurado");
            }
        } catch (Exception e) {
            resultados.put("whatsapp", "✗ Error: " + e.getMessage());
        }
        
        response.put("resultados", resultados);
        response.put("exitoTotal", emailExito && whatsappExito);
        response.put("destinatarios", Map.of(
            "email", email,
            "telefono", telefono
        ));
        
        return ResponseEntity.ok(response);
    }
}