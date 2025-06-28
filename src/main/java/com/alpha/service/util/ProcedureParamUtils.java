package com.alpha.service.util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/*
 * Created by: fkusu
 * Date: 5/10/2025
 */
public class ProcedureParamUtils {
    public static Set<String> getValidParams(Connection connection, String dbName, String procedureName) throws SQLException {
        Set<String> paramNames = new HashSet<>();

        String sql = "SELECT PARAMETER_NAME " +
                "FROM information_schema.parameters " +
                "WHERE SPECIFIC_NAME = ? " +
                "AND SPECIFIC_SCHEMA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, procedureName);
            stmt.setString(2, dbName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String param = rs.getString("PARAMETER_NAME");
                    if (param != null) {
                        paramNames.add(param.toLowerCase()); // atau pakai .toUpperCase() tergantung case
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return paramNames;
    }
}
