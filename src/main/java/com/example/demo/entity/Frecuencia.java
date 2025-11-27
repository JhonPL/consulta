package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "frecuencias")
@Data
public class Frecuencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frecuencia")
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
