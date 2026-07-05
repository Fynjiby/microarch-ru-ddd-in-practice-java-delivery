package microarch.delivery.core.application.queries;

import java.util.List;
import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.queries.dto.OrderDto;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {

    private final OrderRepository orderRepository;

    @Override
    public Result<List<OrderDto>, Error> handle(GetNotCompletedOrdersQuery query) {
        var orders = orderRepository.findAllNotCompleted();
        var dtos = orders.stream().map(o -> new OrderDto(o.getId(), o.getLocation().getX(), o.getLocation().getY()))
                .toList();
        return Result.success(dtos);
    }
}
