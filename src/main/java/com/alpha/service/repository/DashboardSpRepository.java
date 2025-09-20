package com.alpha.service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Repository;

import java.sql.*;

/*
 * Created by: fkusu
 * Date: 8/10/2025
 */
@Repository
@RequiredArgsConstructor
public class DashboardSpRepository {
    private final JdbcTemplate jdbc;

    public SpResult callDashboard(String dateBy, Integer year, Date from, Date to, String marketing) {
        return jdbc.execute((ConnectionCallback<SpResult>) con -> {
            String sql = "{ call USP_DASHBOARD_BOOKING_ACCOUNT(?,?,?,?,?,?,?) }";

            try (CallableStatement cs = con.prepareCall(sql)) {
                // IN params
                if (dateBy == null || dateBy.isBlank()) cs.setNull(1, Types.VARCHAR); else cs.setString(1, dateBy);
                if (year == null) cs.setNull(2, Types.INTEGER); else cs.setInt(2, year);
                if (from == null) cs.setNull(3, Types.DATE);   else cs.setDate(3, new java.sql.Date(from.getTime()));
                if (to   == null) cs.setNull(4, Types.DATE);   else cs.setDate(4, new java.sql.Date(to.getTime()));
                if (marketing == null || marketing.isBlank()) cs.setNull(5, Types.VARCHAR); else cs.setString(5, marketing);

                // OUT params
                cs.registerOutParameter(6, Types.LONGVARCHAR); // p_resultJson (LONGTEXT)
                cs.registerOutParameter(7, Types.INTEGER);     // p_resultCode
                cs.registerOutParameter(8, Types.VARCHAR);     // p_message

                cs.execute();

                String json = cs.getString(6);
                int code    = cs.getInt(7);
                String msg  = cs.getString(8);

                return new SpResult(json, code, msg);
            }
        });
    }

    public record SpResult(String json, int code, String message) {}
}
