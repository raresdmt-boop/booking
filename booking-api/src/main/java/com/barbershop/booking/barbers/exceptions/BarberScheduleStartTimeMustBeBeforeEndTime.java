package com.barbershop.booking.barbers.exceptions;

public class BarberScheduleStartTimeMustBeBeforeEndTime extends RuntimeException {
    public BarberScheduleStartTimeMustBeBeforeEndTime() {
        super(BarberExceptionConstants.BARBER_SCHEDULE_START_TIME_MUST_BE_BEFORE_END_TIME);
    }
}
