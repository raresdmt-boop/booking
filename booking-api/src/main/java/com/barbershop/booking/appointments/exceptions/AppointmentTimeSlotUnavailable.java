
package com.barbershop.booking.appointments.exceptions;

public class AppointmentTimeSlotUnavailable extends RuntimeException {

    public AppointmentTimeSlotUnavailable() {
        super(AppointmentExceptionConstants.APPOINTMENT_TIME_SLOT_UNAVAILABLE);
    }
}
