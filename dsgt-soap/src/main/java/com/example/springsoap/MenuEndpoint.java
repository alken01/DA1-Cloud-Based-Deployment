package com.example.springsoap;


import io.foodmenu.gt.webservice.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class MenuEndpoint {
    private static final String NAMESPACE_URI = "http://foodmenu.io/gt/webservice";

    private MealRepository mealrepo;

    @Autowired
    public MenuEndpoint(MealRepository mealrepo) {
        this.mealrepo = mealrepo;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getMealRequest")
    @ResponsePayload
    public GetMealResponse getMeal(@RequestPayload GetMealRequest request) {
        GetMealResponse response = new GetMealResponse();
        response.setMeal(mealrepo.findMeal(request.getName()));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLargestMealRequest")
    @ResponsePayload
    public GetLargestMealResponse getLargestMeal(@RequestPayload GetLargestMealRequest request) {
        GetLargestMealResponse response = new GetLargestMealResponse();
        response.setMeal(mealrepo.findBiggestMeal());

        return response;
    }


}