package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public class PremiumRouteStrategy implements RouteStrategy {
    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = RouteValidator.filterValidRoutes(routes, request);
        // Например, выбирает самый короткий маршрут с наивысшим приоритетом безопасности/комфорта
        return validRoutes.stream()
                .min((r1, r2) -> Double.compare(r1.distanceKm(), r2.distanceKm()))
                .orElse(validRoutes.get(0));
    }

    @Override
    public String getStrategyName() {
        return "Premium Strategy";
    }
}