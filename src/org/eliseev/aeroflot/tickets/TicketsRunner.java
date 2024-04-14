package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketsRunner {
    public static void main(String[] args) {
        String sql = """
                ALTER TABLE flight
                DROP COLUMN IF EXISTS status; 
                """;
        try (Connection con = PgConnectionManager.open();
             Statement stmt = con.createStatement()) {
            boolean executeResult = stmt.execute(sql);
            System.out.println(executeResult);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}