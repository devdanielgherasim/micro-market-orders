package cloud.microservices.orders.clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST client for the Audit Service.
 */
@Path("/api/audit-logs")
@RegisterRestClient(configKey = "audit-service")
public interface AuditServiceClient {

    /**
     * Create a new audit log entry.
     *
     * @param auditLogCreateRequest the audit log data
     * @return the response from the audit service
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createAuditLog(AuditLogCreateRequest auditLogCreateRequest);
}