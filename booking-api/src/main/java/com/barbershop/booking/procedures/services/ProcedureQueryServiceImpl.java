
package com.barbershop.booking.procedures.services;

import com.barbershop.booking.procedures.dtos.ProcedureResponse;
import com.barbershop.booking.procedures.exceptions.ProcedureNotFound;
import com.barbershop.booking.procedures.repository.ProcedureRepository;
import com.barbershop.booking.procedures.services.interfaces.ProcedureQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class ProcedureQueryServiceImpl implements ProcedureQueryService {

    private final ProcedureRepository procedureRepository;

    public ProcedureQueryServiceImpl(ProcedureRepository procedureRepository) {
        this.procedureRepository = procedureRepository;
    }

    @Override
    public ProcedureResponse getProcedureById(Long id) {
        return procedureRepository.findById(id)
                .map(ProcedureResponse::from)
                .orElseThrow(ProcedureNotFound::new);
    }

    @Override
    public List<ProcedureResponse> getProcedures() {
        return procedureRepository.findAll()
                .stream()
                .map(ProcedureResponse::from)
                .toList();
    }

    @Override
    public List<ProcedureResponse> getProceduresByBarberId(Long barberId) {
        return procedureRepository.findByBarberId(barberId)
                .stream()
                .map(ProcedureResponse::from)
                .toList();
    }
}
