package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class TicketsRunner {
    public static void main(String[] args) {
        try (Connection con = PgConnectionManager.open()) {
            System.out.println(con.getTransactionIsolation());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}