import java.time.LocalDate;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class PGO10 {

    static List<Order> sampleOrders() {
        Product laptop = new Product("Laptop", "Electronics", 4200.0);
        Product phone = new Product("Phone", "Electronics", 2600.0);
        Product headphones = new Product("Headphones", "Electronics", 350.0);
        Product novel = new Product("Novel", "Books", 45.0);
        Product textbook = new Product("Textbook", "Books", 120.0);
        Product tshirt = new Product("T-Shirt", "Fashion", 80.0);
        Product jacket = new Product("Jacket", "Fashion", 300.0);
        Product coffee = new Product("Coffee", "Grocery", 35.0);
        Product tea = new Product("Tea", "Grocery", 25.0);

        return List.of(
            new Order("ORD-001", "Anna Kowalska", LocalDate.of(2024, 2, 3),
                List.of(new OrderItem(laptop, 1), new OrderItem(headphones, 2)),
                OrderStatus.DELIVERED),
            new Order("ORD-002", "Jan Nowak", LocalDate.of(2024, 2, 5),
                List.of(new OrderItem(novel, 3), new OrderItem(textbook, 1)),
                OrderStatus.DELIVERED),
            new Order("ORD-003", "Anna Kowalska", LocalDate.of(2024, 2, 10),
                List.of(new OrderItem(phone, 1), new OrderItem(tshirt, 2)),
                OrderStatus.SHIPPED),
            new Order("ORD-004", "Maria Wisniewska", LocalDate.of(2024, 3, 1),
                List.of(new OrderItem(jacket, 1), new OrderItem(coffee, 3), new OrderItem(tea, 5)),
                OrderStatus.PAID),
            new Order("ORD-005", "Jan Nowak", LocalDate.of(2024, 3, 12),
                List.of(new OrderItem(laptop, 2)),
                OrderStatus.NEW),
            new Order("ORD-006", "Piotr Zielinski", LocalDate.of(2024, 3, 20),
                List.of(new OrderItem(phone, 1), new OrderItem(headphones, 1)),
                OrderStatus.CANCELLED),
            new Order("ORD-007", "Maria Wisniewska", LocalDate.of(2024, 4, 2),
                List.of(new OrderItem(novel, 1), new OrderItem(coffee, 2), new OrderItem(tshirt, 1)),
                OrderStatus.DELIVERED),
            new Order("ORD-008", "Tomasz Lewandowski", LocalDate.of(2024, 4, 8),
                List.of(new OrderItem(laptop, 1), new OrderItem(textbook, 2)),
                OrderStatus.SHIPPED)
        );
    }

    static List<String> activeOrderIds(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .map(o -> o.id())
            .toList();
    }

    static List<Order> ordersAbove(List<Order> orders, double minValue) {
        return orders.stream()
            .filter(o -> o.totalValue() > minValue)
            .sorted((o1, o2) -> Double.compare(o2.totalValue(), o1.totalValue()))
            .toList();
    }

    static List<String> uniqueCustomerNames(List<Order> orders) {
        return orders.stream()
            .map(o -> o.customerName())
            .distinct()
            .sorted()
            .toList();
    }

    static List<String> soldProductNames(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .flatMap(o -> o.items().stream())
            .map(pozycja -> pozycja.product().name())
            .distinct()
            .sorted()
            .toList();
    }

    static double totalRevenue(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .mapToDouble(o -> o.totalValue())
            .sum();
    }

    static OptionalDouble averageDeliveredOrderValue(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() == OrderStatus.DELIVERED)
            .mapToDouble(o -> o.totalValue())
            .average();
    }

    static Map<OrderStatus, Long> countByStatus(List<Order> orders) {
        return orders.stream()
            .collect(Collectors.groupingBy(o -> o.status(), Collectors.counting()));
    }

    static Map<String, Double> revenueByCategory(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .flatMap(o -> o.items().stream())
            .collect(Collectors.groupingBy(
                pozycja -> pozycja.product().category(),
                Collectors.summingDouble(pozycja -> pozycja.totalPrice())));
    }

    static Map<String, Double> topCustomers(List<Order> orders, int limit) {
        Map<String, Double> wydatki = orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .collect(Collectors.groupingBy(
                o -> o.customerName(),
                Collectors.summingDouble(o -> o.totalValue())));

        return wydatki.entrySet().stream()
            .sorted(Comparator.comparing((Map.Entry<String, Double> e) -> e.getValue()).reversed())
            .limit(limit)
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue(),
                (x, y) -> x,
                LinkedHashMap::new));
    }

    static Map<Boolean, List<Order>> partitionActiveOrdersByValue(List<Order> orders, double threshold) {
        return orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .collect(Collectors.partitioningBy(o -> o.totalValue() >= threshold));
    }

    static Optional<Order> mostExpensiveDeliveredOrder(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() == OrderStatus.DELIVERED)
            .max((o1, o2) -> Double.compare(o1.totalValue(), o2.totalValue()));
    }

    static DoubleSummaryStatistics activeOrderStatistics(List<Order> orders) {
        return orders.stream()
            .filter(o -> o.status() != OrderStatus.CANCELLED)
            .mapToDouble(o -> o.totalValue())
            .summaryStatistics();
    }

    public static void main(String[] args) {
        List<Order> orders = sampleOrders();

        System.out.println(activeOrderIds(orders));
        System.out.println(ordersAbove(orders, 3000).stream().map(Order::id).toList());
        System.out.println(uniqueCustomerNames(orders));
        System.out.println(soldProductNames(orders));
        System.out.println(totalRevenue(orders));
        System.out.println(averageDeliveredOrderValue(orders).orElse(0.0));
        System.out.println(countByStatus(orders));
        System.out.println(revenueByCategory(orders));
        System.out.println(topCustomers(orders, 3));
        System.out.println(partitionActiveOrdersByValue(orders, 3000));
        System.out.println(mostExpensiveDeliveredOrder(orders).map(Order::id).orElse("brak"));
        System.out.println(activeOrderStatistics(orders));
    }
}
