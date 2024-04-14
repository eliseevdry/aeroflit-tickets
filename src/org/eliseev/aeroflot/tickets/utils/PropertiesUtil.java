package org.eliseev.aeroflot.tickets.utils;

import org.postgresql.util.PGPropertyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        initProperties();
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void initProperties() {
        try (InputStream inputStream = PGPropertyUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PropertiesUtil() {
    }
}
