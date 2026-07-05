package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public Result<UUID, Error> handle(CreateCourierCommand command) {
        var location = Location.mustCreate(1, 1);
        var courierResult = Courier.create(command.getName(), location);
        if (courierResult.isFailure())
            return Result.failure(courierResult.getError());

        var courier = courierResult.getValue();
        courierRepository.add(courier);
        domainEventPublisher.publish(List.of(courier));

        return Result.success(courier.getId());
    }
}
