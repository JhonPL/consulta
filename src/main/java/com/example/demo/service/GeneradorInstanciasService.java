package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para generar instancias de reportes automáticamente
 * basándose en la frecuencia y configuración de vencimiento del reporte.
 */
@Service
public class GeneradorInstanciasService {

    private final InstanciaReporteRepository instanciaRepo;
    private final EstadoCumplimientoRepository estadoRepo;

    public GeneradorInstanciasService(InstanciaReporteRepository instanciaRepo,
                                      EstadoCumplimientoRepository estadoRepo) {
        this.instanciaRepo = instanciaRepo;
        this.estadoRepo = estadoRepo;
    }

    /**
     * Genera instancias para un reporte desde una fecha inicio hasta una fecha fin.
     * Se utiliza al crear un nuevo reporte.
     */
    @Transactional
    public List<InstanciaReporte> generarInstanciasParaReporte(Reporte reporte, LocalDate fechaInicio, LocalDate fechaFin) {
        List<InstanciaReporte> instancias = new ArrayList<>();
        
        // Obtener estado "Pendiente"
        EstadoCumplimiento estadoPendiente = estadoRepo.findByNombre("Pendiente")
                .orElseThrow(() -> new RuntimeException("Estado 'Pendiente' no encontrado"));

        String frecuencia = reporte.getFrecuencia().getNombre().toUpperCase();
        int diaVencimiento = reporte.getDiaVencimiento() != null ? reporte.getDiaVencimiento() : 15;
        int mesVencimiento = reporte.getMesVencimiento() != null ? reporte.getMesVencimiento() : 1;

        List<LocalDate> fechasVencimiento = calcularFechasVencimiento(
                frecuencia, diaVencimiento, mesVencimiento, fechaInicio, fechaFin
        );

        for (LocalDate fechaVenc : fechasVencimiento) {
            // Verificar que no exista ya una instancia para este periodo
            String periodo = calcularPeriodoReportado(frecuencia, fechaVenc);
            
            boolean existe = instanciaRepo.findByReporte(reporte).stream()
                    .anyMatch(i -> i.getPeriodoReportado().equals(periodo));
            
            if (!existe) {
                InstanciaReporte instancia = new InstanciaReporte();
                instancia.setReporte(reporte);
                instancia.setPeriodoReportado(periodo);
                instancia.setFechaVencimientoCalculada(fechaVenc);
                instancia.setEstado(estadoPendiente);
                instancia.setDiasDesviacion(0);
                
                instancias.add(instanciaRepo.save(instancia));
            }
        }

        return instancias;
    }

    /**
     * Genera instancias para el año actual y el siguiente.
     * Útil al crear un reporte nuevo.
     */
    @Transactional
    public List<InstanciaReporte> generarInstanciasAnuales(Reporte reporte) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.withDayOfMonth(1); // Primer día del mes actual
        LocalDate fin = hoy.plusYears(1).withMonth(12).withDayOfMonth(31); // Fin del año siguiente
        
