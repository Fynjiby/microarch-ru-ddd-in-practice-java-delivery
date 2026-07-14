package microarch.delivery.core.domain.model.order.events;

import java.util.UUID;
import libs.ddd.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class OrderAssignedDomainEvent extends DomainEvent {
    private final UUID orderId;

    public OrderAssignedDomainEvent(Order order) {
        super();
        this.orderId = order.getId();
    }
}