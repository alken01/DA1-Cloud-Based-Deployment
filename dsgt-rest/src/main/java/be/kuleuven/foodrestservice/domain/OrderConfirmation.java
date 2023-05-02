package be.kuleuven.foodrestservice.domain;

public class OrderConfirmation {

    private Order order;
    private double price;

    public OrderConfirmation() {
    }

    public OrderConfirmation(Order order, double price) {
        this.order = order;
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public double getPrice() {
        return price;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}