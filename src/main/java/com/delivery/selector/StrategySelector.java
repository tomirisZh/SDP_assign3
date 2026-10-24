package com.delivery.selector;

import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.strategy.*;

public class StrategySelector {
    public static RouteStrategy selectStrategy(DeliveryRequest request) {
        return switch (request.priority()) {
            case URGENT -> new FastestRouteStrategy();
            case ECONOMY -> new CheapestRouteStrategy();
            case ECO -> new EcoFriendlyRouteStrategy();
            case STANDARD -> new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        };
    }
}