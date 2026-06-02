import java.time.LocalDate;
import java.util.List;

public record Order(
    String id,
    String customerName,
    LocalDate orderDate,
    List<OrderItem> items,
    OrderStatus status
) {
    public double totalValue() {
        return items.stream()
            .mapToDouble(OrderItem::totalPrice)
            .sum();
    }
}
