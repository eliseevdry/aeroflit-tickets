package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TicketsRunner {
    public static void main(String[] args) {
        String flightId = "45000 OR 1 = 1";
        System.out.println(getTicketsId(flightId));
    }

    private static List<Long> getTicketsId(String flight_id) {
        String sql = """
                SELECT id
                FROM ticket
                WHERE flight_id = %s;
                """.formatted(flight_id);
        List<Long> result = new ArrayList<>();
        try (Connection con = PgConnectionManager.open();
             Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                result.add(rs.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}