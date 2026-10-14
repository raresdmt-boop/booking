
package com.barbershop.booking.appointments.models;

import com.barbershop.booking.procedures.models.Procedure;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity(name = "AppointmentProcedure")
@Table(
        name = "appointment_procedures",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_appointment_procedure",
                        columnNames = {"appointment_id", "procedure_id"}
                )
        }
)
public class AppointmentProcedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @Setter
    @NotNull
    @PositiveOrZero
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Setter
    @NotNull
    @Positive
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    protected AppointmentProcedure() {
    }

    public AppointmentProcedure(Appointment appointment,
                                Procedure procedure,
                                BigDecimal price,
                                Integer durationMinutes) {
        this.appointment = appointment;
        this.procedure = procedure;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentProcedure that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AppointmentProcedure{" +
                "id=" + id +
                ", price=" + price +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}
