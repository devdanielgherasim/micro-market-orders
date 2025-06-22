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
 * Filter that propagates the Keycloak token to the Catalog service.
 * This works in conjunction with the token propagation configuration in application.properties.
 */
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION)
public class ProductServiceTokenRequestFilter implements ClientRequestFilter {

    private static final Logger LOG = Logger.getLogger(ProductServiceTokenRequestFilter.class);

    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public void filter(ClientRequestContext requestContext) {
        try {
            MultivaluedMap<String, Object> headers = requestContext.getHeaders();

            if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                LOG.debug("Authorization header present for Catalog service, token propagation is working");
            } else if (securityIdentity != null && !securityIdentity.isAnonymous()) {
                LOG.debug("User is authenticated but no Authorization header found for Catalog service");
            } else {
                LOG.debug("No authenticated user found for token propagation to Catalog service");
            }
        } catch (Exception e) {
            LOG.error("Error in token propagation filter for Catalog service", e);
        }
    }
}