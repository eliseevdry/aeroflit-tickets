package org.eliseev.aeroflot.tickets.dto;

public record FlightFilter(Long offset,
                           Integer limit,
                           Integer pathId,
                           Integer aircraftId,
                           Boolean isCancelled
) {
}
