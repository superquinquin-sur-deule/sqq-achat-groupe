package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UserInfoResponse;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/admin")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "admin-auth")
public class AuthResource {

    private final SecurityIdentity securityIdentity;

    @Inject
    @IdToken
    Instance<JsonWebToken> idToken;

    public AuthResource(SecurityIdentity securityIdentity) {
        this.securityIdentity = securityIdentity;
    }

    @GET
    @Path("/me")
    public DataResponse<UserInfoResponse> me() {
        var name = getClaim("name", securityIdentity.getPrincipal().getName());
        var email = getClaim("email", "");
        return new DataResponse<>(new UserInfoResponse(name, email));
    }

    private String getClaim(String claimName, String defaultValue) {
        if (idToken.isResolvable()) {
            var token = idToken.get();
            Object claim = token.getClaim(claimName);
            if (claim != null) {
                return claim.toString();
            }
        }
        return defaultValue;
    }
}
