
package com.barbershop.booking.appointments.models;

import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.customers.models.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity(name = "Appointment")
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_appointment_id")
    private RecurringAppointment recurringAppointment;

    @Setter
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Setter
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @Setter
    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Setter
    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    @Setter
    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "appointment")
    private List<AppointmentProcedure> procedures = new ArrayList<>();

    protected Appointment() {
    }

    public Appointment(Customer customer, Barber barber,
                       LocalDateTime startTime, LocalDateTime endTime,
                       String notes) {
        this.customer = customer;
        this.barber = barber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment appointment)) return false;
        return id != null && id.equals(appointment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

