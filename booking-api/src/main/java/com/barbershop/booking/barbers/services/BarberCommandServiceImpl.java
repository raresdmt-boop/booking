
package com.barbershop.booking.barbers.services;

import com.barbershop.booking.barbers.dtos.BarberCreateRequest;
import com.barbershop.booking.barbers.dtos.BarberResponse;
import com.barbershop.booking.barbers.dtos.BarberUpdateRequest;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.exceptions.BarberUserAlreadyExists;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.barbers.services.interfaces.BarberCommandService;
import com.barbershop.booking.users.exceptions.UserNotFound;
import com.barbershop.booking.users.models.User;
import com.barbershop.booking.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class BarberCommandServiceImpl implements BarberCommandService {

    private final BarberRepository barberRepository;
    private final UserRepository userRepository;

    public BarberCommandServiceImpl(BarberRepository barberRepository,
                                    UserRepository userRepository) {
        this.barberRepository = barberRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BarberResponse addBarber(BarberCreateRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFound::new);

        if (barberRepository.existsByUserId(request.userId())) {
            throw new BarberUserAlreadyExists();
        }

        Barber barber = new Barber(
                request.firstName(),
                request.lastName(),
                request.phone(),
                user
        );

        Barber savedBarber = barberRepository.save(barber);

        return BarberResponse.from(savedBarber);
    }

    @Override
    @Transactional
    public BarberResponse updateBarber(Long id, BarberUpdateRequest request) {

        Barber barber = barberRepository.findById(id)
                .orElseThrow(BarberNotFound::new);

        if (request.firstName() != null && !request.firstName().isBlank()) {
            barber.setFirstName(request.firstName());
        }

        if (request.lastName() != null && !request.lastName().isBlank()) {
            barber.setLastName(request.lastName());
        }

        if (request.phone() != null && !request.phone().isBlank()) {
            barber.setPhone(request.phone());
        }

        if (request.active() != null) {
            barber.setActive(request.active());
        }

        return BarberResponse.from(barber);
    }

    @Override
    @Transactional
    public BarberResponse deleteBarber(Long id) {

        Barber barber = barberRepository.findById(id)
                .orElseThrow(BarberNotFound::new);

        BarberResponse deletedBarber = BarberResponse.from(barber);

        barberRepository.delete(barber);

        return deletedBarber;
    }
}
