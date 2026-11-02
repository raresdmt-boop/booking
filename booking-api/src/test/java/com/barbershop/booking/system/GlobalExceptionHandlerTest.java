package com.barbershop.booking.system;

import com.barbershop.booking.appointments.exceptions.AppointmentInvalidTimeInterval;
import com.barbershop.booking.barbers.dtos.BarberCreateRequest;
import com.barbershop.booking.barbers.exceptions.BarberNotFound;
import com.barbershop.booking.users.exceptions.UserEmailAlreadyExists;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import com.barbershop.booking.users.exceptions.UserNotFound;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import jakarta.servlet.ServletException;

import java.util.Set;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@WebMvcTest(ProbeController.class)
@Import({ProbeController.class, GlobalExceptionHandler.class})
@DisplayName("GlobalExceptionHandler - integrare web")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("UserNotFound returns 404 Not Found")    void userNotFound_becomes404() throws Exception {

        mockMvc.perform(get("/probe/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User ID not found"))
                .andExpect(jsonPath("$.path").value("/probe/user-not-found"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("BarberNotFound returns 404 Not Found")
    void barberNotFound_becomes404() throws Exception {

        mockMvc.perform(get("/probe/barber-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Barber not found"))
                .andExpect(jsonPath("$.path").value("/probe/barber-not-found"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("UserEmailAlreadyExists returns 409 Conflict")
    void userEmailAlreadyExists_becomes409() throws Exception {

        mockMvc.perform(get("/probe/email-already-exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/probe/email-already-exists"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("AppointmentInvalidTimeInterval returns 400 Bad Request")
    void appointmentInvalidTimeInterval_becomes400() throws Exception {

        mockMvc.perform(get("/probe/invalid-time-interval"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/probe/invalid-time-interval"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("Invalid BarberCreateRequest returns 400 with validation details")
    void invalidBarberCreateRequest_returns400() throws Exception {

        String json = """
            {
                "firstName": "",
                "lastName": "",
                "phone": ""
            }
            """;

        mockMvc.perform(post("/probe/barber-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.length()").value(4))
                .andExpect(jsonPath("$.details[0]").value("firstName: First name is required"))
                .andExpect(jsonPath("$.details[1]").value("lastName: Last name is required"))
                .andExpect(jsonPath("$.details[2]").value("phone: Phone is required"))
                .andExpect(jsonPath("$.details[3]").value("userId: User ID is required"));
    }

    @Test
    @DisplayName("Invalid path variable type returns 400 Bad Request")
    void invalidPathVariableType_returns400() throws Exception {

        mockMvc.perform(get("/probe/barbers/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Parameter 'id' has an invalid value: abc"))
                .andExpect(jsonPath("$.path").value("/probe/barbers/abc"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("Missing required request parameter returns 400 Bad Request")
    void missingRequestParameter_returns400() throws Exception {

        mockMvc.perform(get("/probe/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Required query parameter 'barberId' is missing"))
                .andExpect(jsonPath("$.path").value("/probe/search"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("Malformed JSON returns 400 Bad Request")
    void malformedJson_returns400() throws Exception {

        String json = """
            {
                "userId": 1,
                "firstName":
            }
            """;

        mockMvc.perform(post("/probe/barber-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Malformed or unreadable request body"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("Unknown endpoint returns 404 Not Found")
    void unknownEndpoint_returns404() throws Exception {

        mockMvc.perform(get("/probe/unknown/endpoint"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.path").value("/probe/unknown/endpoint"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("Data integrity violation returns 500 with a generic message")
    void dataIntegrityViolation_returns500() throws Exception {

        mockMvc.perform(get("/probe/data-integrity"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Database integrity violation"))
                .andExpect(jsonPath("$.path").value("/probe/data-integrity"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("Constraint violation returns 400 Bad Request")
    void constraintViolation_returns400() throws Exception {

        mockMvc.perform(get("/probe/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @DisplayName("ApiError response contains a timestamp")
    void apiError_containsTimestamp() throws Exception {

        mockMvc.perform(get("/probe/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    @DisplayName("Invalid method parameter returns 400 Bad Request")
    void invalidMethodParameter_returns400() throws Exception {

        mockMvc.perform(get("/probe/positive")
                        .param("minAge", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]")
                        .value("minAge: must be greater than 0"));
    }

    @Test
    @DisplayName("Unexpected exception is not handled by a specific handler")
    void unexpectedException_isNotHandled() {

        assertThrows(ServletException.class, () ->
                mockMvc.perform(get("/probe/unexpected-error"))
        );
    }

    @Test
    @DisplayName("ApiError timestamp is a valid ISO-8601 instant")
    void apiError_timestampIsValid() throws Exception {

        mockMvc.perform(get("/probe/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(result -> {

                    String response = result.getResponse().getContentAsString();

                    String timestamp = JsonPath.read(response, "$.timestamp");

                    assertDoesNotThrow(() -> Instant.parse(timestamp));
                });
    }

}

@TestComponent
@RestController
@RequestMapping("/probe")
class ProbeController {

    @GetMapping("/user-not-found")
    void userNotFound() {
        throw new UserNotFound();
    }

    @GetMapping("/barber-not-found")
    void barberNotFound() {
        throw new BarberNotFound();
    }

    @GetMapping("/email-already-exists")
    void emailAlreadyExists() {
        throw new UserEmailAlreadyExists();
    }

    @GetMapping("/invalid-time-interval")
    void invalidTimeInterval() {
        throw new AppointmentInvalidTimeInterval();
    }

    @PostMapping("/barber-validation")
    void barberValidation(@Valid @RequestBody BarberCreateRequest request) {
    }

    @GetMapping("/barbers/{id}")
    void barberById(@PathVariable Long id) {
    }

    @GetMapping("/search")
    void search(@RequestParam Long barberId) {
    }

    @GetMapping("/data-integrity")
    void dataIntegrity() {
        throw new DataIntegrityViolationException(
                "Database constraint violation: uk_users_email");
    }

    @GetMapping("/constraint-violation")
    void constraintViolation() {
        throw new ConstraintViolationException(
                "Validation failed",
                Set.of()
        );
    }

    @GetMapping("/positive")
    void positive(@RequestParam @Positive Integer minAge) {
    }

    @GetMapping("/unexpected-error")
    void unexpectedError() {
        throw new NullPointerException("Unexpected internal error");
    }
}