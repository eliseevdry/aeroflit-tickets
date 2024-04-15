package org.eliseev.aeroflot.tickets.service;

import org.eliseev.aeroflot.tickets.dao.FlightDao;
import org.eliseev.aeroflot.tickets.dto.FlightStatus;
import org.eliseev.aeroflot.tickets.dto.GetFlightDto;
import org.eliseev.aeroflot.tickets.entity.Flight;

import java.time.Instant;
import java.util.Optional;

import static org.eliseev.aeroflot.tickets.dto.FlightStatus.ARRIVED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.CANCELLED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.DEPARTED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.SCHEDULED;

public class FlightService {
    private static final FlightService INSTANCE = new FlightService();
    private final FlightDao flightDao = FlightDao.getInstance();

    private FlightService() {
    }

    public static FlightService getInstance() {
        return INSTANCE;
    }

    public boolean deleteById(long id) {
        return flightDao.deleteById(id);
    }

    public GetFlightDto findById(long id) {
        Optional<Flight> maybeFlight = flightDao.findById(id);
        return maybeFlight.map(this::mapGetFlightDto).orElse(null);
    }

    private GetFlightDto mapGetFlightDto(Flight flight) {
        return new GetFlightDto(
                flight.getId(),
                flight.getPathId(),
                flight.getAircraftId(),
                flight.getDepartureDate(),
                flight.getArrivalDate(),
                resolveStatus(flight.getDepartureDate(), flight.getArrivalDate(), flight.getCancelled())
        );
    }

    private FlightStatus resolveStatus(Instant departureDate, Instant arrivalDate, boolean isCancelled) {
        if (isCancelled) {
            return CANCELLED;
        }
        FlightStatus status;
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