        return generarInstanciasParaReporte(reporte, inicio, fin);
    }

    /**
     * Calcula las fechas de vencimiento según la frecuencia.
     */
    private List<LocalDate> calcularFechasVencimiento(String frecuencia, int dia, int mes, 
                                                       LocalDate inicio, LocalDate fin) {
        List<LocalDate> fechas = new ArrayList<>();

        switch (frecuencia) {
            case "MENSUAL":
                // Vence el mismo día de cada mes
                LocalDate fechaMensual = inicio.withDayOfMonth(Math.min(dia, inicio.lengthOfMonth()));
                if (fechaMensual.isBefore(inicio)) {
                    fechaMensual = fechaMensual.plusMonths(1);
                }
                while (!fechaMensual.isAfter(fin)) {
                    int diaAjustado = Math.min(dia, fechaMensual.lengthOfMonth());
                    fechas.add(fechaMensual.withDayOfMonth(diaAjustado));
                    fechaMensual = fechaMensual.plusMonths(1);
                }
                break;

            case "TRIMESTRAL":
                // Vence en el mes indicado de cada trimestre (mes 1, 2 o 3 del trimestre)
                // Trimestres: Ene-Mar, Abr-Jun, Jul-Sep, Oct-Dic
                int[] iniciosTrimestre = {1, 4, 7, 10}; // Meses de inicio de cada trimestre
                for (int year = inicio.getYear(); year <= fin.getYear(); year++) {
                    for (int inicioTrim : iniciosTrimestre) {
                        int mesReal = inicioTrim + (mes - 1); // mes 1 = primer mes del trimestre
                        if (mesReal > 12) continue;
                        
                        YearMonth ym = YearMonth.of(year, mesReal);
                        int diaAjustado = Math.min(dia, ym.lengthOfMonth());
                        LocalDate fecha = LocalDate.of(year, mesReal, diaAjustado);
                        
                        if (!fecha.isBefore(inicio) && !fecha.isAfter(fin)) {
                            fechas.add(fecha);
                        }
                    }
                }
                break;

            case "SEMESTRAL":
                // Vence en el mes indicado de cada semestre (mes 1-6)
                // Semestres: Ene-Jun, Jul-Dic
                int[] iniciosSemestre = {1, 7}; // Meses de inicio de cada semestre
                for (int year = inicio.getYear(); year <= fin.getYear(); year++) {
                    for (int inicioSem : iniciosSemestre) {
                        int mesReal = inicioSem + (mes - 1);
                        if (mesReal > 12) continue;
                        
                        YearMonth ym = YearMonth.of(year, mesReal);
                        int diaAjustado = Math.min(dia, ym.lengthOfMonth());
                        LocalDate fecha = LocalDate.of(year, mesReal, diaAjustado);
                        
                        if (!fecha.isBefore(inicio) && !fecha.isAfter(fin)) {
                            fechas.add(fecha);
                        }
                    }
                }
                break;

            case "ANUAL":
                // Vence una vez al año en el mes y día indicados
                for (int year = inicio.getYear(); year <= fin.getYear(); year++) {
                    YearMonth ym = YearMonth.of(year, mes);
                    int diaAjustado = Math.min(dia, ym.lengthOfMonth());
                    LocalDate fecha = LocalDate.of(year, mes, diaAjustado);
                    
                    if (!fecha.isBefore(inicio) && !fecha.isAfter(fin)) {
                        fechas.add(fecha);
                    }
                }
                break;

            case "SEMANAL":
                // Vence cada semana (día de la semana = dia del 1-7)
                LocalDate fechaSemanal = inicio;
                while (!fechaSemanal.isAfter(fin)) {
                    fechas.add(fechaSemanal);
                    fechaSemanal = fechaSemanal.plusWeeks(1);
                }
                break;

            case "ÚNICA VEZ":
                // Solo una vez, usar la fecha de inicio de vigencia o el mes/día indicado
                YearMonth ymUnica = YearMonth.of(inicio.getYear(), mes);
                int diaUnico = Math.min(dia, ymUnica.lengthOfMonth());
                LocalDate fechaUnica = LocalDate.of(inicio.getYear(), mes, diaUnico);
                if (!fechaUnica.isBefore(inicio) && !fechaUnica.isAfter(fin)) {
                    fechas.add(fechaUnica);
                }
                break;

            default:
                // Por defecto, mensual
                LocalDate fechaDefault = inicio.withDayOfMonth(Math.min(dia, inicio.lengthOfMonth()));
                while (!fechaDefault.isAfter(fin)) {
                    fechas.add(fechaDefault);
                    fechaDefault = fechaDefault.plusMonths(1);
                }
        }

        return fechas;
    }

    /**
     * Calcula el periodo reportado basado en la frecuencia y fecha.
     */
    private String calcularPeriodoReportado(String frecuencia, LocalDate fecha) {
        int year = fecha.getYear();
        int month = fecha.getMonthValue();

        switch (frecuencia) {
            case "MENSUAL":
                return String.format("%d-%02d", year, month);
            
            case "TRIMESTRAL":
                int trimestre = ((month - 1) / 3) + 1;
                return String.format("%d-T%d", year, trimestre);
            
            case "SEMESTRAL":
                int semestre = month <= 6 ? 1 : 2;
                return String.format("%d-S%d", year, semestre);
            
            case "ANUAL":
                return String.valueOf(year);
            
            case "SEMANAL":
                int semana = fecha.getDayOfYear() / 7 + 1;
                return String.format("%d-W%02d", year, semana);
            
            case "ÚNICA VEZ":
                return String.format("UNICO-%d", year);
            
            default:
                return String.format("%d-%02d", year, month);
        }
    }
}
