package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketsRunner {
    public static void main(String[] args) {
//        Long flightId = 45000L;
//        System.out.println(getTicketsId(flightId));
//        List<Long> result = getFlightsIdBetween(
//                LocalDateTime.of(2023, 12, 11, 3, 4),
//                LocalDateTime.of(2023, 12, 13, 12, 30)
//        );
//        System.out.println(result);
        showMetaInf();
    }

    private static void showMetaInf() {
        try (Connection con = PgConnectionManager.open()) {
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet tableRs = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
            StringBuilder sb = new StringBuilder();
            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                sb.append(tableName).append("\n");
                ResultSet columnRs = metaData.getColumns(null, "public", tableName, "%");
                while (columnRs.next()) {
                    sb
                            .append("\t")
                            .append(columnRs.getString("COLUMN_NAME"))
                            .append(" [")
                            .append(columnRs.getString("TYPE_NAME"))
                            .append("]\n");
                }
            }
            System.out.println(sb);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Long> getFlightsIdBetween(LocalDateTime start, LocalDateTime end) {
        String sql = """
                SELECT id
                FROM flight
                WHERE departure_date BETWEEN ? AND ?;
                """;
        List<Long> result = new ArrayList<>();
        try (Connection con = PgConnectionManager.open();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setFetchSize(50);
            stmt.setQueryTimeout(10);
            stmt.setMaxRows(10000);
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static List<Long> getTicketsId(Long flight_id) {
        String sql = """
                SELECT id
                FROM ticket
                WHERE flight_id = ?;
                """;
        List<Long> result = new ArrayList<>();
        try (Connection con = PgConnectionManager.open();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setFetchSize(50);
            stmt.setQueryTimeout(10);
            stmt.setMaxRows(1000);
            stmt.setLong(1, flight_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}