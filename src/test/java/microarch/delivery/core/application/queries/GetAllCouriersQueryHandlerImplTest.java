package microarch.delivery.core.application.queries;

import java.util.List;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllCouriersQueryHandlerImplTest {

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private GetAllCouriersQueryHandlerImpl handler;

    @Test
    void shouldReturnAllCouriersAsDtos() {
        var c1 = Courier.mustCreate("Alice", Location.mustCreate(1, 2));
        var c2 = Courier.mustCreate("Bob", Location.mustCreate(3, 4));
        when(courierRepository.findAll()).thenReturn(List.of(c1, c2));

        var result = handler.handle(GetAllCouriersQuery.create().getValue());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).hasSize(2);
        assertThat(result.getValue()).extracting("name").containsExactly("Alice", "Bob");
    }

    @Test
    void shouldReturnEmptyListWhenNoCouriers() {
        when(courierRepository.findAll()).thenReturn(List.of());

        var result = handler.handle(GetAllCouriersQuery.create().getValue());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEmpty();
    }
}
