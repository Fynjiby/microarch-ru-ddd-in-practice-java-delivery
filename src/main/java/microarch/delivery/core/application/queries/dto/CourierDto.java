package microarch.delivery.core.application.queries.dto;

import java.util.UUID;

public record CourierDto(UUID id, String name, int locationX, int locationY) {
}
