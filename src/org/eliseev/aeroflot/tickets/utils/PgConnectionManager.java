package org.eliseev.aeroflot.tickets.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class PgConnectionManager {
    private static final String PG_URL_KEY = "db.pg.url";
    private static final String PG_USERNAME_KEY = "db.pg.username";
    private static final String PG_PASSWORD_KEY = "db.pg.password";
    private static final String PG_POOL_SIZE_KEY = "db.pg.pool.size";

    private static BlockingQueue<ConnectionWrapper> connectionPool;

    private static List<Connection> sourceConnections;
    private static final int DEFAULT_POOL_SIZE = 10;

    private PgConnectionManager() {
    }

    static {
        initPool();
    }

    private static void initPool() {
        String poolSizeStr = PropertiesUtil.get(PG_POOL_SIZE_KEY);

        int poolSize = poolSizeStr == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSizeStr);

        connectionPool = new ArrayBlockingQueue<>(poolSize);
        sourceConnections = new ArrayList<>();

        for (int i = 0; i < poolSize; i++) {
            Connection connection = open();
            connectionPool.add(new ConnectionWrapper(connection, connectionPool));
            sourceConnections.add(connection);
        }
    }

    public static Connection get() {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeAllConnections() {
        for (Connection sourceConnection : sourceConnections) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Connection open() {
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
