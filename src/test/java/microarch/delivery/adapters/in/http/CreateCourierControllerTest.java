package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.adapters.in.http.model.NewCourier;
import microarch.delivery.core.application.commands.CreateCourierCommand;
import microarch.delivery.core.application.commands.CreateCourierCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCourierControllerTest {

    private CreateCourierCommandHandler createCourierCommandHandler;
    private CreateCourierController controller;

    @BeforeEach
    void setUp() {
        createCourierCommandHandler = mock(CreateCourierCommandHandler.class);
        controller = new CreateCourierController(createCourierCommandHandler);
    }

    @Test
    void test_createCourier_InvalidName_ShouldReturnBadRequest() {
        // arrange
        var request = new NewCourier().name(null);

        // act
        var response = controller.createCourier(request);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verifyNoInteractions(createCourierCommandHandler);
    }

    @Test
    void test_createCourier_CommandHandlerFailure_ShouldReturnConflict() {
        // arrange
        var request = new NewCourier().name("Ivan");
        when(createCourierCommandHandler.handle(any(CreateCourierCommand.class)))
                .thenReturn(Result.failure(Error.of("fake-error", "fail")));

        // act
        var response = controller.createCourier(request);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        verify(createCourierCommandHandler, times(1)).handle(any(CreateCourierCommand.class));
    }

    @Test
    void test_createCourier_Success_ShouldReturnCreatedWithId() {
        // arrange
        var courierId = UUID.randomUUID();
        var request = new NewCourier().name("Ivan");
        when(createCourierCommandHandler.handle(any(CreateCourierCommand.class))).thenReturn(Result.success(courierId));

        // act
        var response = controller.createCourier(request);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCourierId()).isEqualTo(courierId);
        verify(createCourierCommandHandler, times(1)).handle(any(CreateCourierCommand.class));
    }
}
