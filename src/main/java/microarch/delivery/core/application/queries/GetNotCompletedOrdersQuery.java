package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetNotCompletedOrdersQuery {

    public static Result<GetNotCompletedOrdersQuery, Error> create() {
        return Result.success(new GetNotCompletedOrdersQuery());
    }
}
