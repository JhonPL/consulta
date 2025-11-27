package com.example.demo.repository;

import com.example.demo.entity.Reporte;
import com.example.demo.entity.Entidad;
import com.example.demo.entity.Frecuencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, String> {

    List<Reporte> findByEntidad(Entidad entidad);

    List<Reporte> findByFrecuencia(Frecuencia frecuencia);
}
