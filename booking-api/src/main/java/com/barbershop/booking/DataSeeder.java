package com.barbershop.booking;

import com.barbershop.booking.appointments.repositories.AppointmentProcedureRepository;
import com.barbershop.booking.appointments.repositories.AppointmentRepository;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.models.BarberSchedule;
import com.barbershop.booking.barbers.models.BarberTimeOff;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.barbers.repositories.BarberScheduleRepository;
import com.barbershop.booking.barbers.repositories.BarberTimeOffRepository;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.users.models.Role;
import com.barbershop.booking.users.models.User;
import com.barbershop.booking.users.repository.UserRepository;
import com.barbershop.booking.customers.models.Customer;
import com.barbershop.booking.customers.repository.CustomerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final ProcedureRepository procedureRepository;
    private final BarberScheduleRepository barberScheduleRepository;
    private final BarberTimeOffRepository barberTimeOffRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentProcedureRepository  appointmentProcedureRepository;
    private final AppointmentRepository  appointmentRepository;

    public DataSeeder(UserRepository userRepository,
                      BarberRepository barberRepository,
                      ProcedureRepository procedureRepository,
                      BarberScheduleRepository barberScheduleRepository,
                      BarberTimeOffRepository barberTimeOffRepository, CustomerRepository customerRepository, AppointmentProcedureRepository appointmentProcedureRepository, AppointmentRepository appointmentRepository) {

        this.userRepository = userRepository;
        this.barberRepository = barberRepository;
        this.procedureRepository = procedureRepository;
        this.barberScheduleRepository = barberScheduleRepository;
        this.barberTimeOffRepository = barberTimeOffRepository;
        this.customerRepository = customerRepository;
        this.appointmentProcedureRepository = appointmentProcedureRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(String... args) {

        appointmentProcedureRepository.deleteAll();
        appointmentRepository.deleteAll();

        String email = "barber.seed@example.com";

        if (!userRepository.existsByEmail(email)) {

            User user = new User(
                    email,
                    "DEV_ONLY_NOT_A_REAL_PASSWORD_HASH",
                    Role.BARBER
            );

            user = userRepository.save(user);

            Barber barber = new Barber(
                    "Andrei",
                    "Popescu",
                    "0700000001",
                    user
            );

            barber = barberRepository.save(barber);

            List<Procedure> procedures = List.of(
                    new Procedure(
                            "Haircut",
                            "Classic haircut",
                            30,
                            new BigDecimal("50.00"),
                            barber
                    ),
                    new Procedure(
                            "Beard Trim",
                            "Beard shaping",
                            15,
                            new BigDecimal("30.00"),
                            barber
                    ),
                    new Procedure(
                            "Haircut and Beard",
                            "Complete haircut and beard service",
                            45,
                            new BigDecimal("75.00"),
                            barber
                    )
            );

            procedureRepository.saveAll(procedures);

            for (int day = 1; day <= 5; day++) {
                barberScheduleRepository.save(
                        new BarberSchedule(
                                barber,
                                day,
                                LocalTime.of(9, 0),
                                LocalTime.of(17, 0)
                        )
                );
            }

            LocalDate date = LocalDate.of(2026, 10, 26);

            barberTimeOffRepository.save(
                    new BarberTimeOff(
                            barber,
                            date.atTime(12, 0),
                            date.atTime(13, 0),
                            "Lunch break"
                    )
            );

            System.out.println("Seeded 1 barber");
            System.out.println("Seeded " + procedures.size() + " procedures");
            System.out.println("Barber ID: " + barber.getId());
        }

        String customerEmail = "customer.seed@example.com";

        if (!userRepository.existsByEmail(customerEmail)) {

            User customerUser = new User(
                    customerEmail,
                    "DEV_ONLY_NOT_A_REAL_PASSWORD_HASH",
                    Role.CUSTOMER
            );

            customerUser = userRepository.save(customerUser);

            Customer customer = new Customer(
                    customerUser,
                    "Mihai",
                    "Ionescu",
                    "0712345678"
            );

            customer = customerRepository.save(customer);

            System.out.println("Customer ID: " + customer.getId());
        }
    }
}