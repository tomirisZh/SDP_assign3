package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {
    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = RouteValidator.filterValidRoutes(routes, request);

        // Учитываем дополнительное условие (например, погодный/дорожный штраф к времени)
        Route best = validRoutes.get(0);
        int minTime = Integer.MAX_VALUE;

        for (Route r : validRoutes) {
            int adjustedTime = r.estimatedMinutes() + (r.distanceKm() > 50 ? 5 : 2); // штраф за расстояние/трафик
            if (adjustedTime < minTime) {
                minTime = adjustedTime;
                best = r;
            }
        }
        return best;
    }

    @Override
    public String getStrategyName() {
        return "Fastest Strategy";
    }
}