package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import be.kuleuven.foodrestservice.domain.Order;
import be.kuleuven.foodrestservice.domain.OrderConfirmation;
import be.kuleuven.foodrestservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class MealsRestController {

    private final MealsRepository mealsRepository;

    @Autowired
    MealsRestController(MealsRepository mealsRepository) {
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/rest/meals/{id}")
    EntityModel<Meal> getMealById(@PathVariable String id) {
        Meal meal = mealsRepository.findMeal(id).orElseThrow(() -> new MealNotFoundException(id));

        return mealToEntityModel(id, meal);
    }

    @GetMapping("/rest/meals")
    CollectionModel<EntityModel<Meal>> getMeals() {
        Collection<Meal> meals = mealsRepository.getAllMeal();

        List<EntityModel<Meal>> mealEntityModels = new ArrayList<>();
        for (Meal m : meals) {
            EntityModel<Meal> em = mealToEntityModel(m.getId(), m);
            mealEntityModels.add(em);
        }
        return CollectionModel.of(mealEntityModels,
                linkTo(methodOn(MealsRestController.class).getMeals()).withSelfRel());
    }

    @PostMapping("/rest/meals")
    @ResponseStatus(HttpStatus.CREATED)
    EntityModel<Meal> addMeal(@RequestBody Meal meal) {
        String newId = UUID.randomUUID().toString(); // generate new ID
        meal.setId(newId); // set the new ID to the meal object
        mealsRepository.addMeal(meal); // add the meal to the repository
        return mealToEntityModel(meal.getId(), meal);
    }

    @DeleteMapping("/rest/meals/{id}")
    EntityModel<Meal> deleteMeal(@PathVariable String id) {
        Meal deletedMeal = mealsRepository.deleteMeal(id);
        return mealToEntityModel(deletedMeal.getId(), deletedMeal);
    }


    @PutMapping("/rest/meals/{id}")
    EntityModel<Meal> updateMeal(@PathVariable String id, @RequestBody Meal meal) {
        if (!mealsRepository.findMeal(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found");
        }
        mealsRepository.updateMeal(meal, id);

        // return the updated meal
        return getMealById(id);
    }


    @GetMapping("/rest/cheapest-meal")
    EntityModel<Meal> getCheapestMeal() {
        Meal cheapestMeal = mealsRepository.getCheapestMeal();
        return mealToEntityModel(cheapestMeal.getId(), cheapestMeal);
    }

    @GetMapping("/rest/largest-meal")
    EntityModel<Meal> getLargestMeal() {
        Meal cheapestMeal = mealsRepository.getLargestMeal();
        return mealToEntityModel(cheapestMeal.getId(), cheapestMeal);
    }

    // OrderConfirmation addOrder(Order order)

    @PostMapping("/rest/order")
    OrderConfirmation addOrder(@RequestBody Order order) {
        String[] orderMeals = order.getMeals();
        String orderAddress = order.getAddress();

        // check if all meals exist
        for (String meal : orderMeals) {
            if (!mealsRepository.findMealByName(meal).isPresent()) {
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


    private EntityModel<Meal> mealToEntityModel(String id, Meal meal) {
        return EntityModel.of(meal,
                linkTo(methodOn(MealsRestController.class).getMealById(id)).withSelfRel(),
                linkTo(methodOn(MealsRestController.class).getMeals()).withRel("rest/meals"));
    }


}