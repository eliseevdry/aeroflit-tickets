package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.dto.CreateUpdateFlightDto;
import org.eliseev.aeroflot.tickets.dto.FlightFilter;
import org.eliseev.aeroflot.tickets.dto.GetFlightDto;
import org.eliseev.aeroflot.tickets.entity.Flight;
import org.eliseev.aeroflot.tickets.service.FlightService;
import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.time.Instant;
import java.util.List;

public class FlightRunner {
    public static void main(String[] args) {
        try {
            FlightService flightService = FlightService.getInstance();
//            flightService.deleteById(58323L);
//            System.out.println(flightService.findById(57920L));
//            GetFlightDto result = flightService.createOrUpdate(
//                    new CreateUpdateFlightDto(
//                            58326L,
//                            null,
//                            null,
//                            Instant.now(),
//                            null,
//                            null
//                    )
//            );
//            System.out.println(result);
            List<GetFlightDto> flights = flightService.findAllWithFilter(new FlightFilter(null, 500, null, null, true));
            for (int i = 0; i < flights.size(); i++) {
                System.out.println(i + 1 + ") " + flights.get(i));
            }
        } finally {
            PgConnectionManager.closeAllConnections();
        }
    }
}
