package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketsRunner {
    public static void main(String[] args) {
        String sql = """
                SELECT *
                FROM flight
                ORDER BY random()
                LIMIT 100
                """;
        try (Connection con = PgConnectionManager.open();
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String result = "--- id = " +
                                rs.getLong("id") + " ---\n" +
                                "aircraft_id = " + rs.getInt("aircraft_id") + "\n" +
                                "path_id = " + rs.getInt("path_id") + "\n" +
                                "departure_date = " + rs.getTimestamp("departure_date") + "\n" +
                                "arrival_date = " + rs.getTimestamp("arrival_date") + "\n" +
                                "cancelled = " + rs.getBoolean("cancelled") + "\n";
                System.out.println(result);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}