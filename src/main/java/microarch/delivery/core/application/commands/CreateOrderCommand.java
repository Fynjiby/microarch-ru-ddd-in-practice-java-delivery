package microarch.delivery.core.application.commands;

import java.util.UUID;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.kernel.Address;
import microarch.delivery.core.domain.model.kernel.Volume;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {

    private final UUID orderId;
    private final Address address;
    private final Volume volume;

    public static Result<CreateOrderCommand, Error> create(
            UUID orderId,
            String country,
            String city,
            String street,
            String house,
            String apartment,
            int volume) {
        var err = Guard.againstNullOrEmpty(orderId, "orderId");
        if (err != null) return Result.failure(err);

        var addressResult = Address.create(country, city, street, house, apartment);
        if (addressResult.isFailure()) return Result.failure(addressResult.getError());

        var volumeResult = Volume.create(volume);
        if (volumeResult.isFailure()) return Result.failure(volumeResult.getError());

        return Result.success(new CreateOrderCommand(orderId, addressResult.getValue(), volumeResult.getValue()));
    }
}
