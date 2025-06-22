package cloud.microservices.orders.clients;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.logging.Logger;

/**
 * Filter that propagates the Keycloak token to the Audit service.
 * This works in conjunction with the token propagation configuration in application.properties.
 */
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION)
public class AuditServiceTokenRequestFilter implements ClientRequestFilter {

    private static final Logger LOG = Logger.getLogger(AuditServiceTokenRequestFilter.class);

    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public void filter(ClientRequestContext requestContext) {
        try {
            MultivaluedMap<String, Object> headers = requestContext.getHeaders();

            if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                LOG.debug("Authorization header already present, token propagation is working");
            } else if (securityIdentity != null && !securityIdentity.isAnonymous()) {
                LOG.debug("User is authenticated but no Authorization header found");
            } else {
                LOG.debug("No authenticated user found for token propagation");
            }
        } catch (Exception e) {
            LOG.error("Error in token propagation filter", e);
        }
    }
}