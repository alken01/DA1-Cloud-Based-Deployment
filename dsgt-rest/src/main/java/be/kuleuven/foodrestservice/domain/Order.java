package be.kuleuven.foodrestservice.domain;

public class Order {
    private String address;
    private String[] meals;

    public Order() {
    }

    public Order(String address, String[] meals) {
        this.address = address;
        this.meals = meals;
    }

    public Order(String[] strings, String s) {
    }

    public String getAddress() {
        return address;
    }

    public String[] getMeals() {
        return meals;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMeals(String[] meals) {
        this.meals = meals;
    }
}
