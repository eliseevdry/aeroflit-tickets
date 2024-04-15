package org.eliseev.aeroflot.tickets.service;

import org.eliseev.aeroflot.tickets.dao.FlightDao;
import org.eliseev.aeroflot.tickets.dto.CreateUpdateFlightDto;
import org.eliseev.aeroflot.tickets.dto.FlightStatus;
import org.eliseev.aeroflot.tickets.dto.GetFlightDto;
import org.eliseev.aeroflot.tickets.entity.Flight;
import org.eliseev.aeroflot.tickets.mapper.CreateUpdateFlightMapper;
import org.eliseev.aeroflot.tickets.mapper.GetFlightMapper;

import java.time.Instant;
import java.util.Optional;

import static org.eliseev.aeroflot.tickets.dto.FlightStatus.ARRIVED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.CANCELLED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.DEPARTED;
import static org.eliseev.aeroflot.tickets.dto.FlightStatus.SCHEDULED;

public class FlightService {
    private static final FlightService INSTANCE = new FlightService();
    private final FlightDao flightDao = FlightDao.getInstance();

    private final CreateUpdateFlightMapper createUpdateFlightMapper = CreateUpdateFlightMapper.getInstance();

    private final GetFlightMapper getFlightMapper = GetFlightMapper.getInstance();

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
        return maybeFlight.map(getFlightMapper::map).orElse(null);
    }

    public GetFlightDto createOrUpdate(CreateUpdateFlightDto dto) {
        Flight mappedFlight = createUpdateFlightMapper.map(dto);
        Flight result;
        if (mappedFlight.getId() == null) {
            result = flightDao.create(mappedFlight);
        } else {
            result = flightDao.update(mappedFlight);
        }
        return getFlightMapper.map(result);
    }
}
