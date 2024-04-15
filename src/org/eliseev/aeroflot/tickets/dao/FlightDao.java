package org.eliseev.aeroflot.tickets.dao;

import org.eliseev.aeroflot.tickets.entity.Flight;
import org.eliseev.aeroflot.tickets.exception.DaoException;
import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

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

    private static final String SQL_CREATE = """
            INSERT INTO flight(path_id, aircraft_id, departure_date, arrival_date, cancelled)
            VALUES (?,?,?,?,?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE flight
            SET path_id = ?, aircraft_id = ?, departure_date = ?, arrival_date = ?, cancelled = ?
            WHERE id = ?
            """;

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

    public Flight create(Flight flight) {
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            mapStatement(stmt, flight);
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                flight.setId(resultSet.getLong(1));
            }
            return flight;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    public Flight update(Flight flight) {
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE)) {
            mapStatement(stmt, flight);
            stmt.setLong(6, flight.getId());
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        return flight;
    }

    private void mapStatement(PreparedStatement stmt, Flight flight) throws SQLException {
        stmt.setInt(1, flight.getPathId());
        stmt.setInt(2, flight.getAircraftId());
        stmt.setTimestamp(3, Timestamp.from(flight.getDepartureDate()));
        stmt.setTimestamp(4, Timestamp.from(flight.getArrivalDate()));
        stmt.setBoolean(5, flight.getCancelled());
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
}
