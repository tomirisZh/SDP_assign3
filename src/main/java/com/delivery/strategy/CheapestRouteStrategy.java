package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {
    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = RouteValidator.filterValidRoutes(routes, request);

        Route cheapest = validRoutes.get(0);
        for (Route r : validRoutes) {
            if (r.cost() < cheapest.cost()) {
                cheapest = r;
            }
        }
        return cheapest;
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Strategy";
    }
}