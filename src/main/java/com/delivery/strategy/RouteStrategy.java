package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public interface RouteStrategy {
    Route selectRoute(List<Route> routes, DeliveryRequest request);
    String getStrategyName();
}