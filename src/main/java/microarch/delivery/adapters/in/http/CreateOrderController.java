package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.adapters.in.http.mappers.DeliveryMapper;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(NewOrder newOrder) {
        var address = newOrder.getAddress();
        if (address == null)
            return ResponseEntity.badRequest().build();
        var createCommandResult = CreateOrderCommand.create(newOrder.getId(), address.getCountry(), address.getCity(),
                address.getStreet(), address.getHouse(), address.getApartment(),
                newOrder.getVolume() != null ? newOrder.getVolume() : 0);
        if (createCommandResult.isFailure())
            return ResponseEntity.badRequest().build();
        var command = createCommandResult.getValue();

        var handleResult = this.createOrderCommandHandler.handle(command);
        if (handleResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DeliveryMapper.INSTANCE.toCreateOrderResponse(handleResult.getValue()));
    }

}
