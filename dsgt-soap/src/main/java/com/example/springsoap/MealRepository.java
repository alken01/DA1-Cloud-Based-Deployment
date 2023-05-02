package com.example.springsoap;

import javax.annotation.PostConstruct;
import java.util.*;

import io.foodmenu.gt.webservice.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MealRepository {
    private static final Map<String, Meal> meals = new HashMap<String, Meal>();

    @PostConstruct
    public void initData() {

        Meal a = new Meal();
        a.setName("Steak");
        a.setDescription("Steak with fries");
        a.setMealtype(Mealtype.MEAT);
        a.setKcal(1100);
        a.setPrice(6);


        meals.put(a.getName(), a);

        Meal b = new Meal();
        b.setName("Portobello");
        b.setDescription("Portobello Mushroom Burger");
        b.setMealtype(Mealtype.VEGAN);
        b.setKcal(637);
        b.setPrice(7);


        meals.put(b.getName(), b);

        Meal c = new Meal();
        c.setName("Fish and Chips");
        c.setDescription("Fried fish with chips");
        c.setMealtype(Mealtype.FISH);
        c.setKcal(950);
        c.setPrice(1);



        meals.put(c.getName(), c);

    }

    public Meal findMeal(String name) {
        Assert.notNull(name, "The meal's code must not be null");
        return meals.get(name);
    }

    public Meal findMealByName(String name) {
        Assert.notNull(name, "The meal name must not be null");
        Meal meal = meals.values().stream().filter(m -> m.getName().equals(name)).findFirst().get();
        return meal;
    }


    public Meal findBiggestMeal() {

        if (meals == null) return null;
        if (meals.size() == 0) return null;

        var values = meals.values();
        return values.stream().max(Comparator.comparing(Meal::getKcal)).orElseThrow(NoSuchElementException::new);

    }

    public Meal findCheapestMeal() {
        if (meals == null) return null;
        if (meals.size() == 0) return null;

        var values = meals.values();
        return values.stream().min(Comparator.comparing(Meal::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    
    public OrderConfirmation addOrder(Order order) {
        List<String> orderMeals = order.getMeals();

        // check if all meals exist
        for (String meal : orderMeals) {
            if (findMealByName(meal) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found");
            }
        }

        // calculate total price
        double totalPrice = 0;
        for (String meal : orderMeals) {
            totalPrice += findMealByName(meal).getPrice();
        }

        // create order confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation();
        orderConfirmation.setPrice(totalPrice);
        orderConfirmation.setOrder(order);

        return orderConfirmation;
    }


}