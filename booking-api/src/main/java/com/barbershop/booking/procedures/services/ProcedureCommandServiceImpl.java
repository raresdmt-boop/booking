
package com.barbershop.booking.procedures.services;

import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.barbers.models.Barber;
import com.barbershop.booking.barbers.repositories.BarberRepository;
import com.barbershop.booking.procedures.dtos.ProcedureCreateRequest;
import com.barbershop.booking.procedures.dtos.ProcedureResponse;
import com.barbershop.booking.procedures.dtos.ProcedureUpdateRequest;
import com.barbershop.booking.procedures.exceptions.ProcedureNameAlreadyExists;
import com.barbershop.booking.procedures.exceptions.ProcedureNotFound;
import com.barbershop.booking.procedures.models.Procedure;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.procedures.services.interfaces.ProcedureCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProcedureCommandServiceImpl implements ProcedureCommandService {

    private final ProcedureRepository procedureRepository;
    private final BarberRepository barberRepository;

    public ProcedureCommandServiceImpl(ProcedureRepository procedureRepository,
                                       BarberRepository barberRepository) {
        this.procedureRepository = procedureRepository;
        this.barberRepository = barberRepository;
    }

    @Override
    @Transactional
    public ProcedureResponse addProcedure(ProcedureCreateRequest request) {

        Barber barber = barberRepository.findById(request.barberId())
                .orElseThrow(BarberNotFound::new);

        if (procedureRepository.existsByBarberIdAndName(
                request.barberId(), request.name())) {
            throw new ProcedureNameAlreadyExists();
        }

        Procedure procedure = new Procedure(
                request.name(),
                request.description(),
                request.durationMinutes(),
                request.price(),
                barber
        );

        Procedure savedProcedure = procedureRepository.save(procedure);

        return ProcedureResponse.from(savedProcedure);
    }

    @Override
    @Transactional
    public ProcedureResponse updateProcedure(Long id, ProcedureUpdateRequest request) {

        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(ProcedureNotFound::new);

        if (request.name() != null && !request.name().isBlank()) {

            if (!request.name().equals(procedure.getName())
                    && procedureRepository.existsByBarberIdAndName(
                    procedure.getBarber().getId(), request.name())) {
                throw new ProcedureNameAlreadyExists();
            }

            procedure.setName(request.name());
        }

        if (request.description() != null) {
            procedure.setDescription(request.description());
        }

        if (request.durationMinutes() != null) {
            procedure.setDurationMinutes(request.durationMinutes());
        }

        if (request.price() != null) {
            procedure.setPrice(request.price());
        }

        if (request.active() != null) {
            procedure.setActive(request.active());
        }

        return ProcedureResponse.from(procedure);
    }

    @Override
    @Transactional
    public ProcedureResponse deleteProcedure(Long id) {

        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(ProcedureNotFound::new);

        ProcedureResponse deletedProcedure = ProcedureResponse.from(procedure);

        procedureRepository.delete(procedure);

        return deletedProcedure;
    }
}
