package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Esta configuración habilita el uso de @Scheduled en toda la aplicación
}