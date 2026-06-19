package microarch.delivery.adapters.out.postgres;

import microarch.delivery.PostgresIntegrationTestBase;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.domain.model.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    OrderRepository repository;

    @Test
    void canAddAndFindById() {
        // Arrange
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 4), Volume.mustCreate(5));

        // Act
        repository.add(order);
        var loaded = repository.findById(order.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(order.getId());
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.Created);
        assertThat(loaded.get().getLocation()).isEqualTo(order.getLocation());
        assertThat(loaded.get().getVolume()).isEqualTo(order.getVolume());
    }

    @Test
    void canUpdateOrder() {
        // Arrange
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 4), Volume.mustCreate(5));
        repository.add(order);
        order.assign();

        // Act
        repository.update(order);
        var loaded = repository.findById(order.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.Assigned);
    }

    @Test
    void canFindAnyCreated() {
        // Arrange
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 2), Volume.mustCreate(3));
        repository.add(order);

        // Act
        var loaded = repository.findAnyCreated();

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.Created);
    }

    @Test
    void findAnyCreatedReturnsEmptyWhenNoneExist() {
        // Act
        var loaded = repository.findAnyCreated();

        // Assert
        assertThat(loaded).isEmpty();
    }

    @Test
    void canFindAllAssigned() {
        // Arrange
        var order1 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 1), Volume.mustCreate(2));
        var order2 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(3));
        var order3 = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(4));
        order1.assign();
        order2.assign();
        repository.add(order1);
        repository.add(order2);
        repository.add(order3);

        // Act
        var assigned = repository.findAllAssigned();

        // Assert
        assertThat(assigned).hasSize(2);
        assertThat(assigned).allMatch(o -> o.getStatus() == OrderStatus.Assigned);
    }
}
