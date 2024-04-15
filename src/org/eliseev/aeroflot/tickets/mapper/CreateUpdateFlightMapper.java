package org.eliseev.aeroflot.tickets.mapper;

import org.eliseev.aeroflot.tickets.dao.FlightDao;
import org.eliseev.aeroflot.tickets.dto.CreateUpdateFlightDto;
import org.eliseev.aeroflot.tickets.entity.Flight;
import org.eliseev.aeroflot.tickets.exception.ServiceException;

import java.time.Instant;

public class CreateUpdateFlightMapper {
    private static final CreateUpdateFlightMapper INSTANCE = new CreateUpdateFlightMapper();

    private final FlightDao flightDao = FlightDao.getInstance();

    private CreateUpdateFlightMapper() {
    }

    public static CreateUpdateFlightMapper getInstance() {
        return INSTANCE;
    }

    public Flight map(CreateUpdateFlightDto from) {
        Flight flight;
        if (from.id() == null) {
            if (from.pathId() == null ||
                from.aircraftId() == null ||
                from.departureDate() == null ||
                from.arrivalDate() == null ||
                from.isCancelled() == null) {
                throw new ServiceException("Указаны не все поля для создания сущности");
            }
            flight = map(from, new Flight());
        } else {
            flight = flightDao.findById(from.id())
                    .map(f -> map(from, f))
                    .orElseThrow(() -> new ServiceException("Нет перелета с id = " + from.id()));
        }
        return flight;
    }

    public Flight map(CreateUpdateFlightDto from, Flight to) {
        if (from.pathId() != null) {
            to.setPathId(from.pathId());
        }
        if (from.aircraftId() != null) {
            to.setAircraftId(from.aircraftId());
        }
        if (from.departureDate() != null) {
            to.setDepartureDate(from.departureDate());
        }
        if (from.arrivalDate() != null) {
            if (from.arrivalDate().isBefore(Instant.now())) {
                throw new ServiceException("Дата прилета указана в прошлом");
            }
            if (from.arrivalDate().isBefore(to.getDepartureDate())) {
                throw new ServiceException("Дата прилета указана раньше даты вылета");
            }
            to.setArrivalDate(from.arrivalDate());
        }
        if (from.isCancelled() != null) {
            to.setCancelled(from.isCancelled());
        }
        return to;
    }
}
