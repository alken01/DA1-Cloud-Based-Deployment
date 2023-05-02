package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Order;
import be.kuleuven.foodrestservice.domain.OrderConfirmation;
import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
public class MealsRestRpcStyleController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestRpcStyleController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/restrpc/meals/{id}")
    Meal getMealById(@PathVariable String id) {
        Optional<Meal> meal = mealsRepository.findMeal(id);

        return meal.orElseThrow(() -> new MealNotFoundException(id));
    }

    @GetMapping("/restrpc/meals")
    Collection<Meal> getMeals() {
        return mealsRepository.getAllMeal();
    }

    @PostMapping("/restrpc/meals")
    @ResponseStatus(HttpStatus.CREATED)
    Meal addMeal(@RequestBody Meal meal) {
        String newId = UUID.randomUUID().toString(); // generate new ID
        meal.setId(newId); // set the new ID to the meal object
        mealsRepository.addMeal(meal); // add the meal to the repository
        return meal;
    }

    @DeleteMapping("/restrpc/meals/{id}")
    Meal deleteMeal(@PathVariable String id) {
        Meal deleted = mealsRepository.deleteMeal(id);
        return deleted;
    }


    @PutMapping("/restrpc/meals/{id}")
    Meal updateMeal(@PathVariable String id, @RequestBody Meal meal) {
        if (!mealsRepository.findMeal(id).isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found");
        }
        mealsRepository.updateMeal(meal, id);

        // return the updated meal
        return getMealById(id);
    }


    @GetMapping("/restrpc/cheapest-meal")
    Meal getCheapestMeal() {
        return mealsRepository.getCheapestMeal();
    }

    @GetMapping("/restrpc/largest-meal")
    Meal getLargestMeal() {
        return mealsRepository.getLargestMeal();
    }

// OrderConfirmation addOrder(Order order)

    @PutMapping("/restrpc/order")
    OrderConfirmation addOrder(@RequestBody Order order) {
        String[] orderMeals = order.getMeals();
        String orderAddress = order.getAddress();

        // check if all meals exist
        for (String meal : orderMeals) {
            if (!mealsRepository.findMealByName(meal).isPresent()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found");
            }
        }

        // calculate total price
        double totalPrice = 0;
        for (String meal : orderMeals) {
            totalPrice += mealsRepository.findMealByName(meal).get().getPrice();
        }

        // create order confirmation
        OrderConfirmation orderConfirmation = new OrderConfirmation(order, totalPrice);
        return orderConfirmation;
    }

}
