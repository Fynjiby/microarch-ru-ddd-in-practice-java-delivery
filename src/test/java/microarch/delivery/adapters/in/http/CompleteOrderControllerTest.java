package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.application.commands.CompleteOrderCommand;
import microarch.delivery.core.application.commands.CompleteOrderCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompleteOrderControllerTest {

    private CompleteOrderCommandHandler completeOrderCommandHandler;
    private CompleteOrderController controller;

    @BeforeEach
    void setUp() {
        completeOrderCommandHandler = mock(CompleteOrderCommandHandler.class);
        controller = new CompleteOrderController(completeOrderCommandHandler);
    }

    @Test
    void test_completeOrder_InvalidIds_ShouldReturnBadRequest() {
        // arrange
        UUID courierId = null;
        UUID orderId = null;

        // act
        var response = controller.completeOrder(courierId, orderId);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verifyNoInteractions(completeOrderCommandHandler);
    }

    @Test
    void test_completeOrder_CommandHandlerFailure_ShouldReturnConflict() {
        // arrange
        var courierId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        when(completeOrderCommandHandler.handle(any(CompleteOrderCommand.class)))
                .thenReturn(UnitResult.failure(Error.of("fake-error", "fail")));

        // act
        var response = controller.completeOrder(courierId, orderId);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        verify(completeOrderCommandHandler, times(1)).handle(any(CompleteOrderCommand.class));
    }

    @Test
    void test_completeOrder_Success_ShouldReturnOk() {
        // arrange
        var courierId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        when(completeOrderCommandHandler.handle(any(CompleteOrderCommand.class))).thenReturn(UnitResult.success());

        // act
        var response = controller.completeOrder(courierId, orderId);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(completeOrderCommandHandler, times(1)).handle(any(CompleteOrderCommand.class));
    }
}
