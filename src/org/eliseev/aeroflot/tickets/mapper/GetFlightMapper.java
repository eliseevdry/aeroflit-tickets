package org.eliseev.aeroflot.tickets.mapper;

import org.eliseev.aeroflot.tickets.dao.FlightDao;
import org.eliseev.aeroflot.tickets.dto.FlightStatus;
import org.eliseev.aeroflot.tickets.dto.GetFlightDto;
import org.eliseev.aeroflot.tickets.entity.Flight;

import java.time.Instant;

import static org.eliseev.aeroflot.tickets.dto.FlightStatus.ARRIVED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.CANCELLED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.DEPARTED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.SCHEDULED;

public class GetFlightMapper {
    private static final GetFlightMapper INSTANCE = new GetFlightMapper();

    private final FlightDao flightDao = FlightDao.getInstance();

    private GetFlightMapper() {
    }

    public static GetFlightMapper getInstance() {
        return INSTANCE;
    }

    public GetFlightDto map(Flight from) {

        return new GetFlightDto(
                from.getId(),
                from.getPathId(),
                from.getAircraftId(),
                from.getDepartureDate(),
                from.getArrivalDate(),
                resolveStatus(from.getDepartureDate(), from.getArrivalDate(), from.getCancelled())
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
