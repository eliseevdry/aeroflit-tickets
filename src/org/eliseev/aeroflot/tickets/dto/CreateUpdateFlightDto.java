package org.eliseev.aeroflot.tickets.dto;

import java.time.Instant;

public record CreateUpdateFlightDto(
        Long id,
        Integer pathId,
        Integer aircraftId,
        Instant departureDate,
        Instant arrivalDate,
        Boolean isCancelled
) {
}
