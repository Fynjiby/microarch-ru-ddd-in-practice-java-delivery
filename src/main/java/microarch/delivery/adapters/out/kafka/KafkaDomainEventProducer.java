package microarch.delivery.adapters.out.kafka;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import libs.ddd.DomainEvent;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import queues.order.events.OrderEventsProto.OrderAssignedIntegrationEvent;
import queues.order.events.OrderEventsProto.OrderCompletedIntegrationEvent;

@Component
@RequiredArgsConstructor
public class KafkaDomainEventProducer implements DomainEventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.order-events-topic}")
    private String topic;

    public void produce(DomainEvent event) {
        try {
            var messageData = switch (event) {
                case OrderAssignedDomainEvent e ->
                        Map.entry(e.getOrderId().toString(), mapToProto(e));
                case OrderCompletedDomainEvent e ->
                        Map.entry(e.getOrderId().toString(), mapToProto(e));
                default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
            };
            String kafkaKey = messageData.getKey();
            byte[] kafkaValue = messageData.getValue().toByteArray();
            kafkaTemplate.send(topic, kafkaKey, kafkaValue).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka publish interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Kafka publish failed", e);
        }
    }

    private OrderAssignedIntegrationEvent mapToProto(OrderAssignedDomainEvent event) {
        return OrderAssignedIntegrationEvent.newBuilder().setOrderId(event.getOrderId().toString()).build();
    }

    private OrderCompletedIntegrationEvent mapToProto(OrderCompletedDomainEvent event) {
        return OrderCompletedIntegrationEvent.newBuilder().setOrderId(event.getOrderId().toString()).build();
    }
}

