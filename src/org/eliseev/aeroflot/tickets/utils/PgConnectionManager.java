package org.eliseev.aeroflot.tickets.utils;

import org.postgresql.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class PgConnectionManager {
    private static final String  PG_URL="jdbc:postgresql://localhost:5432/flight_repository";
    private static final String  PG_USERNAME="postgres";
    private static final String  PG_PASSWORD="postgres";

    private PgConnectionManager() {
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(PG_URL, PG_USERNAME, PG_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
