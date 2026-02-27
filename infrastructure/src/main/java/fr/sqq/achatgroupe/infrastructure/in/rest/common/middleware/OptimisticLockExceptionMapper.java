package fr.sqq.achatgroupe.infrastructure.in.rest.common.middleware;

import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProblemDetailResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @Override
    public Response toResponse(OptimisticLockException e) {
        var problem = new ProblemDetailResponse(
                "https://api.sqq.fr/errors/conflict",
                "Conflit de modification",
                409,
                "Un conflit est survenu, veuillez réessayer"
        );
        return Response.status(409)
                .type(PROBLEM_JSON)
                .entity(problem)
                .build();
    }
}
