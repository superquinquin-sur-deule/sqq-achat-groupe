package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.util.List;

public record UserInfoResponse(String name, String email, List<String> roles) {
}
