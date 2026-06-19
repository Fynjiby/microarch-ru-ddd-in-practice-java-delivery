package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.core.application.commands.MoveCourierCommand;
import microarch.delivery.core.application.commands.MoveCourierCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MoveCourierControllerTest {

    private MoveCourierCommandHandler moveCourierCommandHandler;
    private MoveCourierController controller;

    @BeforeEach
    void setUp() {
        moveCourierCommandHandler = mock(MoveCourierCommandHandler.class);
        controller = new MoveCourierController(moveCourierCommandHandler);
    }

    @Test
    void test_moveCourier_InvalidCourierId_ShouldReturnBadRequest() {
        // arrange
        UUID courierId = null;
        var location = new Location().x(3).y(4);

        // act
        var response = controller.moveCourier(courierId, location);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verifyNoInteractions(moveCourierCommandHandler);
    }

    @Test
    void test_moveCourier_CommandHandlerFailure_ShouldReturnConflict() {
        // arrange
        var courierId = UUID.randomUUID();
        var location = new Location().x(3).y(4);
        when(moveCourierCommandHandler.handle(any(MoveCourierCommand.class)))
                .thenReturn(UnitResult.failure(Error.of("fake-error", "fail")));

        // act
        var response = controller.moveCourier(courierId, location);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        verify(moveCourierCommandHandler, times(1)).handle(any(MoveCourierCommand.class));
    }

    @Test
    void test_moveCourier_Success_ShouldReturnOk() {
        // arrange
        var courierId = UUID.randomUUID();
        var location = new Location().x(3).y(4);
        when(moveCourierCommandHandler.handle(any(MoveCourierCommand.class))).thenReturn(UnitResult.success());

        // act
        var response = controller.moveCourier(courierId, location);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(moveCourierCommandHandler, times(1)).handle(any(MoveCourierCommand.class));
    }
}
