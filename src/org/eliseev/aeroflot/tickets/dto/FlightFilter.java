package org.eliseev.aeroflot.tickets.dto;

public record FlightFilter(int limit, int offset, Integer pathId, Integer aircraftId, Boolean isCancelled) {
}
