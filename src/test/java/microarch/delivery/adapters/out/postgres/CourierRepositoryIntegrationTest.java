package microarch.delivery.adapters.out.postgres;

import microarch.delivery.PostgresIntegrationTestBase;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CourierRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    CourierRepository repository;

    @Test
    void canAddAndFindById() {
        // Arrange
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(5, 5));

        // Act
        repository.add(courier);
        var loaded = repository.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(courier.getId());
        assertThat(loaded.get().getName()).isEqualTo(courier.getName());
        assertThat(loaded.get().getLocation()).isEqualTo(courier.getLocation());
    }

    @Test
    void canUpdateCourier() {
        // Arrange
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(5, 5));
        repository.add(courier);
        Location location = Location.mustCreate(7, 8);
        courier.move(location);

        // Act
        repository.update(courier);
        var loaded = repository.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getLocation()).isEqualTo(location);
    }

    @Test
    void canFindAll() {
        // Arrange
        repository.add(Courier.mustCreate("Ivan", Location.mustCreate(1, 1)));
        repository.add(Courier.mustCreate("Petr", Location.mustCreate(2, 2)));
        repository.add(Courier.mustCreate("Anna", Location.mustCreate(3, 3)));

        // Act
        var all = repository.findAll();

        // Assert
        assertThat(all).hasSize(3);
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        // Act
        var loaded = repository.findById(java.util.UUID.randomUUID());

        // Assert
        assertThat(loaded).isEmpty();
    }
}
