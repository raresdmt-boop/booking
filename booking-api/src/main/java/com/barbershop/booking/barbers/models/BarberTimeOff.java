
package com.barbershop.booking.barbers.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity(name = "BarberTimeOff")
@Table(name = "barber_time_off")
public class BarberTimeOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @Setter
    @NotNull
    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Setter
    @NotNull
    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Setter
    @Column(name = "reason", length = 200)
    private String reason;

    protected BarberTimeOff() {
    }

    public BarberTimeOff(Barber barber,
                         LocalDateTime startDatetime,
                         LocalDateTime endDatetime,
                         String reason) {
        this.barber = barber;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BarberTimeOff that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BarberTimeOff{" +
                "id=" + id +
                ", startDatetime=" + startDatetime +
                ", endDatetime=" + endDatetime +
                ", reason='" + reason + '\'' +
                '}';
    }
}
