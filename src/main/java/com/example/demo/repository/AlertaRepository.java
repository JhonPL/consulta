package com.example.demo.repository;

import com.example.demo.entity.Alerta;
import com.example.demo.entity.InstanciaReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    List<Alerta> findByInstancia(InstanciaReporte instancia);
}
