package microarch.delivery.core.domain.model.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @ParameterizedTest
    @ValueSource(ints = { 1, 10 })
    void shouldBeCorrectWhenParamsAreCorrectOnCreated(int coordinate) {
        var result = Location.create(coordinate, coordinate);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(coordinate);
        assertThat(result.getValue().getY()).isEqualTo(coordinate);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0, 11 })
    void shouldReturnErrorWhenParamsAreNotCorrectOnCreated(int coordinate) {
        var result = Location.create(coordinate, coordinate);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    public void shouldBeEqualWhenAllPropertiesIsEqual() {
        var first = Location.mustCreate(1, 1);
        var second = Location.mustCreate(1, 1);
        var result = first.equals(second);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldBeNotEqualWhenOneOfPropertiesIsNotEqual() {
        var first = Location.mustCreate(1, 2);
        var second = Location.mustCreate(1, 1);
        var result = first.equals(second);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeSymmetricDistanceTo() {
        var a = Location.mustCreate(2, 3);
        var b = Location.mustCreate(7, 8);

        assertThat(a.distanceTo(b)).isEqualTo(b.distanceTo(a));
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 1, 1, 0", "4, 9, 2, 6, 5", "5, 5, 1, 1, 8", "1, 5, 5, 1, 8", "1, 1, 10, 10, 18" })
    void shouldCalculateDistanceCorrectly(int x1, int y1, int x2, int y2, int expectedDistance) {
        var from = Location.mustCreate(x1, y1);
        var to = Location.mustCreate(x2, y2);

        assertThat(from.distanceTo(to)).isEqualTo(expectedDistance);
    }
}