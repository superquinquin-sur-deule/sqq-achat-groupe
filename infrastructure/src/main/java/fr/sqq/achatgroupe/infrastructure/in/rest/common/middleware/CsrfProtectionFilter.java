package fr.sqq.achatgroupe.infrastructure.in.rest.common.middleware;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@PreMatching
public class CsrfProtectionFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "app.csrf.enabled", defaultValue = "true")
    boolean enabled;

    private static final String ADMIN_PATH_PREFIX = "/api/admin/";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!enabled) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        if (!path.startsWith(ADMIN_PATH_PREFIX)) {
            return;
        }

        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            return;
        }

        String header = requestContext.getHeaderString("X-Requested-With");
        if (!"XMLHttpRequest".equals(header)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("CSRF protection: missing X-Requested-With header")
                            .build());
        }
    }
}
