package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.services.OrderDistributionDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderDistributionDomainService orderDistributionDomainService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public UnitResult<Error> handle(AssignOrderCommand command) {
        var orderOpt = orderRepository.findAnyCreated();
        if (orderOpt.isEmpty())
            return UnitResult.failure(Error.of("order.no.pending", "No pending orders available for assignment"));

        var order = orderOpt.get();
        var couriers = courierRepository.findAll();

        var distributeResult = orderDistributionDomainService.distributeOrder(order, couriers);
        if (distributeResult.isFailure())
            return UnitResult.failure(distributeResult.getError());

        var courier = distributeResult.getValue();
        orderRepository.update(order);
        courierRepository.update(courier);
        domainEventPublisher.publish(List.of(order, courier));

        return UnitResult.success();
    }
}
