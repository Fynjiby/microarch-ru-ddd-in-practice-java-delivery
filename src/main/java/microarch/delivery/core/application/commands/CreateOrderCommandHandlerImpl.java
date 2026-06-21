package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public UnitResult<Error> handle(CreateOrderCommand command) {
        var orderResult = Order.create(command.getOrderId(), command.getLocation(), command.getVolume());
        if (orderResult.isFailure()) return UnitResult.failure(orderResult.getError());

        var order = orderResult.getValue();
        orderRepository.add(order);
        domainEventPublisher.publish(List.of(order));

        return UnitResult.success();
    }
}
