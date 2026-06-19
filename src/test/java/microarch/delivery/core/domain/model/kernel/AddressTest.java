package microarch.delivery.core.domain.model.kernel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void shouldCreateWithAllFieldsIncludingApartment() {
        var result = Address.create("Russia", "Moscow", "Lenina", "1", "10");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getCountry()).isEqualTo("Russia");
        assertThat(result.getValue().getCity()).isEqualTo("Moscow");
        assertThat(result.getValue().getStreet()).isEqualTo("Lenina");
        assertThat(result.getValue().getHouse()).isEqualTo("1");
        assertThat(result.getValue().getApartment()).isEqualTo("10");
    }

    @Test
    void shouldCreateWithNullApartment() {
        var result = Address.create("Russia", "Moscow", "Lenina", "1", null);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getApartment()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "  " })
    void shouldReturnErrorWhenCountryIsBlankOrNull(String country) {
        var result = Address.create(country, "Moscow", "Lenina", "1", null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "  " })
    void shouldReturnErrorWhenCityIsBlankOrNull(String city) {
        var result = Address.create("Russia", city, "Lenina", "1", null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "  " })
    void shouldReturnErrorWhenStreetIsBlankOrNull(String street) {
        var result = Address.create("Russia", "Moscow", street, "1", null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "  " })
    void shouldReturnErrorWhenHouseIsBlankOrNull(String house) {
        var result = Address.create("Russia", "Moscow", "Lenina", house, null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        var first = Address.mustCreate("Russia", "Moscow", "Lenina", "1", "10");
        var second = Address.mustCreate("Russia", "Moscow", "Lenina", "1", "10");

        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldBeEqualWhenBothApartmentsAreNull() {
        var first = Address.mustCreate("Russia", "Moscow", "Lenina", "1", null);
        var second = Address.mustCreate("Russia", "Moscow", "Lenina", "1", null);

        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldNotBeEqualWhenCityDiffers() {
        var first = Address.mustCreate("Russia", "Moscow", "Lenina", "1", null);
        var second = Address.mustCreate("Russia", "Kazan", "Lenina", "1", null);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldNotBeEqualWhenApartmentDiffers() {
        var first = Address.mustCreate("Russia", "Moscow", "Lenina", "1", "10");
        var second = Address.mustCreate("Russia", "Moscow", "Lenina", "1", null);

        assertThat(first).isNotEqualTo(second);
    }
}
