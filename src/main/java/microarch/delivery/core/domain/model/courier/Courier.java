package microarch.delivery.core.domain.model.courier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.Order;

@Entity
@Table(name = "couriers")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Courier extends Aggregate<UUID> {

    private static final int MAX_ALLOWED_VOLUME = 20;

    @Column(name = "name")
    private String name;

    @Embedded
    private Location location;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "max_volume"))
    private Volume maxVolume;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "current_volume"))
    private Volume currentVolume;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "courier_id")
    private final List<Assignment> assignments = new ArrayList<>();

    private Courier(UUID id, String name, Location location, Volume maxVolume) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.maxVolume = maxVolume;
        this.currentVolume = Volume.mustCreate(0);
    }

    public List<Assignment> getAssignments() {
        return List.copyOf(assignments);
    }

    public static Result<Courier, Error> create(String name, Location location) {
        return create(name, location, Volume.mustCreate(MAX_ALLOWED_VOLUME));
    }

    public static Result<Courier, Error> create(String name, Location location, Volume maxVolume) {
        if (name == null || name.isBlank()) {
            return Result.failure(GeneralErrors.valueIsRequired("name"));
        }
        if (location == null) {
            return Result.failure(GeneralErrors.valueIsRequired("location"));
        }
        if (maxVolume == null) {
            return Result.failure(GeneralErrors.valueIsRequired("maxVolume"));
        }
        if (maxVolume.getValue() > MAX_ALLOWED_VOLUME) {
            return Result.failure(
                    GeneralErrors.valueMustBeLessOrEqual("maxVolume", maxVolume.getValue(), MAX_ALLOWED_VOLUME));
        }
        return Result.success(new Courier(UUID.randomUUID(), name, location, maxVolume));
    }

    public static Courier mustCreate(String name, Location location) {
        return create(name, location).getValueOrThrow();
    }

    public static Courier mustCreate(String name, Location location, Volume maxVolume) {
        return create(name, location, maxVolume).getValueOrThrow();
    }

    public boolean canTakeOrder(Volume orderVolume) {
        Objects.requireNonNull(orderVolume, "orderVolume");
        return currentVolume.add(orderVolume).isLessOrEqualTo(maxVolume);
    }

    public UnitResult<Error> takeOrder(Order order) {
        Objects.requireNonNull(order, "order");
        Volume orderVolume = order.getVolume();
        if (!canTakeOrder(orderVolume)) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("volume", orderVolume.getValue()));
        }
        var assignment = Assignment.mustCreate(order, orderVolume, order.getLocation());
        assignments.add(assignment);
        currentVolume = currentVolume.add(orderVolume);
        return UnitResult.success();
    }

    public UnitResult<Error> completeAssignment(UUID orderId) {
        Objects.requireNonNull(orderId, "orderId");
        var assignment = assignments.stream().filter(a -> a.getOrderId().equals(orderId)).findFirst().orElse(null);
        if (assignment == null) {
            return UnitResult.failure(GeneralErrors.notFound("Assignment", orderId));
        }
        var result = assignment.complete(this.location);
        if (result.isFailure()) {
            return result;
        }
        assignments.remove(assignment);
        currentVolume = currentVolume.subtract(assignment.getVolume());
        return UnitResult.success();
    }

    public UnitResult<Error> move(Location newLocation) {
        Objects.requireNonNull(newLocation, "newLocation");
        this.location = newLocation;
        return UnitResult.success();
    }
}
