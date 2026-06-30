package microarch.delivery.core.domain.model.kernel;

import java.util.List;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address extends ValueObject<Address> {

    private final String country;
    private final String city;
    private final String street;
    private final String house;
    private final String apartment;

    public static Result<Address, Error> create(
            String country, String city, String street, String house, String apartment) {
        var err = Guard.againstNullOrEmpty(country, "country");
        if (err != null) return Result.failure(err);

        err = Guard.againstNullOrEmpty(city, "city");
        if (err != null) return Result.failure(err);

        err = Guard.againstNullOrEmpty(street, "street");
        if (err != null) return Result.failure(err);

        err = Guard.againstNullOrEmpty(house, "house");
        if (err != null) return Result.failure(err);

        return Result.success(new Address(country, city, street, house, apartment));
    }

    public static Address mustCreate(
            String country, String city, String street, String house, String apartment) {
        return create(country, city, street, house, apartment).getValueOrThrow();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(country, city, street, house, apartment == null ? "" : apartment);
    }
}
