package microarch.delivery.core.application.commands;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.GeoService;
import microarch.delivery.core.ports.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final GeoService geoService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public Result<UUID, Error> handle(CreateOrderCommand command) {
        var location = geoService.getLocation(command.getAddress());
        var orderResult = Order.create(command.getOrderId(), location, command.getVolume());
        if (orderResult.isFailure())
            return Result.failure(orderResult.getError());

        var order = orderResult.getValue();
        orderRepository.add(order);
        domainEventPublisher.publish(List.of(order));

        return Result.success(order.getId());
    }
}
