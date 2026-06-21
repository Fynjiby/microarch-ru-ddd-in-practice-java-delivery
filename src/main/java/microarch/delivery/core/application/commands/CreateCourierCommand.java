package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Location;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateCourierCommand {

    private final String name;
    private final Location location;

    public static Result<CreateCourierCommand, Error> create(String name, int locationX, int locationY) {
        var err = Guard.againstNullOrEmpty(name, "name");
        if (err != null) return Result.failure(err);

        var locationResult = Location.create(locationX, locationY);
        if (locationResult.isFailure()) return Result.failure(locationResult.getError());

        return Result.success(new CreateCourierCommand(name, locationResult.getValue()));
    }
}
