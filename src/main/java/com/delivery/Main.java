package com.delivery;

import com.delivery.context.RoutePlanner;
import com.delivery.model.*;
import com.delivery.selector.StrategySelector;
import com.delivery.strategy.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Location warehouse = new Location("Central Warehouse", 55.7558, 37.6173);
        Location customer = new Location("Customer Hub", 55.7000, 37.5000);

        List<Route> routes = List.of(
                new Route("Route A", warehouse, customer, 12.5, 25, 450.0, 1.5, true),
                new Route("Route B", warehouse, customer, 10.0, 20, 600.0, 2.1, true),
                new Route("Route C", warehouse, customer, 18.0, 35, 300.0, 0.8, true),
                new Route("Route D", warehouse, customer, 9.0, 30, 750.0, 2.8, true),
                new Route("Route E", warehouse, customer, 14.0, 22, 500.0, 1.4, true),
                new Route("Route F", warehouse, customer, 22.0, 45, 250.0, 0.6, true),
                new Route("Route G", warehouse, customer, 11.0, 19, 650.0, 2.3, false) // Unavailable
        );

        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 1000.0, 50, false);

        System.out.println("=== 1. AUTOMATIC STRATEGY SELECTION & RUNTIME SWITCHING ===");
        RouteStrategy autoStrategy = StrategySelector.selectStrategy(request);
        RoutePlanner planner = new RoutePlanner(autoStrategy);
        System.out.println("Selected by Selector: " + autoStrategy.getStrategyName());
        System.out.println("Chosen Route: " + planner.plan(routes, request).id());

        // Runtime switching
        planner.setStrategy(new EcoFriendlyRouteStrategy());
        System.out.println("Switched to: Eco-Friendly -> Chosen Route: " + planner.plan(routes, request).id());

        planner.setStrategy(new CheapestRouteStrategy());
        System.out.println("Switched to: Cheapest -> Chosen Route: " + planner.plan(routes, request).id());

        System.out.println("\n=== 2. STRATEGY COMPARISON MODE ===");
        List<RouteStrategy> allStrategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2),
                new PremiumRouteStrategy()
        );

        for (RouteStrategy strategy : allStrategies) {
            try {
                Route r = strategy.selectRoute(routes, request);
                System.out.println("- " + strategy.getStrategyName() + ": " + r.id() +
                        " (Time: " + r.estimatedMinutes() + "m, Cost: " + r.cost() + ", CO2: " + r.carbonEmission() + "kg)");
            } catch (Exception e) {
                System.out.println("- " + strategy.getStrategyName() + ": Failed -> " + e.getMessage());
            }
        }
    }
}