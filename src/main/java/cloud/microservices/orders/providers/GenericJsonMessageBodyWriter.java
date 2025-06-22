package cloud.microservices.orders.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Generic MessageBodyWriter for JSON serialization.
 * This class replaces multiple type-specific MessageBodyWriters with a single generic implementation.
 * It can serialize any object to JSON using Jackson's ObjectMapper.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GenericJsonMessageBodyWriter implements MessageBodyWriter<Object> {

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        httpHeaders.putSingle("Content-Type", MediaType.APPLICATION_JSON);

        objectMapper.writeValue(entityStream, object);
    }
}