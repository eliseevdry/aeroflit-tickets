package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.dto.CreateUpdateFlightDto;
import org.eliseev.aeroflot.tickets.dto.GetFlightDto;
import org.eliseev.aeroflot.tickets.service.FlightService;
import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

import java.time.Instant;

public class FlightRunner {
    public static void main(String[] args) {
        try {
            FlightService flightService = FlightService.getInstance();
//            flightService.deleteById(58323L);
//            System.out.println(flightService.findById(57920L));
            GetFlightDto result = flightService.createOrUpdate(
                    new CreateUpdateFlightDto(
                            58326L,
                            null,
                            null,
                            Instant.now(),
                            null,
                            null
                    )
            );
            System.out.println(result);

        } finally {
            PgConnectionManager.closeAllConnections();
        }
    }
}
