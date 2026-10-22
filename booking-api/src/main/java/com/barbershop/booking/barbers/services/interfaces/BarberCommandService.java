
package com.barbershop.booking.barbers.services.interfaces;

import com.barbershop.booking.barbers.dtos.BarberCreateRequest;
import com.barbershop.booking.barbers.dtos.BarberResponse;
import com.barbershop.booking.barbers.dtos.BarberUpdateRequest;

public interface BarberCommandService {

    BarberResponse addBarber(BarberCreateRequest request);

    BarberResponse updateBarber(Long id, BarberUpdateRequest request);

    BarberResponse deleteBarber(Long id);
}
