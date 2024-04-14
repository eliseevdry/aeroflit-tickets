package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketsRunner {
    public static void main(String[] args) {
        String sql = """
                INSERT INTO city(country_name, name)
                VALUES
                ('Russia', 'Moscow'),
                ('Russia', 'Voronezh');
                """;
        try (Connection con = PgConnectionManager.open();
             Statement stmt = con.createStatement()) {
            int count = stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            System.out.println("Update count: " + count);
            while (rs.next()) {
                String result = "--- id = " + rs.getInt("id") + " ---\n";
                System.out.print(result);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}