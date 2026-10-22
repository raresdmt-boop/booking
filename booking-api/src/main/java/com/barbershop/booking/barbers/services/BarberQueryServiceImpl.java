
package com.barbershop.booking.barbers.services;

import com.barbershop.booking.barbers.dtos.BarberResponse;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.barbers.services.interfaces.BarberQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class BarberQueryServiceImpl implements BarberQueryService {

    private final BarberRepository barberRepository;

    public BarberQueryServiceImpl(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    @Override
    public BarberResponse getBarberById(Long id) {
        return barberRepository.findById(id)
                .map(BarberResponse::from)
                .orElseThrow(BarberNotFound::new);
    }

    @Override
    public List<BarberResponse> getBarbers() {
        return barberRepository.findAll()
                .stream()
                .map(BarberResponse::from)
                .toList();
    }
}
