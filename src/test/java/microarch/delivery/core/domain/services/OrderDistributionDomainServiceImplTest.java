package microarch.delivery.core.domain.services;

import java.util.List;
import java.util.UUID;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDistributionDomainServiceImplTest {

    private OrderDistributionDomainService service;

    @BeforeEach
    void setUp() {
        service = new OrderDistributionDomainServiceImpl();
    }

    @Test
    void shouldAssignOrderToNearestCourier() {
        var orderLocation = Location.mustCreate(5, 5);
        var order = Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(3));

        var nearCourier = Courier.mustCreate("Near", Location.mustCreate(4, 5), Volume.mustCreate(10));
        var farCourier = Courier.mustCreate("Far", Location.mustCreate(1, 1), Volume.mustCreate(10));

        var result = service.distributeOrder(order, List.of(farCourier, nearCourier));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(nearCourier);
        assertThat(nearCourier.getAssignments()).hasSize(1);
        assertThat(farCourier.getAssignments()).isEmpty();
    }

    @Test
    void shouldTransitionOrderToAssignedStatus() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(2));
        var courier = Courier.mustCreate("Alice", Location.mustCreate(3, 3), Volume.mustCreate(10));

        service.distributeOrder(order, List.of(courier));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.Assigned);
    }

    @Test
    void shouldIncreaseCourierCurrentVolumeAfterAssignment() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(4));
        var courier = Courier.mustCreate("Bob", Location.mustCreate(5, 5), Volume.mustCreate(10));

        service.distributeOrder(order, List.of(courier));

        assertThat(courier.getCurrentVolume()).isEqualTo(Volume.mustCreate(4));
    }

    @Test
    void shouldReturnFailureWhenOrderIsNull() {
        var courier = Courier.mustCreate("Alice", Location.mustCreate(5, 5), Volume.mustCreate(10));

        var result = service.distributeOrder(null, List.of(courier));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnFailureWhenCouriersListIsNull() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(3));

        var result = service.distributeOrder(order, null);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnFailureWhenNoCouriersAvailable() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(3));

        var result = service.distributeOrder(order, List.of());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnFailureWhenAllCouriersAreFull() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(5));

        var fullCourier1 = Courier.mustCreate("C1", Location.mustCreate(5, 5), Volume.mustCreate(3));
        fullCourier1.takeOrder(Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(3)));

        var fullCourier2 = Courier.mustCreate("C2", Location.mustCreate(5, 5), Volume.mustCreate(4));
        fullCourier2.takeOrder(Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(4)));

        var result = service.distributeOrder(order, List.of(fullCourier1, fullCourier2));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldSkipOverloadedCourierAndAssignToEligibleOne() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(5, 5), Volume.mustCreate(5));

        // closest but full
        var fullCourier = Courier.mustCreate("Full", Location.mustCreate(5, 5), Volume.mustCreate(3));
        fullCourier.takeOrder(Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(3)));

        // farther but has capacity
        var freeCourier = Courier.mustCreate("Free", Location.mustCreate(1, 1), Volume.mustCreate(10));

        var result = service.distributeOrder(order, List.of(fullCourier, freeCourier));

        assertThat(result.isSuccess()).isTrue();
        assertThat(freeCourier.getAssignments()).hasSize(1);
        assertThat(fullCourier.getAssignments()).hasSize(1); // unchanged
    }

    @Test
    void shouldAssignOrderToSingleEligibleCourier() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(7, 8), Volume.mustCreate(1));
        var courier = Courier.mustCreate("Solo", Location.mustCreate(2, 3), Volume.mustCreate(5));

        var result = service.distributeOrder(order, List.of(courier));

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    void shouldSelectCourierWithMinimumManhattanDistance() {
        var orderLocation = Location.mustCreate(5, 5);
        var order = Order.mustCreate(UUID.randomUUID(), orderLocation, Volume.mustCreate(1));

        // distance from (3,5) = 2
        var c1 = Courier.mustCreate("C1", Location.mustCreate(3, 5), Volume.mustCreate(10));
        // distance from (5,8) = 3
        var c2 = Courier.mustCreate("C2", Location.mustCreate(5, 8), Volume.mustCreate(10));
        // distance from (6,5) = 1
        var c3 = Courier.mustCreate("C3", Location.mustCreate(6, 5), Volume.mustCreate(10));

        var result = service.distributeOrder(order, List.of(c1, c2, c3));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(c3);
        assertThat(c3.getAssignments()).hasSize(1);
        assertThat(c1.getAssignments()).isEmpty();
        assertThat(c2.getAssignments()).isEmpty();
    }

    @Test
    void shouldAllowCourierToTakeMultipleOrdersWithinCapacity() {
        var courier = Courier.mustCreate("Multi", Location.mustCreate(5, 5), Volume.mustCreate(10));

        var order1 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(4));
        var order2 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(7, 7), Volume.mustCreate(4));

        service.distributeOrder(order1, List.of(courier));
        service.distributeOrder(order2, List.of(courier));

        assertThat(courier.getAssignments()).hasSize(2);
        assertThat(courier.getCurrentVolume()).isEqualTo(Volume.mustCreate(8));
    }
}
