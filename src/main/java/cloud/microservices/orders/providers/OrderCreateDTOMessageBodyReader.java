package cloud.microservices.orders.providers;

import cloud.microservices.orders.dtos.OrderCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Custom MessageBodyReader for OrderCreateDTO objects.
 * Ensures proper JSON deserialization of OrderCreateDTO objects.
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class OrderCreateDTOMessageBodyReader implements MessageBodyReader<OrderCreateDTO> {

    @Inject
    ObjectMapper objectMapper;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return OrderCreateDTO.class.isAssignableFrom(type);
    }

    @Override
    public OrderCreateDTO readFrom(Class<OrderCreateDTO> type, Type genericType, Annotation[] annotations,
                                  MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        
        // Use the injected ObjectMapper to deserialize the JSON input stream to an OrderCreateDTO object
        return objectMapper.readValue(entityStream, OrderCreateDTO.class);
    }
}