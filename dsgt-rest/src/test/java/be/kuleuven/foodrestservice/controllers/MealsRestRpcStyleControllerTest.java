package be.kuleuven.foodrestservice.controllers;

import be.kuleuven.foodrestservice.domain.Meal;
import be.kuleuven.foodrestservice.domain.MealType;
import be.kuleuven.foodrestservice.domain.MealsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MealsRestRpcStyleController.class)
public class MealsRestRpcStyleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealsRepository mealsRepository;

    private Meal testMeal;

    @BeforeEach
    void setUp() {
        testMeal = new Meal();
        testMeal.setId("test-id");
        testMeal.setName("Test Meal");
        testMeal.setDescription("Test Meal Description");
        testMeal.setKcal(500);
        testMeal.setPrice(8.99);
        testMeal.setMealType(MealType.MEAT);
    }

    @Test
    void testGetMealById() throws Exception {
        when(mealsRepository.findMeal(anyString())).thenReturn(Optional.of(testMeal));

        mockMvc.perform(get("/restrpc/meals/{id}", "test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMeal.getId()))
                .andExpect(jsonPath("$.name").value(testMeal.getName()));

        verify(mealsRepository, times(1)).findMeal("test-id");
    }

    @Test
    void testGetMeals() throws Exception {
        List<Meal> meals = Arrays.asList(testMeal);
        when(mealsRepository.getAllMeal()).thenReturn(meals);

        mockMvc.perform(get("/restrpc/meals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testMeal.getId()))
                .andExpect(jsonPath("$[0].name").value(testMeal.getName()));

        verify(mealsRepository, times(1)).getAllMeal();
    }

    // Add more tests for addMeal, deleteMeal, updateMeal, getCheapestMeal, getLargestMeal, and addOrder methods.
}
