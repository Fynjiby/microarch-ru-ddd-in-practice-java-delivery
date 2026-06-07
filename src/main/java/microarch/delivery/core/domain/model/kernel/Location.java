package microarch.delivery.core.domain.model.kernel;

import java.util.List;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {
    private static final int MIN_COORDINATE = 1;
    private static final int MAX_COORDINATE = 10;
    private int x;
    private int y;

    private Location() {
        // для JPA
    }

    public static Result<Location, Error> create(int x, int y) {
        if (x < MIN_COORDINATE || x > MAX_COORDINATE) {
            return Result.failure(GeneralErrors.valueIsOutOfRange("x", x, MIN_COORDINATE, MAX_COORDINATE));
        }
        if (y < MIN_COORDINATE || y > MAX_COORDINATE) {
            return Result.failure(GeneralErrors.valueIsOutOfRange("y", y, MIN_COORDINATE, MAX_COORDINATE));
        }
        return Result.success(new Location(x, y));
    }

    public static Location mustCreate(int x, int y) {
        return create(x, y).getValueOrThrow();
    }

    public int distanceTo(Location location) {
        return Math.abs(this.x - location.x) + Math.abs(this.y - location.y);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }
}
