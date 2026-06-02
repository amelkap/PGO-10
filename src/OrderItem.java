public record OrderItem(Product product, int quantity) {
    public double totalPrice() {
        return product.price() * quantity;
    }
}
