package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketsRunner {
    public static void main(String[] args) {
        String sql = """
                UPDATE flight
                SET cancelled = true
                WHERE id in (SELECT id FROM flight ORDER BY random() LIMIT 1000)
                """;
        try (Connection con = PgConnectionManager.open();
             Statement stmt = con.createStatement()) {
            int executeResult = stmt.executeUpdate(sql);
            System.out.println(executeResult);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}