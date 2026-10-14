
package com.barbershop.booking.procedures.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import com.barbershop.booking.appointments.models.AppointmentProcedure;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

@Getter
@Entity(name = "Procedure")
@Table(name = "procedures")
public class Procedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Procedure name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Setter
    @Column(name = "description", length = 500)
    private String description;

    @Setter
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than zero")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Setter
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "procedure")
    private List<AppointmentProcedure> appointmentProcedures = new ArrayList<>();

    protected Procedure() {
    }

    public Procedure(String name, String description,
                     Integer durationMinutes, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Procedure procedure)) return false;
        return id != null && id.equals(procedure.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Procedure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", price=" + price +
                ", active=" + active +
                '}';
    }
}
