package org.eliseev.aeroflot.tickets;

import org.eliseev.aeroflot.tickets.dao.FlightDao;
import org.eliseev.aeroflot.tickets.dto.GetFlightDto;
import org.eliseev.aeroflot.tickets.service.FlightService;
import org.eliseev.aeroflot.tickets.utils.PgConnectionManager;

public class FlightRunner {
    public static void main(String[] args) {
        try {
            FlightService flightService = FlightService.getInstance();
//            flightService.deleteById(58323L);
//            System.out.println(flightService.findById(57920L));

        } finally {
            PgConnectionManager.closeAllConnections();
        }
    }
}
