package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public class BalancedRouteStrategy implements RouteStrategy {
    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        double sum = timeWeight + costWeight + distanceWeight + emissionWeight;
        if (Math.abs(sum - 1.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum up to 1.0, current sum: " + sum);
        }
        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = RouteValidator.filterValidRoutes(routes, request);

        // Находим макс значения для нормализации
        double maxTime = validRoutes.stream().mapToInt(Route::estimatedMinutes).max().orElse(1);
        double maxCost = validRoutes.stream().mapToDouble(Route::cost).max().orElse(1);
        double maxDist = validRoutes.stream().mapToDouble(Route::distanceKm).max().orElse(1);
        double maxEmis = validRoutes.stream().mapToDouble(Route::carbonEmission).max().orElse(1);

        Route best = validRoutes.get(0);
        double minScore = Double.MAX_VALUE;

        for (Route r : validRoutes) {
            double normTime = r.estimatedMinutes() / maxTime;
            double normCost = r.cost() / maxCost;
            double normDist = r.distanceKm() / maxDist;
            double normEmis = r.carbonEmission() / maxEmis;

            double score = (normTime * timeWeight) +
                    (normCost * costWeight) +
                    (normDist * distanceWeight) +
                    (normEmis * emissionWeight);

            if (score < minScore) {
                minScore = score;
                best = r;
            }
        }
        return best;
    }

    @Override
    public String getStrategyName() {
        return "Balanced Strategy";
    }
}