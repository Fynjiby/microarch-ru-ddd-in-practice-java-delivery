package microarch.delivery.core.domain.model.order;

import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Status;
import microarch.delivery.core.domain.model.kernel.Volume;

@Entity
@Table(name = "assignments")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Assignment extends BaseEntity<UUID> {
    private static final int COMPLETION_DISTANCE = 1;

    @Column(name = "order_id")
    @Getter
    private final UUID orderId;

    @Embedded
    @Getter
    private final Volume volume;

    @Embedded
    @Getter
    private final Location location;

    @Column(name = "status")
    @Getter
    private Status status;

    private Assignment(Order order, Volume volume, Location location, Status status) {
        super(UUID.randomUUID());
        this.orderId = order.getId();
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Result<Assignment, Error> create(Order order, Volume volume, Location location) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        var assignment = new Assignment(order, volume, location, Status.Assigned);
        return Result.success(assignment);
    }

    public UnitResult<Error> complete(Location currentLocation) {
        Objects.requireNonNull(currentLocation, "currentLocation");

        int distance = currentLocation.distanceTo(this.location);
        if (distance > COMPLETION_DISTANCE) {
            return UnitResult.failure(GeneralErrors.valueMustBeLessOrEqual("distance", distance, COMPLETION_DISTANCE));
        }
        this.status = Status.Completed;
        return UnitResult.success();
    }

    public static Assignment mustCreate(Order order, Volume volume, Location location) {
        return create(order, volume, location).getValueOrThrow();
    }
}
