package com.barbershop.booking.customers.models;


import com.barbershop.booking.appointments.models.Appointment;
import com.barbershop.booking.appointments.models.RecurringAppointment;
import com.barbershop.booking.users.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity(name = "Customer")
@Table(name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_phone", columnNames = "phone"),
        })
public class Customer {

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
    @Column(name = "blocked", nullable = false)
    private boolean blocked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer")
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<RecurringAppointment> recurringAppointments = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    protected Customer() {
    }

    public Customer(User user, String firstName, String lastName,
                    String phone) {

        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return id != null && id.equals(customer.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", blocked=" + blocked +
                ", createdAt=" + createdAt +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}

