
package com.barbershop.booking.barbers.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Entity(name = "BarberSchedule")
@Table(name = "barber_schedule")
public class BarberSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @Setter
    @Min(1)
    @Max(7)
    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Setter
    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Setter
    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    protected BarberSchedule() {
    }

    public BarberSchedule(Barber barber, int dayOfWeek,
                          LocalTime startTime, LocalTime endTime) {
        this.barber = barber;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BarberSchedule that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BarberSchedule{" +
                "id=" + id +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}