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
    private static final String PG_MAX_ROWS_KEY = "db.pg.max-rows";
    private static final String PG_FETCH_SIZE_KEY = "db.pg.fetch-size";
    private static final String PG_QUERY_TIMEOUT_KEY = "db.pg.query-timeout";

    private static BlockingQueue<ConnectionWrapper> connectionPool;

    private static List<Connection> sourceConnections;
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final int DEFAULT_MAX_ROWS = 1000;
    private static final int DEFAULT_FETCH_SIZE = 100;
    private static final int DEFAULT_QUERY_TIMEOUT = 10;

    private PgConnectionManager() {
    }

    static {
        initPool();
    }

    private static void initPool() {
        String poolSizeStr = PropertiesUtil.get(PG_POOL_SIZE_KEY);
        String maxRowsStr = PropertiesUtil.get(PG_MAX_ROWS_KEY);
        String fetchSizeStr = PropertiesUtil.get(PG_FETCH_SIZE_KEY);
        String queryTimeoutStr = PropertiesUtil.get(PG_QUERY_TIMEOUT_KEY);

        int poolSize = poolSizeStr == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSizeStr);
        int maxRows = maxRowsStr == null ? DEFAULT_MAX_ROWS : Integer.parseInt(maxRowsStr);
        int fetchSize = fetchSizeStr == null ? DEFAULT_FETCH_SIZE : Integer.parseInt(fetchSizeStr);
        int queryTimeout = queryTimeoutStr == null ? DEFAULT_QUERY_TIMEOUT : Integer.parseInt(queryTimeoutStr);

        connectionPool = new ArrayBlockingQueue<>(poolSize);
        sourceConnections = new ArrayList<>();

        for (int i = 0; i < poolSize; i++) {
            Connection connection = open();
            connectionPool.add(new ConnectionWrapper(connection, connectionPool, maxRows, fetchSize, queryTimeout));
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
