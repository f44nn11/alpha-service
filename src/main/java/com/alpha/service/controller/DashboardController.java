package com.alpha.service.controller;

import com.alpha.service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 8/10/2025
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) String dateBy,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "ALL") String marketing
    ) {
        Map<String, Object> body = service.getDashboard(
                dateBy,
                year,
                from != null ? Date.valueOf(from) : null,
                to   != null ? Date.valueOf(to)   : null,
                marketing
        );
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(DashboardService.DashboardSpException.class)
    public ResponseEntity<Map<String, Object>> handleSpError(DashboardService.DashboardSpException ex) {
        return ResponseEntity.status(500).body(Map.of(
                "resultCode", ex.code,
                "message", ex.getMessage()
        ));
    }
}
