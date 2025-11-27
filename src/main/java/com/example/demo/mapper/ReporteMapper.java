package com.example.demo.mapper;

import com.example.demo.dto.ReporteDTO;
import com.example.demo.entity.Reporte;
import org.springframework.stereotype.Component;

@Component
public class ReporteMapper {

    public ReporteDTO toDto(Reporte r) {
        if (r == null) return null;
        
        ReporteDTO dto = new ReporteDTO();
        dto.setId(r.getId());
        dto.setNombre(r.getNombre());
        dto.setEntidadId(r.getEntidad() != null ? r.getEntidad().getId() : null);
        dto.setEntidadNombre(r.getEntidad() != null ? r.getEntidad().getRazonSocial() : null);
        dto.setBaseLegal(r.getBaseLegal());
        dto.setFechaInicioVigencia(r.getFechaInicioVigencia());
        dto.setFechaFinVigencia(r.getFechaFinVigencia());
        dto.setFrecuenciaId(r.getFrecuencia() != null ? r.getFrecuencia().getId() : null);
        dto.setFrecuenciaNombre(r.getFrecuencia() != null ? r.getFrecuencia().getNombre() : null);
        dto.setDiaVencimiento(r.getDiaVencimiento());
        dto.setMesVencimiento(r.getMesVencimiento());
        dto.setPlazoAdicionalDias(r.getPlazoAdicionalDias());
        dto.setFormatoRequerido(r.getFormatoRequerido());
        dto.setLinkInstrucciones(r.getLinkInstrucciones());
        dto.setResponsableElaboracionId(r.getResponsableElaboracion() != null ? r.getResponsableElaboracion().getId() : null);
        dto.setResponsableElaboracionNombre(r.getResponsableElaboracion() != null ? r.getResponsableElaboracion().getNombreCompleto() : null);
        dto.setResponsableSupervisionId(r.getResponsableSupervision() != null ? r.getResponsableSupervision().getId() : null);
        dto.setResponsableSupervisionNombre(r.getResponsableSupervision() != null ? r.getResponsableSupervision().getNombreCompleto() : null);
        dto.setActivo(r.isActivo());
        
        return dto;
    }
}