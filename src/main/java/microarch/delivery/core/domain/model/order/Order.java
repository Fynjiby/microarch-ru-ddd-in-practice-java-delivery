package microarch.delivery.core.domain.model.order;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Result;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    private Order(UUID id) {
        this.id = id;
    }

    public static Result<Order, Error> create() {
        var order = new Order(UUID.randomUUID());
        return Result.success(order);
    }

    public static Order mustCreate() {
        return create().getValueOrThrow();
    }
}
