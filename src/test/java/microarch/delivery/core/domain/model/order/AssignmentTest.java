package microarch.delivery.core.domain.model.order;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Status;
import microarch.delivery.core.domain.model.kernel.Volume;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignmentTest {

    private static final Volume VOLUME = Volume.mustCreate(10);
    private static final Location LOCATION = Location.mustCreate(5, 5);
    private static final Order ORDER = Order.mustCreate(UUID.randomUUID(), LOCATION, VOLUME);

    @Test
    void shouldCreateAssignmentWithAssignedStatus() {
        var result = Assignment.create(ORDER, VOLUME, LOCATION);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getStatus()).isEqualTo(Status.Assigned);
    }

    @Test
    void shouldCreateAssignmentWithCorrectFields() {
        var assignment = Assignment.mustCreate(ORDER, VOLUME, LOCATION);

        assertThat(assignment.getOrderId()).isEqualTo(ORDER.getId());
        assertThat(assignment.getVolume()).isEqualTo(VOLUME);
        assertThat(assignment.getLocation()).isEqualTo(LOCATION);
    }

    @Test
    void shouldCompleteWhenCourierIsAtSameLocation() {
        var assignment = Assignment.mustCreate(ORDER, VOLUME, LOCATION);

        var result = assignment.complete(LOCATION);

        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Completed);
    }

    @Test
    void shouldCompleteWhenCourierIsWithinCompletionDistance() {
        var assignment = Assignment.mustCreate(ORDER, VOLUME, Location.mustCreate(5, 5));
        var nearLocation = Location.mustCreate(5, 6);

        var result = assignment.complete(nearLocation);

        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Completed);
    }

    @Test
    void shouldFailWhenCourierIsTooFarFromLocation() {
        var assignment = Assignment.mustCreate(ORDER, VOLUME, LOCATION);
        var farLocation = Location.mustCreate(1, 1);

        var result = assignment.complete(farLocation);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
        assertThat(assignment.getStatus()).isEqualTo(Status.Assigned);
    }

    @Test
    void shouldThrowWhenOrderIsNullOnCreate() {
        assertThatThrownBy(() -> Assignment.create(null, VOLUME, LOCATION))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowWhenVolumeIsNullOnCreate() {
        assertThatThrownBy(() -> Assignment.create(ORDER, null, LOCATION))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowWhenLocationIsNullOnCreate() {
        assertThatThrownBy(() -> Assignment.create(ORDER, VOLUME, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowWhenCurrentLocationIsNullOnComplete() {
        var assignment = Assignment.mustCreate(ORDER, VOLUME, LOCATION);

        assertThatThrownBy(() -> assignment.complete(null))
                .isInstanceOf(NullPointerException.class);
    }
}
