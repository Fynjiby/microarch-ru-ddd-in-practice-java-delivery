package microarch.delivery.core.domain.model.order;

public enum OrderStatus {
    Created, Assigned, Completed;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
        case Created -> next == Assigned;
        case Assigned -> next == Completed;
        case Completed -> false;
        };
    }
}
