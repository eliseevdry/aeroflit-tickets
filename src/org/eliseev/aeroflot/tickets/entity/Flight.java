package org.eliseev.aeroflot.tickets.entity;

import org.eliseev.aeroflot.tickets.dto.FlightStatus;

import java.time.Instant;

public class Flight {
    private Long id;
    private Integer pathId;
    private Integer aircraftId;
    private Instant departureDate;
    private Instant arrivalDate;
    private Boolean isCancelled;
    private FlightStatus status;

    public Flight() {
    }

    public Flight(Long id, Integer pathId, Integer aircraftId, Instant departureDate, Instant arrivalDate, Boolean isCancelled) {
        this.id = id;
        this.pathId = pathId;
        this.aircraftId = aircraftId;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.isCancelled = isCancelled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPathId() {
        return pathId;
    }

    public void setPathId(Integer pathId) {
        this.pathId = pathId;
    }

    public Integer getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(Integer aircraftId) {
        this.aircraftId = aircraftId;
    }

    public Instant getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Instant departureDate) {
        this.departureDate = departureDate;
    }

    public Instant getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Instant arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Flight{" +
               "id=" + id +
               ", pathId=" + pathId +
               ", aircraftId=" + aircraftId +
               ", departureDate=" + departureDate +
               ", arrivalDate=" + arrivalDate +
               ", isCancelled=" + isCancelled +
               '}';
    }
}
