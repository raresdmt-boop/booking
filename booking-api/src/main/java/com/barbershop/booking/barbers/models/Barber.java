package com.barbershop.booking.barbers.models;

import com.barbershop.booking.appointments.models.RecurringAppointment;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.users.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import com.barbershop.booking.appointments.models.Appointment;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@Getter
@Entity(name = "Barber")
@Table(name = "barbers")
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Setter
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Setter
    @NotBlank(message = "Phone is required")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "barber")
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "barber")
    private List<BarberSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "barber")
    private List<BarberTimeOff> timeOffs = new ArrayList<>();

    @OneToMany(mappedBy = "barber")
    private List<Procedure> procedures = new ArrayList<>();

    @OneToMany(mappedBy = "barber")
    private List<RecurringAppointment> recurringAppointments = new ArrayList<>();

    protected Barber() {
    }

    public Barber(String firstName, String lastName,
                  String phone, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.user = user;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barber barber)) return false;
        return id != null && id.equals(barber.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Barber{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", userId=" + user.getId() +
                '}';
    }
}

