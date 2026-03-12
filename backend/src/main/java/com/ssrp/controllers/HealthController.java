package com.ssrp.controllers;

import com.ssrp.dto.HealthStatusDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping
    public ResponseEntity<HealthStatusDto> health() {
        HealthStatusDto dto = new HealthStatusDto(
                "UP",
                appName + " - iteration-0",
                "UNKNOWN",
                "UNKNOWN"
        );
        return ResponseEntity.ok(dto);
    }
}

