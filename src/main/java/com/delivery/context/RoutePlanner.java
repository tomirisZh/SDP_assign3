package com.delivery.context;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.strategy.RouteStrategy;
import java.util.List;

public class RoutePlanner {
    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public Route plan(List<Route> routes, DeliveryRequest request) {
        return strategy.selectRoute(routes, request);
    }
}