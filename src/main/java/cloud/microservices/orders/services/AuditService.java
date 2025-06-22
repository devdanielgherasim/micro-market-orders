package cloud.microservices.orders.services;

import cloud.microservices.orders.clients.AuditLogCreateRequest;
import cloud.microservices.orders.clients.AuditServiceClient;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * Service for sending audit logs to the audit service.
 */
@ApplicationScoped
public class AuditService {

    private static final Logger LOG = Logger.getLogger(AuditService.class);
    private static final String DEFAULT_USER = "system";

    @Inject
    @RestClient
    AuditServiceClient auditServiceClient;

    @Inject
    SecurityIdentity securityIdentity;

    /**
     * Get the authenticated user's name or the default user if not authenticated.
     *
     * @return the authenticated user's name or the default user
     */
    private String getAuthenticatedUser() {
        if (securityIdentity != null && securityIdentity.getPrincipal() != null) {
            return securityIdentity.getPrincipal().getName();
        }
        return DEFAULT_USER;
    }

    /**
     * Log a create operation.
     *
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @param details    additional details
     */
    public void logCreate(String entityType, String entityId, String details) {
        logAction("CREATE", entityType, entityId, getAuthenticatedUser(), details);
    }

    /**
     * Log an update operation.
     *
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @param details    additional details
     */
    public void logUpdate(String entityType, String entityId, String details) {
        logAction("UPDATE", entityType, entityId, getAuthenticatedUser(), details);
    }

    /**
     * Log a delete operation.
     *
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @param details    additional details
     */
    public void logDelete(String entityType, String entityId, String details) {
        logAction("DELETE", entityType, entityId, getAuthenticatedUser(), details);
    }

    /**
     * Log a read operation.
     *
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @param details    additional details
     */
    public void logRead(String entityType, String entityId, String details) {
        logAction("READ", entityType, entityId, getAuthenticatedUser(), details);
    }

    /**
     * Log an action.
     *
     * @param action     the action performed
     * @param entityType the type of entity
     * @param entityId   the ID of the entity
     * @param username   the username who performed the action
     * @param details    additional details
     */
    public void logAction(String action, String entityType, String entityId, String username, String details) {
        try {
            LOG.debug("Creating audit log request: action=" + action + ", entityType=" + entityType + 
                     ", entityId=" + entityId + ", username=" + username);

            AuditLogCreateRequest request = AuditLogCreateRequest.of(action, entityType, entityId, username, details);
            try (Response response = auditServiceClient.createAuditLog(request)) {
                int status = response.getStatus();

                if (status == Response.Status.CREATED.getStatusCode()) {
                    LOG.debug("Successfully created audit log: action=" + action + ", entityType=" + entityType + 
                             ", entityId=" + entityId + ", status=" + status);
                } else if (status == Response.Status.UNAUTHORIZED.getStatusCode()) {
                    LOG.error("Authentication failed when creating audit log: action=" + action + 
                             ", entityType=" + entityType + ", entityId=" + entityId + 
                             ". Check credentials in AuditServiceClient.");
                } else if (status == Response.Status.FORBIDDEN.getStatusCode()) {
                    LOG.error("Authorization failed when creating audit log: action=" + action + 
                             ", entityType=" + entityType + ", entityId=" + entityId + 
                             ". User may not have required permissions.");
                } else {
                    LOG.warn("Failed to create audit log: action=" + action + ", entityType=" + entityType + 
                            ", entityId=" + entityId + ", status=" + status);
                }
            }
        } catch (jakarta.ws.rs.ProcessingException e) {
            LOG.error("Network or connection error creating audit log: action=" + action + 
                     ", entityType=" + entityType + ", entityId=" + entityId + 
                     ". Audit service may be unavailable.", e);
        } catch (Exception e) {
            LOG.error("Error creating audit log: action=" + action + ", entityType=" + entityType + 
                     ", entityId=" + entityId, e);
        }
    }
}
