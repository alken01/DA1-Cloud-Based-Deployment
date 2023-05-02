package be.kuleuven.foodrestservice.domain;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class MealsRepository {
    // map: id -> meal
    private static final Map<String, Meal> meals = new HashMap<>();

    @PostConstruct
    public void initData() {

        Meal a = new Meal();
        a.setId("5268203c-de76-4921-a3e3-439db69c462a");
        a.setName("Steak");
        a.setDescription("Steak with fries");
        a.setMealType(MealType.MEAT);
        a.setKcal(1100);
        a.setPrice((10.00));

        meals.put(a.getId(), a);

        Meal b = new Meal();
        b.setId("4237681a-441f-47fc-a747-8e0169bacea1");
        b.setName("Portobello");
        b.setDescription("Portobello Mushroom Burger");
        b.setMealType(MealType.VEGAN);
        b.setKcal(637);
        b.setPrice((7.00));

        meals.put(b.getId(), b);

        Meal c = new Meal();
        c.setId("cfd1601f-29a0-485d-8d21-7607ec0340c8");
        c.setName("Fish and Chips");
        c.setDescription("Fried fish with chips");
        c.setMealType(MealType.FISH);
        c.setKcal(950);
        c.setPrice(5.00);

        meals.put(c.getId(), c);
    }

    public Optional<Meal> findMeal(String id) {
        Assert.notNull(id, "The meal id must not be null");
        Meal meal = meals.get(id);
        return Optional.ofNullable(meal);
    }

    // find meal by name
    public Optional<Meal> findMealByName(String name) {
        Assert.notNull(name, "The meal name must not be null");
        Meal meal = meals.values().stream().filter(m -> m.getName().equals(name)).findFirst().get();
        return Optional.ofNullable(meal);
    }

    public Collection<Meal> getAllMeal() {
        return meals.values();
    }

    public Meal getCheapestMeal() {
        // return the cheapest meal in meals
        return meals.values().stream().min(Comparator.comparing(Meal::getPrice)).get();
    }

    public Meal getLargestMeal() {
        // return the largest meal in meals
        return meals.values().stream().max(Comparator.comparing(Meal::getKcal)).get();
    }

    public void addMeal(Meal meal) {
        // generate a new UUID and set it as the ID of the meal
        String newId = UUID.randomUUID().toString();
        meal.setId(newId);
        meals.put(newId, meal);
    }

    public Meal deleteMeal(String id) {
        Meal deleted = meals.remove(id);
        return deleted;
    }

    public void updateMeal(Meal meal, String newId) {
        deleteMeal(meal.getId());

        meal.setId(newId);
        addMeal(meal);
    }
}
