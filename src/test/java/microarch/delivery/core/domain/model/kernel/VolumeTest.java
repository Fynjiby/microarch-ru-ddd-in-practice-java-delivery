package microarch.delivery.core.domain.model.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class VolumeTest {

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 500, 999 })
    void shouldBeCorrectWhenValueIsInRangeOnCreated(int value) {
        var result = Volume.create(value);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 1000, Integer.MAX_VALUE })
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

    @Test
    void shouldAddCorrectly() {
        var a = Volume.mustCreate(10);
        var b = Volume.mustCreate(5);

        assertThat(a.add(b)).isEqualTo(Volume.mustCreate(15));
    }

    @Test
    void shouldSubtractCorrectly() {
        var a = Volume.mustCreate(10);
        var b = Volume.mustCreate(5);

        assertThat(a.subtract(b)).isEqualTo(Volume.mustCreate(5));
    }

    @Test
    void shouldReturnTrueWhenIsLessThan() {
        var small = Volume.mustCreate(5);
        var large = Volume.mustCreate(10);

        assertThat(small.isLessThan(large)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIsLessThanAndEqual() {
        var a = Volume.mustCreate(10);
        var b = Volume.mustCreate(10);

        assertThat(a.isLessThan(b)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenIsLessOrEqualToAndEqual() {
        var a = Volume.mustCreate(10);
        var b = Volume.mustCreate(10);

        assertThat(a.isLessOrEqualTo(b)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenIsLessOrEqualToAndLess() {
        var small = Volume.mustCreate(5);
        var large = Volume.mustCreate(10);

        assertThat(small.isLessOrEqualTo(large)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIsLessOrEqualToAndGreater() {
        var large = Volume.mustCreate(10);
        var small = Volume.mustCreate(5);

        assertThat(large.isLessOrEqualTo(small)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenIsGreaterThan() {
        var large = Volume.mustCreate(10);
        var small = Volume.mustCreate(5);

        assertThat(large.isGreaterThan(small)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIsGreaterThanAndEqual() {
        var a = Volume.mustCreate(10);
        var b = Volume.mustCreate(10);

        assertThat(a.isGreaterThan(b)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenIsGreaterOrEqualToAndEqual() {
        var a = Volume.mustCreate(10);
        var b = Volume.mustCreate(10);

        assertThat(a.isGreaterOrEqualTo(b)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenIsGreaterOrEqualToAndGreater() {
        var large = Volume.mustCreate(10);
        var small = Volume.mustCreate(5);

        assertThat(large.isGreaterOrEqualTo(small)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIsGreaterOrEqualToAndLess() {
        var small = Volume.mustCreate(5);
        var large = Volume.mustCreate(10);

        assertThat(small.isGreaterOrEqualTo(large)).isFalse();
    }
}
