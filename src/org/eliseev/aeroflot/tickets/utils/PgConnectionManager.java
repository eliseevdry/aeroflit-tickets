package org.eliseev.aeroflot.tickets.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class PgConnectionManager {
    private static final String PG_URL_KEY = "db.pg.url";
    private static final String PG_USERNAME_KEY = "db.pg.username";
    private static final String PG_PASSWORD_KEY = "db.pg.password";

    private PgConnectionManager() {
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(PG_URL_KEY),
                    PropertiesUtil.get(PG_USERNAME_KEY),
                    PropertiesUtil.get(PG_PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
