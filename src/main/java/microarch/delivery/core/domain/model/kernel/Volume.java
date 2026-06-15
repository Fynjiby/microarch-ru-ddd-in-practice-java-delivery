package microarch.delivery.core.domain.model.kernel;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 999;

    @Column(name = "volume")
    private int value;

    public static Result<Volume, Error> create(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            return Result.failure(GeneralErrors.valueIsOutOfRange("value", value, MIN_VALUE, MAX_VALUE));
        }
        return Result.success(new Volume(value));
    }

    public static Volume mustCreate(int value) {
        return create(value).getValueOrThrow();
    }

    public Volume add(Volume other) {
        return mustCreate(this.value + other.value);
    }

    public Volume subtract(Volume other) {
        return mustCreate(this.value - other.value);
    }

    public boolean isLessThan(Volume other) {
        return this.compareTo(other) < 0;
    }

    public boolean isLessOrEqualTo(Volume other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isGreaterThan(Volume other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterOrEqualTo(Volume other) {
        return this.compareTo(other) >= 0;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.value);
    }
}
