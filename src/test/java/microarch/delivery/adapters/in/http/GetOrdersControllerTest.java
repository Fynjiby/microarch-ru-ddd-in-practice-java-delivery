package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersQuery;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;
import microarch.delivery.core.application.queries.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetOrdersControllerTest {

    private GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;
    private GetOrdersController controller;

    @BeforeEach
    void setUp() {
        getNotCompletedOrdersQueryHandler = mock(GetNotCompletedOrdersQueryHandler.class);
        controller = new GetOrdersController(getNotCompletedOrdersQueryHandler);
    }

    @Test
    void test_getOrders_QueryHandlerFailure_ShouldReturnInternalServerError() {
        // arrange
        when(getNotCompletedOrdersQueryHandler.handle(any(GetNotCompletedOrdersQuery.class)))
                .thenReturn(Result.failure(Error.of("fake-error", "fail")));

        // act
        var response = controller.getOrders();

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        verify(getNotCompletedOrdersQueryHandler, times(1)).handle(any(GetNotCompletedOrdersQuery.class));
    }

    @Test
    void test_getOrders_Success_ShouldReturnOkWithList() {
        // arrange
        var dto = new OrderDto(UUID.randomUUID(), 5, 7);
        when(getNotCompletedOrdersQueryHandler.handle(any(GetNotCompletedOrdersQuery.class)))
                .thenReturn(Result.success(List.of(dto)));

        // act
        var response = controller.getOrders();

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(dto.id());
        assertThat(response.getBody().get(0).getLocation().getX()).isEqualTo(dto.locationX());
        assertThat(response.getBody().get(0).getLocation().getY()).isEqualTo(dto.locationY());
        verify(getNotCompletedOrdersQueryHandler, times(1)).handle(any(GetNotCompletedOrdersQuery.class));
    }

    @Test
    void test_getOrders_EmptyList_ShouldReturnOkWithEmptyList() {
        // arrange
        when(getNotCompletedOrdersQueryHandler.handle(any(GetNotCompletedOrdersQuery.class)))
                .thenReturn(Result.success(List.of()));

        // act
        var response = controller.getOrders();

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }
}
