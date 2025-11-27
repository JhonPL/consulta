package com.example.demo.service;

import com.example.demo.dto.EstadisticasDTO;
import com.example.demo.entity.InstanciaReporte;
import com.example.demo.repository.AlertaRepository;
import com.example.demo.repository.InstanciaReporteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    private final InstanciaReporteRepository instanciaRepo;
    private final AlertaRepository alertaRepo;

    public EstadisticasService(InstanciaReporteRepository instanciaRepo,
                              AlertaRepository alertaRepo) {
        this.instanciaRepo = instanciaRepo;
        this.alertaRepo = alertaRepo;
    }

    public EstadisticasDTO obtenerEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<InstanciaReporte> instancias = instanciaRepo.findByFechaVencimientoCalculadaBetween(fechaInicio, fechaFin);

        EstadisticasDTO stats = new EstadisticasDTO();
        
        stats.setTotalObligaciones((long) instancias.size());
        
        long enviadosATiempo = instancias.stream()
                .filter(i -> i.getDiasDesviacion() != null && i.getDiasDesviacion() <= 0)
                .filter(i -> i.getEstado().getNombre().equalsIgnoreCase("Enviado") ||
                           i.getEstado().getNombre().equalsIgnoreCase("Aprobado"))
                .count();
        
        long enviadosTarde = instancias.stream()
                .filter(i -> i.getDiasDesviacion() != null && i.getDiasDesviacion() > 0)
                .filter(i -> i.getEstado().getNombre().equalsIgnoreCase("Enviado") ||
                           i.getEstado().getNombre().equalsIgnoreCase("Aprobado"))
                .count();
        
        long vencidos = instancias.stream()
                .filter(i -> LocalDate.now().isAfter(i.getFechaVencimientoCalculada()))
                .filter(i -> !i.getEstado().getNombre().equalsIgnoreCase("Enviado") &&
                           !i.getEstado().getNombre().equalsIgnoreCase("Aprobado"))
                .count();
        
        long pendientes = instancias.stream()
                .filter(i -> i.getEstado().getNombre().equalsIgnoreCase("Pendiente") ||
                           i.getEstado().getNombre().equalsIgnoreCase("En Proceso"))
                .count();
        
        stats.setTotalEnviadosATiempo(enviadosATiempo);
        stats.setTotalEnviadosTarde(enviadosTarde);
        stats.setTotalVencidos(vencidos);
        stats.setTotalPendientes(pendientes);
        
        if (stats.getTotalObligaciones() > 0) {
            stats.setPorcentajeCumplimientoATiempo(
                (enviadosATiempo * 100.0) / stats.getTotalObligaciones()
            );
        } else {
            stats.setPorcentajeCumplimientoATiempo(0.0);
        }
        
        double promedioRetraso = instancias.stream()
                .filter(i -> i.getDiasDesviacion() != null && i.getDiasDesviacion() > 0)
                .mapToInt(InstanciaReporte::getDiasDesviacion)
                .average()
                .orElse(0.0);
        stats.setDiasRetrasoPromedio(promedioRetraso);
        
        // Distribución por estado
        Map<String, Long> distribucion = instancias.stream()
                .collect(Collectors.groupingBy(
                    i -> i.getEstado().getNombre(),
                    Collectors.counting()
                ));
        stats.setDistribucionEstados(distribucion);
        
        // Alertas críticas
        long alertasCriticas = alertaRepo.findAll().stream()
                .filter(a -> !a.isLeida())
                .filter(a -> a.getTipo().isEsPostVencimiento())
                .count();
        stats.setAlertasCriticasActivas(alertasCriticas);
        
        // Próximos a vencer
        LocalDate hoy = LocalDate.now();
        long proximos7Dias = instancias.stream()
                .filter(i -> i.getFechaVencimientoCalculada().isAfter(hoy) &&
                           i.getFechaVencimientoCalculada().isBefore(hoy.plusDays(8)))
                .filter(i -> !i.getEstado().getNombre().equalsIgnoreCase("Enviado"))
                .count();
        stats.setReportesProximosVencer7Dias(proximos7Dias);
        
        long proximos3Dias = instancias.stream()
                .filter(i -> i.getFechaVencimientoCalculada().isAfter(hoy) &&
                           i.getFechaVencimientoCalculada().isBefore(hoy.plusDays(4)))
                .filter(i -> !i.getEstado().getNombre().equalsIgnoreCase("Enviado"))
                .count();
        stats.setReportesProximosVencer3Dias(proximos3Dias);
        
        return stats;
    }

    public Map<String, Object> obtenerCumplimientoPorEntidad(LocalDate fechaInicio, LocalDate fechaFin) {
        List<InstanciaReporte> instancias = filtrarPorFechas(fechaInicio, fechaFin);
        
        Map<String, Map<String, Long>> cumplimientoPorEntidad = instancias.stream()
                .collect(Collectors.groupingBy(
                    i -> i.getReporte().getEntidad().getRazonSocial(),
                    Collectors.groupingBy(
                        this::clasificarEstado,
                        Collectors.counting()
                    )
                ));
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cumplimientoPorEntidad", cumplimientoPorEntidad);
        return resultado;
    }

    public Map<String, Object> obtenerCumplimientoPorResponsable(LocalDate fechaInicio, LocalDate fechaFin) {
        List<InstanciaReporte> instancias = filtrarPorFechas(fechaInicio, fechaFin);
        
        Map<String, Map<String, Long>> cumplimientoPorResponsable = instancias.stream()
                .collect(Collectors.groupingBy(
                    i -> i.getReporte().getResponsableElaboracion().getNombreCompleto(),
                    Collectors.groupingBy(
                        this::clasificarEstado,
                        Collectors.counting()
                    )
                ));
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cumplimientoPorResponsable", cumplimientoPorResponsable);
        return resultado;
    }

    public Map<String, Object> obtenerTendenciaHistorica(int meses) {
        Map<String, Double> tendencia = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        
        for (int i = meses - 1; i >= 0; i--) {
            YearMonth mes = YearMonth.from(hoy.minusMonths(i));
            LocalDate inicioMes = mes.atDay(1);
            LocalDate finMes = mes.atEndOfMonth();
            
            List<InstanciaReporte> instanciasMes = instanciaRepo.findByFechaVencimientoCalculadaBetween(inicioMes, finMes);
            
            long total = instanciasMes.size();
            long aTiempo = instanciasMes.stream()
                    .filter(inst -> inst.getDiasDesviacion() != null && inst.getDiasDesviacion() <= 0)
                    .filter(inst -> inst.getEstado().getNombre().equalsIgnoreCase("Enviado"))
                    .count();
            
            double porcentaje = total > 0 ? (aTiempo * 100.0) / total : 0.0;
            tendencia.put(mes.format(DateTimeFormatter.ofPattern("yyyy-MM")), porcentaje);
        }
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("tendencia", tendencia);
        return resultado;
    }

    public Map<String, Long> obtenerDistribucionEstados() {
        return instanciaRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                    i -> i.getEstado().getNombre(),
                    Collectors.counting()
                ));
    }

    public Map<String, Object> obtenerProximosAVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        List<InstanciaReporte> proximos = instanciaRepo.findProximosAVencer(hoy, hoy.plusDays(dias));
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cantidad", proximos.size());
        resultado.put("reportes", proximos);
        return resultado;
    }

    public Map<String, Object> obtenerReportesVencidos() {
        LocalDate hoy = LocalDate.now();
        List<InstanciaReporte> vencidos = instanciaRepo.findVencidos(hoy);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cantidad", vencidos.size());
        resultado.put("reportes", vencidos);
        return resultado;
    }

    public Map<String, Object> obtenerTopIncumplimientoEntidades(int top) {
        Map<String, Long> incumplimientos = instanciaRepo.findAll().stream()
                .filter(i -> i.getDiasDesviacion() != null && i.getDiasDesviacion() > 0)
                .collect(Collectors.groupingBy(
                    i -> i.getReporte().getEntidad().getRazonSocial(),
                    Collectors.counting()
                ));
        
        List<Map.Entry<String, Long>> topList = incumplimientos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .toList();
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("top", topList);
        return resultado;
    }

    public Map<String, Object> obtenerTopIncumplimientoResponsables(int top) {
        Map<String, Long> incumplimientos = instanciaRepo.findAll().stream()
                .filter(i -> i.getDiasDesviacion() != null && i.getDiasDesviacion() > 0)
                .collect(Collectors.groupingBy(
                    i -> i.getReporte().getResponsableElaboracion().getNombreCompleto(),
                    Collectors.counting()
                ));
        
        List<Map.Entry<String, Long>> topList = incumplimientos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .toList();
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("top", topList);
        return resultado;
    }

    public Map<String, Object> obtenerResumenPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        EstadisticasDTO stats = obtenerEstadisticas(fechaInicio, fechaFin);
        
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("periodo", Map.of(
            "inicio", fechaInicio,
            "fin", fechaFin
        ));
        resumen.put("estadisticas", stats);
        
        return resumen;
    }

    private List<InstanciaReporte> filtrarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) fechaInicio = LocalDate.now().minusMonths(3);
        if (fechaFin == null) fechaFin = LocalDate.now();
        
        return instanciaRepo.findByFechaVencimientoCalculadaBetween(fechaInicio, fechaFin);
    }

    private String clasificarEstado(InstanciaReporte i) {
        if (i.getEstado().getNombre().equalsIgnoreCase("Enviado") &&
            i.getDiasDesviacion() != null && i.getDiasDesviacion() <= 0) {
            return "A Tiempo";
        } else if (i.getEstado().getNombre().equalsIgnoreCase("Enviado") &&
                  i.getDiasDesviacion() != null && i.getDiasDesviacion() > 0) {
            return "Tarde";
        } else if (LocalDate.now().isAfter(i.getFechaVencimientoCalculada())) {
            return "Vencido";
        } else {
            return "Pendiente";
        }
    }
}
