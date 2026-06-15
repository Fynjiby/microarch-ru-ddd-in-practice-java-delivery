package microarch.delivery.core.domain.model.order;

import java.util.UUID;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    @Embedded
    private Location location;

    @Embedded
    private Volume volume;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume) {
        this.id = id;
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.Created;
    }

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        if (id == null) {
            return Result.failure(GeneralErrors.valueIsRequired("id"));
        }
        if (location == null) {
            return Result.failure(GeneralErrors.valueIsRequired("location"));
        }
        if (volume == null) {
            return Result.failure(GeneralErrors.valueIsRequired("volume"));
        }
        return Result.success(new Order(id, location, volume));
    }

    public static Order mustCreate(UUID id, Location location, Volume volume) {
        return create(id, location, volume).getValueOrThrow();
    }

    public UnitResult<Error> assign() {
        if (!this.status.canTransitionTo(OrderStatus.Assigned)) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("status", this.status));
        }
        this.status = OrderStatus.Assigned;
        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (!this.status.canTransitionTo(OrderStatus.Completed)) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("status", this.status));
        }
        this.status = OrderStatus.Completed;
        return UnitResult.success();
    }
}
