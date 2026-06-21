package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoveCourierCommandHandlerImpl implements MoveCourierCommandHandler {

    private final CourierRepository courierRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public UnitResult<Error> handle(MoveCourierCommand command) {
        var courierOpt = courierRepository.findById(command.getCourierId());
        if (courierOpt.isEmpty()) return UnitResult.failure(GeneralErrors.notFound("Courier", command.getCourierId()));

        var courier = courierOpt.get();
        var moveResult = courier.move(command.getTargetLocation());
        if (moveResult.isFailure()) return UnitResult.failure(moveResult.getError());

        courierRepository.update(courier);
        domainEventPublisher.publish(List.of(courier));

        return UnitResult.success();
    }
}
