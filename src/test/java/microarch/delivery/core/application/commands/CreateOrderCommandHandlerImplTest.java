package microarch.delivery.core.application.commands;

import java.util.UUID;
import libs.ddd.DomainEventPublisher;
import microarch.delivery.core.domain.model.kernel.Address;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.GeoService;
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
class CreateOrderCommandHandlerImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private GeoService geoService;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CreateOrderCommandHandlerImpl handler;

    @Test
    void shouldCreateOrderSuccessfully() {
        var command = CreateOrderCommand.create(
                UUID.randomUUID(), "Russia", "Moscow", "Lenina", "1", "10", 5).getValue();
        when(geoService.getLocation(any())).thenReturn(Location.mustCreate(3, 4));

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(orderRepository).add(any());
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldCallGeoServiceWithCommandAddress() {
        var address = Address.mustCreate("Russia", "Moscow", "Lenina", "1", null);
        var command = CreateOrderCommand.create(
                UUID.randomUUID(), "Russia", "Moscow", "Lenina", "1", null, 3).getValue();
        when(geoService.getLocation(any())).thenReturn(Location.mustCreate(7, 7));

        handler.handle(command);

        verify(geoService).getLocation(any());
    }
}
