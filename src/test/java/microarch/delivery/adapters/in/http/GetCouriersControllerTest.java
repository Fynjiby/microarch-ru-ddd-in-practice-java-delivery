package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.application.queries.GetAllCouriersQuery;
import microarch.delivery.core.application.queries.GetAllCouriersQueryHandler;
import microarch.delivery.core.application.queries.dto.CourierDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetCouriersControllerTest {

    private GetAllCouriersQueryHandler getAllCouriersQueryHandler;
    private GetCouriersController controller;

    @BeforeEach
    void setUp() {
        getAllCouriersQueryHandler = mock(GetAllCouriersQueryHandler.class);
        controller = new GetCouriersController(getAllCouriersQueryHandler);
    }

    @Test
    void test_getCouriers_QueryHandlerFailure_ShouldReturnInternalServerError() {
        // arrange
        when(getAllCouriersQueryHandler.handle(any(GetAllCouriersQuery.class)))
                .thenReturn(Result.failure(Error.of("fake-error", "fail")));

        // act
        var response = controller.getCouriers();

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        verify(getAllCouriersQueryHandler, times(1)).handle(any(GetAllCouriersQuery.class));
    }

    @Test
    void test_getCouriers_Success_ShouldReturnOkWithList() {
        // arrange
        var dto = new CourierDto(UUID.randomUUID(), "Ivan", 3, 4);
        when(getAllCouriersQueryHandler.handle(any(GetAllCouriersQuery.class)))
                .thenReturn(Result.success(List.of(dto)));

        // act
        var response = controller.getCouriers();

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(dto.id());
        assertThat(response.getBody().get(0).getName()).isEqualTo(dto.name());
        assertThat(response.getBody().get(0).getLocation().getX()).isEqualTo(dto.locationX());
        assertThat(response.getBody().get(0).getLocation().getY()).isEqualTo(dto.locationY());
        verify(getAllCouriersQueryHandler, times(1)).handle(any(GetAllCouriersQuery.class));
    }

    @Test
    void test_getCouriers_EmptyList_ShouldReturnOkWithEmptyList() {
        // arrange
        when(getAllCouriersQueryHandler.handle(any(GetAllCouriersQuery.class)))
                .thenReturn(Result.success(List.of()));

        // act
        var response = controller.getCouriers();

        // assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }
}
