package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.mappers.DeliveryMapper;
import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.core.application.queries.GetAllCouriersQuery;
import microarch.delivery.core.application.queries.GetAllCouriersQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {

    private final GetAllCouriersQueryHandler getAllCouriersQueryHandler;

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var createQueryResult = GetAllCouriersQuery.create();
        if (createQueryResult.isFailure())
            return ResponseEntity.badRequest().build();
        var query = createQueryResult.getValue();

        var handleResult = this.getAllCouriersQueryHandler.handle(query);
        if (handleResult.isFailure())
            return ResponseEntity.internalServerError().build();

        var couriers = handleResult.getValue().stream().map(DeliveryMapper.INSTANCE::toHttp).toList();
        return ResponseEntity.ok(couriers);
    }
}
