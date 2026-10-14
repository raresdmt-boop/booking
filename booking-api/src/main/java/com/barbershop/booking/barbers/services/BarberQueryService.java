
package com.barbershop.booking.barbers.services;

import com.barbershop.booking.barbers.models.Barber;
import java.util.List;

public interface BarberQueryService {

    Barber getBarberById(Long id);

    List<Barber> getAllBarbers();

}
