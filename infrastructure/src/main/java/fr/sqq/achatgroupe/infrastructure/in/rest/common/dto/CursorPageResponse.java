package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.util.List;

public record CursorPageResponse<T>(List<T> data, PageInfo pageInfo) {

    public record PageInfo(String endCursor, boolean hasNext) {}
}
