package cloud.microservices.orders.providers;

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
 * Generic MessageBodyReader for JSON deserialization.
 * This class replaces multiple type-specific MessageBodyReaders with a single generic implementation.
 * It can deserialize JSON to any object using Jackson's ObjectMapper.
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class GenericJsonMessageBodyReader implements MessageBodyReader<Object> {

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
                           MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        return objectMapper.readValue(entityStream, objectMapper.constructType(genericType != null ? genericType : type));
    }
}