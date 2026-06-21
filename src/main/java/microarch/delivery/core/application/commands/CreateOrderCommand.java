package microarch.delivery.core.application.commands;

import java.util.UUID;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {

    private final UUID orderId;
    private final Location location;
    private final Volume volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderId, int locationX, int locationY, int volume) {
        var err = Guard.againstNullOrEmpty(orderId, "orderId");
        if (err != null) return Result.failure(err);

        var locationResult = Location.create(locationX, locationY);
        if (locationResult.isFailure()) return Result.failure(locationResult.getError());

        var volumeResult = Volume.create(volume);
        if (volumeResult.isFailure()) return Result.failure(volumeResult.getError());

        return Result.success(new CreateOrderCommand(orderId, locationResult.getValue(), volumeResult.getValue()));
    }
}
