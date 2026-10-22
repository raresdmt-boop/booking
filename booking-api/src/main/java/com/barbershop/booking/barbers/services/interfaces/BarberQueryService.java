
package com.barbershop.booking.barbers.services.interfaces;

import com.barbershop.booking.barbers.dtos.BarberResponse;

import java.util.List;

public interface BarberQueryService {

    BarberResponse getBarberById(Long id);

    List<BarberResponse> getBarbers();
}
