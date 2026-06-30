package microarch.delivery.adapters.out.grpc;

import java.util.concurrent.ThreadLocalRandom;
import microarch.delivery.core.domain.model.kernel.Address;
import microarch.delivery.core.domain.model.kernel.Location;
import microarch.delivery.core.ports.GeoService;
import org.springframework.stereotype.Component;

@Component
public class GeoServiceImpl implements GeoService {

    @Override
    public Location getLocation(Address address) {
        int x = ThreadLocalRandom.current().nextInt(1, 11);
        int y = ThreadLocalRandom.current().nextInt(1, 11);
        return Location.mustCreate(x, y);
    }
}
