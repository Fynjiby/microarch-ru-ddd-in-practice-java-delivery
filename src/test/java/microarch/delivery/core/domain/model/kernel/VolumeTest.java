package microarch.delivery.core.domain.model.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class VolumeTest {

    @ParameterizedTest
    @ValueSource(ints = { 1, 500, 999 })
    void shouldBeCorrectWhenValueIsInRangeOnCreated(int value) {
        var result = Volume.create(value);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, 1000, Integer.MAX_VALUE })
    void shouldReturnErrorWhenValueIsOutOfRangeOnCreated(int value) {
        var result = Volume.create(value);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        var first = Volume.mustCreate(10);
        var second = Volume.mustCreate(10);

        assertThat(first.equals(second)).isTrue();
    }

    @Test
    void shouldBeNotEqualWhenValuesAreNotEqual() {
        var first = Volume.mustCreate(10);
        var second = Volume.mustCreate(20);

        assertThat(first.equals(second)).isFalse();
    }
}
