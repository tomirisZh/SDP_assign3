package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {
    private static final double EMISSION_WEIGHT = 0.7;
    private static final double DISTANCE_WEIGHT = 0.3;

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = RouteValidator.filterValidRoutes(routes, request);

        Route bestEco = validRoutes.get(0);
        double minScore = Double.MAX_VALUE;

        for (Route r : validRoutes) {
            double ecoScore = (r.carbonEmission() * EMISSION_WEIGHT) + (r.distanceKm() * DISTANCE_WEIGHT);
            if (ecoScore < minScore) {
                minScore = ecoScore;
                bestEco = r;
            }
        }
        return bestEco;
    }

    @Override
    public String getStrategyName() {
        return "Eco-Friendly Strategy";
    }
}