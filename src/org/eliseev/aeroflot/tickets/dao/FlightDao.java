package org.eliseev.aeroflot.tickets.dao;

import org.eliseev.aeroflot.tickets.dto.FlightFilter;
import org.eliseev.aeroflot.tickets.entity.Flight;
import org.eliseev.aeroflot.tickets.exception.DaoException;
import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlightDao {

    private static final FlightDao INSTANCE = new FlightDao();

    private final FlightFilter emptyFilter = new FlightFilter(null, null, null, null, null);

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

    public List<Flight> findAllWithFilter(FlightFilter filter) {
        StringBuilder sqlBuilder = new StringBuilder(SQL_FIND_ALL);
        List<Object> args = new ArrayList<>();
        if (!filter.equals(emptyFilter)) {
            List<String> filters = new ArrayList<>();
            if (filter.pathId() != null) {
                filters.add("path_id = ?");
                args.add(filter.pathId());
            }
            if (filter.aircraftId() != null) {
                filters.add("aircraft_id = ?");
                args.add(filter.aircraftId());
            }
            if (filter.isCancelled() != null) {
                filters.add("cancelled = ?");
                args.add(filter.isCancelled());
            }
            sqlBuilder.append(filters.stream()
                    .collect(Collectors.joining(" AND ", "WHERE ", " OFFSET ? LIMIT ?")));
        }
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(sqlBuilder.toString())) {
            args.add(filter.offset() == null ? 0L : filter.offset());
            args.add(filter.limit() == null ? stmt.getMaxRows() : filter.limit());
            for (int i = 0; i < args.size(); i++) {
                stmt.setObject(i + 1, args.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            List<Flight> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapFlight(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    private List<Flight> findAll(String sql) {
        try (Connection con = PgConnectionManager.get();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            con.setReadOnly(true);
            ResultSet rs = stmt.executeQuery();
            List<Flight> content = new ArrayList<>();
            while (rs.next()) {
                content.add(mapFlight(rs));
            }
            return content;
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
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
