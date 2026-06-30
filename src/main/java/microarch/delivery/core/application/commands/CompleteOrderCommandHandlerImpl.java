package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompleteOrderCommandHandlerImpl implements CompleteOrderCommandHandler {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public UnitResult<Error> handle(CompleteOrderCommand command) {
        var courierOpt = courierRepository.findById(command.getCourierId());
        if (courierOpt.isEmpty()) return UnitResult.failure(GeneralErrors.notFound("Courier", command.getCourierId()));

        var orderOpt = orderRepository.findById(command.getOrderId());
        if (orderOpt.isEmpty()) return UnitResult.failure(GeneralErrors.notFound("Order", command.getOrderId()));

        var courier = courierOpt.get();
        var order = orderOpt.get();

        var completeAssignmentResult = courier.completeAssignment(order.getId());
        if (completeAssignmentResult.isFailure()) return completeAssignmentResult;

        var completeOrderResult = order.complete();
        if (completeOrderResult.isFailure()) return completeOrderResult;

        courierRepository.update(courier);
        orderRepository.update(order);
        domainEventPublisher.publish(List.of(courier, order));

        return UnitResult.success();
    }
}
