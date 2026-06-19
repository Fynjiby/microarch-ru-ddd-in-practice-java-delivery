package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.adapters.in.http.model.Address;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderControllerTest {

    private CreateOrderCommandHandler createOrderCommandHandler;
    private CreateOrderController controller;

    @BeforeEach
    void setUp() {
        createOrderCommandHandler = mock(CreateOrderCommandHandler.class);
        controller = new CreateOrderController(createOrderCommandHandler);
    }

    @Test
    void test_createOrder_InvalidRequest_ShouldReturnBadRequest() {
        // arrange
        var request = new NewOrder().id(UUID.randomUUID()).address(null);

        // act
        var response = controller.createOrder(request);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        verifyNoInteractions(createOrderCommandHandler);
    }

    @Test
    void test_createOrder_CommandHandlerFailure_ShouldReturnConflict() {
        // arrange
        var request = validNewOrder();
        when(createOrderCommandHandler.handle(any(CreateOrderCommand.class)))
                .thenReturn(Result.failure(Error.of("fake-error", "fail")));

        // act
        var response = controller.createOrder(request);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        verify(createOrderCommandHandler, times(1)).handle(any(CreateOrderCommand.class));
    }

    @Test
    void test_createOrder_Success_ShouldReturnCreatedWithId() {
        // arrange
        var orderId = UUID.randomUUID();
        var request = validNewOrder();
        when(createOrderCommandHandler.handle(any(CreateOrderCommand.class))).thenReturn(Result.success(orderId));

        // act
        var response = controller.createOrder(request);

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOrderId()).isEqualTo(orderId);
        verify(createOrderCommandHandler, times(1)).handle(any(CreateOrderCommand.class));
    }

    private NewOrder validNewOrder() {
        var address = new Address().country("Russia").city("Moscow").street("Lenina").house("1").apartment("1");
        return new NewOrder().id(UUID.randomUUID()).address(address).volume(5);
    }
}
