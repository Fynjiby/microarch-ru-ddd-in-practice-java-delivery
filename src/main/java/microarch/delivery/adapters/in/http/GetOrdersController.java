package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetOrdersApi;
import microarch.delivery.adapters.in.http.mappers.DeliveryMapper;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersQuery;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetOrdersController implements GetOrdersApi {

    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var createQueryResult = GetNotCompletedOrdersQuery.create();
        if (createQueryResult.isFailure())
            return ResponseEntity.badRequest().build();
        var query = createQueryResult.getValue();

        var handleResult = this.getNotCompletedOrdersQueryHandler.handle(query);
        if (handleResult.isFailure())
            return ResponseEntity.internalServerError().build();

        var orders = handleResult.getValue().stream().map(DeliveryMapper.INSTANCE::toHttp).toList();
        return ResponseEntity.ok(orders);
    }
}
