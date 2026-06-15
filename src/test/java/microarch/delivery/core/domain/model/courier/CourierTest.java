package microarch.delivery.core.domain.model.courier;

import org.junit.jupiter.api.Test;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourierTest {

    private static final Location LOCATION = Location.mustCreate(5, 5);
    private static final Volume MAX_VOLUME = Volume.mustCreate(10);

    private static Order makeOrder(int volume) {
        return Order.mustCreate(UUID.randomUUID(), LOCATION, Volume.mustCreate(volume));
    }

    @Test
    void shouldCreateWithCorrectFields() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);

        assertThat(courier.getName()).isEqualTo("Ivan");
        assertThat(courier.getLocation()).isEqualTo(LOCATION);
        assertThat(courier.getMaxVolume()).isEqualTo(MAX_VOLUME);
        assertThat(courier.getCurrentVolume()).isEqualTo(Volume.mustCreate(0));
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void shouldCreateWithDefaultMaxVolume() {
        var courier = Courier.mustCreate("Ivan", LOCATION);

        assertThat(courier.getName()).isEqualTo("Ivan");
        assertThat(courier.getLocation()).isEqualTo(LOCATION);
        assertThat(courier.getMaxVolume()).isEqualTo(Volume.mustCreate(20));
        assertThat(courier.getCurrentVolume()).isEqualTo(Volume.mustCreate(0));
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void shouldReturnErrorWhenNameIsBlankOnCreateWithDefaultMaxVolume() {
        var result = Courier.create("", LOCATION);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenLocationIsNullOnCreateWithDefaultMaxVolume() {
        var result = Courier.create("Ivan", null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenNameIsBlankOnCreate() {
        var result = Courier.create("", LOCATION, MAX_VOLUME);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenLocationIsNullOnCreate() {
        var result = Courier.create("Ivan", null, MAX_VOLUME);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenMaxVolumeIsNullOnCreate() {
        var result = Courier.create("Ivan", LOCATION, null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnErrorWhenMaxVolumeExceedsLimitOnCreate() {
        var result = Courier.create("Ivan", LOCATION, Volume.mustCreate(21));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnTrueWhenCanTakeOrder() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);

        assertThat(courier.canTakeOrder(Volume.mustCreate(10))).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCannotTakeOrderDueToVolumeExceeded() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);

        assertThat(courier.canTakeOrder(Volume.mustCreate(11))).isFalse();
    }

    @Test
    void shouldReturnFalseWhenCannotTakeOrderDueToCurrentVolumeAlreadyFilled() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);
        courier.takeOrder(makeOrder(8));

        assertThat(courier.canTakeOrder(Volume.mustCreate(3))).isFalse();
    }

    @Test
    void shouldTakeOrderSuccessfully() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);
        var order = makeOrder(5);

        var result = courier.takeOrder(order);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
        assertThat(courier.getCurrentVolume()).isEqualTo(Volume.mustCreate(5));
    }

    @Test
    void shouldReturnErrorWhenTakeOrderExceedsMaxVolume() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);
        var order = makeOrder(11);

        var result = courier.takeOrder(order);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    void shouldCompleteAssignmentWhenCourierIsCloseEnough() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);
        var order = makeOrder(5);
        courier.takeOrder(order);

        var result = courier.completeAssignment(order.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).isEmpty();
        assertThat(courier.getCurrentVolume()).isEqualTo(Volume.mustCreate(0));
    }

    @Test
    void shouldReturnErrorWhenCompleteAssignmentAndCourierIsTooFar() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(5));
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(5, 5), MAX_VOLUME);
        courier.takeOrder(order);

        var result = courier.completeAssignment(order.getId());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void shouldReturnErrorWhenCompleteAssignmentNotFound() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);

        var result = courier.completeAssignment(UUID.randomUUID());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldMoveToNewLocation() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);
        var newLocation = Location.mustCreate(3, 7);

        var result = courier.move(newLocation);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getLocation()).isEqualTo(newLocation);
    }

    @Test
    void shouldThrowWhenMoveToNullLocation() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);

        assertThatThrownBy(() -> courier.move(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldReturnImmutableAssignments() {
        var courier = Courier.mustCreate("Ivan", LOCATION, MAX_VOLUME);
        courier.takeOrder(makeOrder(5));

        var assignments = courier.getAssignments();

        assertThatThrownBy(() -> assignments.clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
