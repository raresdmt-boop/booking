
package com.barbershop.booking.barbers.services;

import com.barbershop.booking.barbers.models.Barber;

public interface BarberCommandService {

    Barber createBarber(Barber barber);

    Barber updateBarber(Long id, Barber barber);

    void deleteBarber(Long id);

}
