package com.alpha.service.service;

import com.alpha.service.repository.DashboardSpRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 8/10/2025
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardSpRepository repo;
    private final ObjectMapper objectMapper;

    public Map<String, Object> getDashboard(String dateBy, Integer year, Date from, Date to, String marketing) {
        var res = repo.callDashboard(dateBy, year, from, to, marketing);

        if (res.code() != 200) {
            throw new DashboardSpException(res.code(), res.message());
        }
        try {
            // p_resultJson sudah JSON lengkap dari SP
            return objectMapper.readValue(res.json(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse SP JSON result", e);
        }
    }

    public static class DashboardSpException extends RuntimeException {
        public final int code;
        public DashboardSpException(int code, String msg) { super(msg); this.code = code; }
    }
}
