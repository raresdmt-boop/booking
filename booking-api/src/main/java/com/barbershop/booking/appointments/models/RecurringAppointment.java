
package com.barbershop.booking.appointments.models;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity(name = "RecurringAppointment")
@Table(name = "recurring_appointments")
public class RecurringAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Setter
    @NotNull
    @Positive
    @Column(name = "interval_weeks", nullable = false)
    private Integer intervalWeeks;

    @Setter
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Setter
    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Setter
    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    protected RecurringAppointment() {
    }

    public RecurringAppointment(Customer customer,
                                Integer intervalWeeks,
                                LocalDate startDate,
                                LocalDate endDate,
                                LocalTime startTime,
                                Barber barber) {
        this.customer = customer;
        this.intervalWeeks = intervalWeeks;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.barber = barber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecurringAppointment that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RecurringAppointment{" +
                "id=" + id +
                ", intervalWeeks=" + intervalWeeks +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                ", startTime=" + startTime +
                ", barberId=" + barber.getId() +
                '}';
    }
}
