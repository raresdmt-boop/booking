
package com.barbershop.booking.appointments.controllers;

import com.barbershop.booking.appointments.dtos.appointment.AppointmentCreateRequest;
import com.barbershop.booking.appointments.dtos.appointment.AppointmentResponse;
import com.barbershop.booking.appointments.services.appointment.interfaces.AppointmentCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentCommandService appointmentCommandService;

    public AppointmentController(AppointmentCommandService appointmentCommandService) {
        this.appointmentCommandService = appointmentCommandService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request) {

        return appointmentCommandService.addAppointment(request);
    }
}
