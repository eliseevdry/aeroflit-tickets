package org.eliseev.aeroflot.tickets.dao;

import org.eliseev.aeroflot.tickets.dto.FlightStatus;
import org.eliseev.aeroflot.tickets.entity.Flight;
import org.eliseev.aeroflot.tickets.exception.DaoException;
import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static org.eliseev.aeroflot.tickets.dto.FlightStatus.ARRIVED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.CANCELLED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.DEPARTED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.SCHEDULED;

public class FlightDao {

    private static final FlightDao INSTANCE = new FlightDao();

    private static final String SQL_DELETE_BY_ID = """
            DELETE
            FROM flight
            WHERE id = ?
            """;
    private static final String SQL_FIND_ALL = """
            SELECT id, path_id, aircraft_id, departure_date, arrival_date, cancelled
            FROM flight
            """;
    private static final String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ? LIMIT 1";

    private FlightDao() {
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    public boolean deleteById(long id) {
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_BY_ID)) {
            stmt.setLong(1, id);
            int i = stmt.executeUpdate();
            return i > 0;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    public Optional<Flight> findById(long id) {
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(SQL_FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            Flight flight = null;
            if (rs.next()) {
                flight = mapFlight(rs);
            }
            return Optional.ofNullable(flight);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    private Flight mapFlight(ResultSet rs) throws SQLException {
        Flight flight;
        flight = new Flight(
                rs.getLong("id"),
                rs.getInt("path_id"),
                rs.getInt("aircraft_id"),
                rs.getTimestamp("departure_date").toInstant(),
                rs.getTimestamp("arrival_date").toInstant(),
                rs.getBoolean("cancelled")
        );
        return flight;
    }

    private FlightStatus resolveStatus(Instant departureDate, Instant arrivalDate, boolean isCancelled) {
        FlightStatus status;
        if (isCancelled) {
            status = CANCELLED;
        }
        Instant now = Instant.now();
        if (arrivalDate.isBefore(now)) {
            status = ARRIVED;
        } else if (departureDate.isAfter(now)) {
            status = SCHEDULED;
        } else {
            status = DEPARTED;
        }
        return status;
    }
}
