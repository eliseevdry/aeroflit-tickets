package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        try {
//            Long flightId = 45000L;
//            System.out.println(getTicketsId(flightId));
            List<Long> result = getFlightsIdBetween(
                    LocalDateTime.of(2023, 12, 11, 3, 4),
                    LocalDateTime.of(2023, 12, 13, 12, 30)
            );
            System.out.println(result);
//            showMetaInf();
//
//            try {
//                deleteFlight(45001L);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//            try {
//                cancelFlights(new long[]{45001L, 45002L});
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//            loadAircraftPic(1, "Boeing-737-100_1.jpeg");
//            getAircraftPic(1, "first_aircraft.jpg");
        } finally {
            PgConnectionManager.closeAllConnections();
        }
    }

    private static void loadAircraftPic(int aircraftId, String filename) {
        String sql = "UPDATE aircraft SET image = ? WHERE id = ?";
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            byte[] file = Files.readAllBytes(Path.of("resources", filename));
            stmt.setBytes(1, file);
            stmt.setInt(2, aircraftId);
            stmt.executeUpdate();
        } catch (SQLException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void getAircraftPic(int aircraftId, String filename) {
        String sql = "SELECT image FROM aircraft WHERE id = ?";
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, aircraftId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Files.write(Path.of("resources", filename), rs.getBytes("image"), StandardOpenOption.CREATE);
            }
        } catch (SQLException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void showMetaInf() {
        try (Connection con = PgConnectionManager.get()) {
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
        try (Connection con = PgConnectionManager.get();
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
        try (Connection con = PgConnectionManager.get();
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

    private static void cancelFlights(long[] flightsId) throws SQLException {
        // можно было через WHERE id IN (...) и без батча, тут для демонстрации.
        String sql = "UPDATE flight SET cancelled = true WHERE id = ?";
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = PgConnectionManager.get();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);
            for (long id : flightsId) {
                stmt.setLong(1, id);
                stmt.addBatch();
            }
            stmt.executeBatch();
            con.commit();
        } catch (Exception ex) {
            if (con != null) {
                con.rollback();
            }
            throw ex;
        } finally {
            if (con != null) {
                con.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private static void deleteFlight(Long flightId) throws SQLException {
        String deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = ?";
        String deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        Connection con = null;
        PreparedStatement deleteTicketsStmt = null;
        PreparedStatement deleteFlightStmt = null;
        try {
            con = PgConnectionManager.get();
            con.setAutoCommit(false);
            deleteTicketsStmt = con.prepareStatement(deleteTicketsSql);
            deleteTicketsStmt.setLong(1, flightId);
            deleteFlightStmt = con.prepareStatement(deleteFlightSql);
            deleteFlightStmt.setLong(1, flightId);

            deleteTicketsStmt.executeUpdate();
            if (true) {
                throw new RuntimeException("Oops");
            }
            deleteFlightStmt.executeUpdate();
            con.commit();
        } catch (Exception ex) {
            if (con != null) {
                con.rollback();
            }
            throw ex;
        } finally {
            if (con != null) {
                con.close();
            }
            if (deleteTicketsStmt != null) {
                deleteTicketsStmt.close();
            }
            if (deleteFlightStmt != null) {
                deleteFlightStmt.close();
            }
        }
    }
}