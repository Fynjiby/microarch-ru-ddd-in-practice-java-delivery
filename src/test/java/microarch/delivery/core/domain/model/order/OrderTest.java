package microarch.delivery.core.domain.model.order;

import org.junit.jupiter.api.Test;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Location LOCATION = Location.mustCreate(5, 5);
    private static final Volume VOLUME = Volume.mustCreate(10);

    @Test
    void shouldCreateWithStatusCreated() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.Created);
    }

    @Test
    void shouldCreateWithCorrectFields() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);

        assertThat(order.getId()).isEqualTo(ID);
        assertThat(order.getLocation()).isEqualTo(LOCATION);
        assertThat(order.getVolume()).isEqualTo(VOLUME);
    }

    @Test
    void shouldReturnErrorWhenIdIsNullOnCreate() {
        var result = Order.create(null, LOCATION, VOLUME);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenLocationIsNullOnCreate() {
        var result = Order.create(ID, null, VOLUME);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenVolumeIsNullOnCreate() {
        var result = Order.create(ID, LOCATION, null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldAssignWhenStatusIsCreated() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);

        var result = order.assign();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Assigned);
    }

    @Test
    void shouldReturnErrorWhenAssignAndStatusIsNotCreated() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);
        order.assign();

        var result = order.assign();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Assigned);
    }

    @Test
    void shouldCompleteWhenStatusIsAssigned() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);
        order.assign();

        var result = order.complete();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Completed);
    }

    @Test
    void shouldReturnErrorWhenCompleteAndStatusIsCreated() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);

        var result = order.complete();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Created);
    }

    @Test
    void shouldReturnErrorWhenCompleteAndStatusIsCompleted() {
        var order = Order.mustCreate(ID, LOCATION, VOLUME);
        order.assign();
        order.complete();

        var result = order.complete();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Completed);
    }
}
