
package com.barbershop.booking.appointments.exceptions;

public class AppointmentExceptionConstants {

    public static final String APPOINTMENT_ID_NOT_FOUND =
            "Appointment ID not found";

    public static final String APPOINTMENT_TIME_SLOT_UNAVAILABLE =
            "The selected time slot is already booked";

    public static final String APPOINTMENT_INVALID_TIME_INTERVAL =
            "Appointment end time must be after start time";

    public static final String APPOINTMENT_CANNOT_BE_CANCELED =
            "This appointment cannot be canceled";

    public static final String APPOINTMENT_INVALID_STATUS =
            "Appointment invalid status";

    public static final String APPOINTMENT_PROCEDURES_REQUIRED =
            "At least one procedure must be selected";

    public static final String APPOINTMENT_DUPLICATE_PROCEDURE =
            "Duplicate procedures are not allowed";

    public static final String APPOINTMENT_PROCEDURE_NOT_FOUND =
            "One or more selected procedures were not found";

    public static final String APPOINTMENT_PROCEDURE_BARBER_MISMATCH =
            "Procedure does not belong to the selected barber";

}
