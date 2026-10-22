package com.barbershop.booking.appointments.controller;

import com.barbershop.booking.appointments.services.appointment.interfaces.AppointmentAvailabilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments/availability")
public class AppointmentAvailabilityController {

    private final AppointmentAvailabilityService availabilityService;

    public AppointmentAvailabilityController(
            AppointmentAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public List<LocalTime> getAvailableSlots(
            @RequestParam Long barberId,
            @RequestParam LocalDate date,
            @RequestParam List<Long> procedureIds) {

        return availabilityService.getAvailableSlots(
                barberId,
                date,
                procedureIds
        );
    }
}