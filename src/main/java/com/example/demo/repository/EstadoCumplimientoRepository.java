package com.example.demo.repository;

import com.example.demo.entity.EstadoCumplimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoCumplimientoRepository extends JpaRepository<EstadoCumplimiento, Integer> {
}
