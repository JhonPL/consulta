package com.example.demo.controller;

import com.example.demo.dto.EventoCalendarioDTO;
import com.example.demo.entity.InstanciaReporte;
import com.example.demo.service.CalendarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendario")
@CrossOrigin
public class CalendarioController {

    private final CalendarioService service;

    public CalendarioController(CalendarioService service) {
        this.service = service;
    }

    /**
     * Obtener eventos del calendario para un mes específico
     */
    @GetMapping("/eventos")
    public List<EventoCalendarioDTO> obtenerEventos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth mes,
            @RequestParam(required = false) Integer entidadId,
            @RequestParam(required = false) Integer responsableId,
            @RequestParam(required = false) String frecuencia,
            Authentication authentication) {
        
        return service.obtenerEventosCalendario(mes, entidadId, responsableId, frecuencia, authentication);
    }

    /**
     * Obtener vista de calendario personalizada según rol del usuario
     */
    @GetMapping("/mi-calendario")
    public List<EventoCalendarioDTO> obtenerMiCalendario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth mes,
            Authentication authentication) {
        
        return service.obtenerMiCalendario(mes, authentication);
    }

    /**
     * Obtener eventos del año completo (vista anual)
     */
    @GetMapping("/vista-anual")
    public Map<String, List<EventoCalendarioDTO>> obtenerVistaAnual(
            @RequestParam int year,
            Authentication authentication) {
        
        return service.obtenerVistaAnual(year, authentication);
    }

    /**
     * Búsqueda avanzada de instancias de reportes
     */
    @GetMapping("/buscar")
    public List<InstanciaReporte> buscarReportes(
            @RequestParam(required = false) Integer entidadId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String periodoReportado,
            @RequestParam(required = false) Integer estadoId,
            @RequestParam(required = false) Integer responsableElaboracionId,
            @RequestParam(required = false) Integer responsableSupervisionId,
            @RequestParam(required = false) String proceso,
            @RequestParam(required = false) String busquedaLibre) {
        
        return service.buscarReportesAvanzada(
            entidadId, fechaInicio, fechaFin, periodoReportado, estadoId,
            responsableElaboracionId, responsableSupervisionId, proceso, busquedaLibre
        );
    }

    /**
     * Exportar resultados de búsqueda (preparado para implementar)
     */
    @GetMapping("/exportar")
    public Map<String, Object> exportarResultados(
            @RequestParam(required = false) Integer entidadId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        // Implementación futura para exportar a Excel/PDF
        return Map.of("mensaje", "Funcionalidad de exportación en desarrollo");
    }
}