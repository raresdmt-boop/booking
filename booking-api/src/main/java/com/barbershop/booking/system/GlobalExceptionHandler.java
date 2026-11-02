package com.barbershop.booking.system;

import com.barbershop.booking.appointments.exceptions.*;
import com.barbershop.booking.barbers.exceptions.*;
import com.barbershop.booking.customers.exceptions.CustomerNotFound;
import com.barbershop.booking.customers.exceptions.CustomerPhoneAlreadyExists;
import com.barbershop.booking.customers.exceptions.CustomerUserAlreadyExists;
import com.barbershop.booking.procedures.exceptions.ProcedureNameAlreadyExists;
import com.barbershop.booking.procedures.exceptions.ProcedureNotActive;
import com.barbershop.booking.procedures.exceptions.ProcedureNotFound;
import com.barbershop.booking.users.exceptions.UserEmailAlreadyExists;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.barbershop.booking.users.exceptions.UserNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFound.class,
            BarberNotFound.class,
            CustomerNotFound.class,
            ProcedureNotFound.class,
            AppointmentNotFound.class,
            AppointmentProcedureNotFound.class

})
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request) {

        return build(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler({
            AppointmentTimeSlotUnavailable.class,
            CustomerPhoneAlreadyExists.class,
            CustomerUserAlreadyExists.class,
            BarberUserAlreadyExists.class,
            ProcedureNameAlreadyExists.class,
            UserEmailAlreadyExists.class,
            AppointmentCannotBeCanceled.class,
            AppointmentInvalidStatus.class,
            BarberNotActive.class,
            ProcedureNotActive.class
    })
    public ResponseEntity<ApiError> handleConflict(
            RuntimeException exception,
            HttpServletRequest request) {

        return build(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler({
            AppointmentInvalidTimeInterval.class,
            AppointmentProceduresRequired.class,
            AppointmentDuplicateProcedure.class,
            AppointmentProcedureBarberMismatch.class,
            BarberDoesNotHaveThisProcedure.class,
            BarberScheduleStartTimeMustBeBeforeEndTime.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            RuntimeException exception,
            HttpServletRequest request) {

        return build(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                        fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .sorted()
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                details
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleParameterValidation(
            ConstraintViolationException exception,
            HttpServletRequest request) {

        List<String> details = exception.getConstraintViolations().stream()
                .map(this::describe)
                .sorted()
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                details
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        String message = "Parameter '" + exception.getName()
                + "' has an invalid value: " + exception.getValue();

        return build(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request) {

        String message = "Required query parameter '"
                + exception.getParameterName()
                + "' is missing";

        return build(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        return build(
                HttpStatus.BAD_REQUEST,
                "Malformed or unreadable request body",
                request,
                List.of()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request) {

        return build(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                request,
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database integrity violation",
                request,
                List.of()
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request) {

        List<String> details = exception.getParameterValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors()
                        .stream()
                        .map(error -> result.getMethodParameter().getParameterName()
                                + ": " + error.getDefaultMessage()))
                .sorted()
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                details
        );
    }

    private String describe(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        String field = path.contains(".")
                ? path.substring(path.lastIndexOf(".") + 1)
                : path;

        return field + ": " + violation.getMessage();
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> details) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        );

        return ResponseEntity.status(status).body(error);
    }
}
