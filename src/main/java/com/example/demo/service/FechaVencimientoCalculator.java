package com.example.demo.service;

import com.example.demo.entity.Reporte;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

@Service
public class FechaVencimientoCalculator {

    /**
     * Calcula la fecha de vencimiento para un reporte dado un período
     * @param reporte El reporte con su configuración de frecuencia
     * @param periodoReportado El período en formato "YYYY-MM" o "YYYY-QX" o "YYYY"
     * @return La fecha de vencimiento calculada
     */
    public LocalDate calcularFechaVencimiento(Reporte reporte, String periodoReportado) {
        String frecuencia = reporte.getFrecuencia().getNombre().toUpperCase();
        LocalDate fechaBase;

        switch (frecuencia) {
            case "MENSUAL":
                fechaBase = calcularVencimientoMensual(periodoReportado, reporte.getDiaVencimiento());
                break;
            case "BIMESTRAL":
                fechaBase = calcularVencimientoBimestral(periodoReportado, reporte.getDiaVencimiento());
                break;
            case "TRIMESTRAL":
                fechaBase = calcularVencimientoTrimestral(periodoReportado, reporte.getDiaVencimiento());
                break;
            case "SEMESTRAL":
                fechaBase = calcularVencimientoSemestral(periodoReportado, reporte.getDiaVencimiento());
                break;
            case "ANUAL":
                fechaBase = calcularVencimientoAnual(periodoReportado, reporte.getMesVencimiento(), reporte.getDiaVencimiento());
                break;
            case "SEMANAL":
                fechaBase = calcularVencimientoSemanal(periodoReportado, reporte.getDiaVencimiento());
                break;
            case "DIARIA":
                fechaBase = LocalDate.parse(periodoReportado).plusDays(1);
                break;
            default:
                throw new IllegalArgumentException("Frecuencia no soportada: " + frecuencia);
        }

        // Agregar plazo adicional si existe
        if (reporte.getPlazoAdicionalDias() != null && reporte.getPlazoAdicionalDias() > 0) {
            fechaBase = fechaBase.plusDays(reporte.getPlazoAdicionalDias());
        }

        return fechaBase;
    }

    private LocalDate calcularVencimientoMensual(String periodoReportado, Integer diaVencimiento) {
        // Formato esperado: "2025-03" para Marzo 2025
        YearMonth yearMonth = YearMonth.parse(periodoReportado);
        LocalDate finMes = yearMonth.atEndOfMonth();
        LocalDate mesVencimiento = yearMonth.plusMonths(1).atDay(Math.min(diaVencimiento, yearMonth.plusMonths(1).lengthOfMonth()));
        return mesVencimiento;
    }

    private LocalDate calcularVencimientoBimestral(String periodoReportado, Integer diaVencimiento) {
        // Formato esperado: "2025-B1" para primer bimestre 2025
        String[] partes = periodoReportado.split("-B");
        int year = Integer.parseInt(partes[0]);
        int bimestre = Integer.parseInt(partes[1]);
        
        // El bimestre termina 2 meses después de su inicio
        int mesInicio = (bimestre - 1) * 2 + 1;
        YearMonth ultimoMes = YearMonth.of(year, mesInicio + 1);
        
        return ultimoMes.plusMonths(1).atDay(Math.min(diaVencimiento, ultimoMes.plusMonths(1).lengthOfMonth()));
    }

    private LocalDate calcularVencimientoTrimestral(String periodoReportado, Integer diaVencimiento) {
        // Formato esperado: "2025-Q1" para primer trimestre 2025
        String[] partes = periodoReportado.split("-Q");
        int year = Integer.parseInt(partes[0]);
        int trimestre = Integer.parseInt(partes[1]);
        
        // El trimestre termina 3 meses después de su inicio
        int mesInicio = (trimestre - 1) * 3 + 1;
        YearMonth ultimoMes = YearMonth.of(year, mesInicio + 2);
        
        return ultimoMes.plusMonths(1).atDay(Math.min(diaVencimiento, ultimoMes.plusMonths(1).lengthOfMonth()));
    }

    private LocalDate calcularVencimientoSemestral(String periodoReportado, Integer diaVencimiento) {
        // Formato esperado: "2025-S1" para primer semestre 2025
        String[] partes = periodoReportado.split("-S");
        int year = Integer.parseInt(partes[0]);
        int semestre = Integer.parseInt(partes[1]);
        
        // El semestre termina 6 meses después de su inicio
        int mesInicio = (semestre - 1) * 6 + 1;
        YearMonth ultimoMes = YearMonth.of(year, mesInicio + 5);
        
        return ultimoMes.plusMonths(1).atDay(Math.min(diaVencimiento, ultimoMes.plusMonths(1).lengthOfMonth()));
    }

    private LocalDate calcularVencimientoAnual(String periodoReportado, Integer mesVencimiento, Integer diaVencimiento) {
        // Formato esperado: "2025" para año 2025
        int year = Integer.parseInt(periodoReportado);
        
        YearMonth yearMonth = YearMonth.of(year + 1, mesVencimiento);
        return yearMonth.atDay(Math.min(diaVencimiento, yearMonth.lengthOfMonth()));
    }

    private LocalDate calcularVencimientoSemanal(String periodoReportado, Integer diaVencimiento) {
        // Formato esperado: "2025-W12" para semana 12 de 2025
        LocalDate fechaPeriodo = LocalDate.parse(periodoReportado);
        return fechaPeriodo.plusDays(7).with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.of(diaVencimiento)));
    }

    /**
     * Calcula los días de desviación entre la fecha de envío y la fecha límite
     * @param fechaEnvio Fecha real de envío
     * @param fechaLimite Fecha límite calculada
     * @return Días de desviación (positivo = tarde, negativo = anticipado)
     */
    public int calcularDiasDesviacion(LocalDate fechaEnvio, LocalDate fechaLimite) {
        if (fechaEnvio == null || fechaLimite == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(fechaLimite, fechaEnvio);
    }
}