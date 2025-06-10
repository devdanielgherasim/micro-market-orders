package cloud.microservices.orders.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception mapper to handle common exceptions and return standardized error responses.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error("Exception occurred", exception);

        switch (exception) {
            case NotFoundException ignored1 -> {
                return buildResponse(Response.Status.NOT_FOUND, exception.getMessage());
            }
            case ConstraintViolationException constraintViolationException -> {
                return handleConstraintViolation((ConstraintViolationException) exception);
            }
            case IllegalArgumentException ignored -> {
                return buildResponse(Response.Status.BAD_REQUEST, exception.getMessage());
            }
            default -> {
                return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                        "An unexpected error occurred. Please contact support.");
            }
        }
    }

    private Response handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> getPropertyPath(violation),
                        ConstraintViolation::getMessage,
                        (error1, error2) -> error1 + ", " + error2
                ));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(buildErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), 
                        "Validation error", errors))
                .build();
    }

    private String getPropertyPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        // Remove method name from property path if present
        int lastDotIndex = propertyPath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            propertyPath = propertyPath.substring(lastDotIndex + 1);
        }
        return propertyPath;
    }

    private Response buildResponse(Response.Status status, String message) {
        return Response.status(status)
                .entity(buildErrorResponse(status.getStatusCode(), message, null))
                .build();
    }

    private Map<String, Object> buildErrorResponse(int status, String message, Map<String, String> errors) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status);
        errorResponse.put("message", message);
        errorResponse.put("path", uriInfo.getPath());

        if (errors != null && !errors.isEmpty()) {
            errorResponse.put("errors", errors);
        }

        return errorResponse;
    }
}
