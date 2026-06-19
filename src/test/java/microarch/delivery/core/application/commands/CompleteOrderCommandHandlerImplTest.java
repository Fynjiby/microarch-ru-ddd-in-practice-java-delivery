package microarch.delivery.core.application.commands;

import java.util.Optional;
import java.util.UUID;
import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompleteOrderCommandHandlerImplTest {

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CompleteOrderCommandHandlerImpl handler;

    private static final Location LOCATION = Location.mustCreate(5, 5);

    @Test
    void shouldCompleteOrderSuccessfully() {
        var order = Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(3));
        var courier = Courier.mustCreate("Ivan", LOCATION);
        courier.takeOrder(order);
        order.assign();

        var command = CompleteOrderCommand.create(courier.getId(), order.getId()).getValue();
        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).update(courier);
        verify(orderRepository).update(order);
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldReturnErrorWhenCourierNotFound() {
        var command = CompleteOrderCommand.create(UUID.randomUUID(), UUID.randomUUID()).getValue();
        when(courierRepository.findById(any())).thenReturn(Optional.empty());

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenOrderNotFound() {
        var courier = Courier.mustCreate("Ivan", LOCATION);
        var command = CompleteOrderCommand.create(courier.getId(), UUID.randomUUID()).getValue();
        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }
}
