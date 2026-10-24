package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.exception.NoSuitableRouteException;
import java.util.List;
import java.util.stream.Collectors;

public class RouteValidator {
    public static List<Route> filterValidRoutes(List<Route> routes, DeliveryRequest request) {
        List<Route> valid = routes.stream()
                .filter(Route::available)
                .filter(r -> r.cost() <= request.maxBudget())
                .filter(r -> r.estimatedMinutes() <= request.maxDeliveryMinutes())
                .collect(Collectors.toList());

        if (valid.isEmpty()) {
            throw new NoSuitableRouteException("No routes satisfy the given constraints (budget, deadline, availability).");
        }
        return valid;
    }
}