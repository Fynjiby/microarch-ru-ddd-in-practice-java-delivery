package microarch.delivery.adapters.in.http.mappers;

import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.adapters.in.http.model.CreateCourierResponse;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.queries.dto.CourierDto;
import microarch.delivery.core.application.queries.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface DeliveryMapper {

    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    @Mapping(target = "location", expression = "java(new microarch.delivery.adapters.in.http.model.Location().x(dto.locationX()).y(dto.locationY()))")
    Courier toHttp(CourierDto dto);

    @Mapping(target = "location", expression = "java(new microarch.delivery.adapters.in.http.model.Location().x(dto.locationX()).y(dto.locationY()))")
    Order toHttp(OrderDto dto);

    default CreateCourierResponse toCreateCourierResponse(UUID courierId) {
        return new CreateCourierResponse().courierId(courierId);
    }

    default CreateOrderResponse toCreateOrderResponse(UUID orderId) {
        return new CreateOrderResponse().orderId(orderId);
    }
}
