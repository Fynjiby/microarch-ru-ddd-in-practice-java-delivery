package microarch.delivery.core.domain.services;

import java.util.Comparator;
import java.util.List;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderDistributionDomainServiceImpl implements OrderDistributionDomainService {

    @Override
    public Result<Courier, Error> distributeOrder(Order order, List<Courier> couriers) {
        if (order == null) {
            return Result.failure(GeneralErrors.valueIsRequired("order"));
        }
        if (couriers == null) {
            return Result.failure(GeneralErrors.valueIsRequired("couriers"));
        }

        var winner = couriers.stream()
                .filter(c -> c.canTakeOrder(order.getVolume()))
                .min(Comparator.comparingInt(c -> c.getLocation().distanceTo(order.getLocation())));

        if (winner.isEmpty()) {
            return Result.failure(Errors.allCouriersBusy());
        }

        var courier = winner.get();

        var takeResult = courier.takeOrder(order);
        if (takeResult.isFailure()) {
            return Result.failure(takeResult.getError());
        }

        var assignResult = order.assign();
        if (assignResult.isFailure()) {
            return Result.failure(assignResult.getError());
        }

        return Result.success(courier);
    }

    public static final class Errors {

        public static Error allCouriersBusy() {
            return Error.of(
                    "all.couriers.busy",
                    "No available couriers to take the order: all couriers are at full capacity"
            );
        }
    }
}
