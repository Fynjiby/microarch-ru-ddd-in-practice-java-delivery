package microarch.delivery.adapters.in.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import queues.basket.events.BasketEventsProto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketEventsConsumer {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @KafkaListener(topics = "${app.kafka.basket-events-topic}")
    public void listen(byte[] message) {
        try {
            var event = BasketEventsProto.BasketConfirmedIntegrationEvent.parseFrom(message);

            var commandResult = CreateOrderCommand.create(
                    UUID.fromString(event.getBasketId()),
                    event.getAddress().getCountry(),
                    event.getAddress().getCity(),
                    event.getAddress().getStreet(),
                    event.getAddress().getHouse(),
                    event.getAddress().getApartment(),
                    event.getVolume()
            );

            if (commandResult.isFailure()) {
                throw new RuntimeException("Invalid command: " + commandResult.getError());
            }

            var command = commandResult.getValue();
            var handleResult = this.createOrderCommandHandler.handle(command);

            if (handleResult.isFailure()) {
                throw new RuntimeException("Failed to handle command: " + handleResult.getError());
            }

            log.info("Order created successfully: {}", handleResult.getValue());

        } catch (InvalidProtocolBufferException ex) {
            throw new RuntimeException("Failed to parse protobuf message", ex);
        }
    }
}