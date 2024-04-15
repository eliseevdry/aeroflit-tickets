package org.eliseev.aeroflot.tickets.dto;

import java.time.Instant;

public record GetFlightDto(
        Long id,
        Integer pathId,
        Integer aircraftId,
        Instant departureDate,
        Instant arrivalDate,
        FlightStatus status
) {
}
