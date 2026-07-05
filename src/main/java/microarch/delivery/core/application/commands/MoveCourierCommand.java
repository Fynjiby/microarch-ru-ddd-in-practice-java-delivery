package microarch.delivery.core.application.commands;

import java.util.UUID;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveCourierCommand {

    private final UUID courierId;
    private final Location targetLocation;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, int targetX, int targetY) {
        var err = Guard.againstNullOrEmpty(courierId, "courierId");
        if (err != null)
            return Result.failure(err);

        var locationResult = Location.create(targetX, targetY);
        if (locationResult.isFailure())
            return Result.failure(locationResult.getError());

        return Result.success(new MoveCourierCommand(courierId, locationResult.getValue()));
    }
}
