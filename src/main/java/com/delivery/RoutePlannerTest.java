package com.delivery;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.*;
import com.delivery.context.RoutePlanner;
import com.delivery.selector.StrategySelector;
import com.delivery.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoutePlannerTest {
    private List<Route> routes;
    private Location locA, locB;

    @BeforeEach
    void setUp() {
        locA = new Location("A", 55.0, 37.0);
        locB = new Location("B", 55.5, 37.5);

        routes = List.of(
                new Route("R1", locA, locB, 10.0, 15, 500.0, 1.2, true),
                new Route("R2", locA, locB, 15.0, 10, 800.0, 2.5, true),
                new Route("R3", locA, locB, 25.0, 40, 200.0, 0.5, true),
                new Route("R4", locA, locB, 8.0, 20, 1200.0, 3.0, false), // unavailable
                new Route("R5", locA, locB, 12.0, 18, 600.0, 1.0, true)
        );
    }

    @Test
    void testFastestStrategy() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 1000.0, 60, false);
        Route result = planner.plan(routes, req);
        assertEquals("R2", result.id());
    }

    @Test
    void testCheapestStrategy() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.ECONOMY, 1000.0, 60, false);
        Route result = planner.plan(routes, req);
        assertEquals("R3", result.id());
    }

    @Test
    void testEcoStrategy() {
        RoutePlanner planner = new RoutePlanner(new EcoFriendlyRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.ECO, 1000.0, 60, true);
        Route result = planner.plan(routes, req);
        assertEquals("R3", result.id());
    }

    @Test
    void testBalancedStrategy() {
        RoutePlanner planner = new RoutePlanner(new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 1000.0, 60, false);
        assertNotNull(planner.plan(routes, req));
    }

    @Test
    void testRuntimeStrategySwitching() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 1000.0, 60, false);
        assertEquals("R2", planner.plan(routes, req).id());

        planner.setStrategy(new CheapestRouteStrategy());
        assertEquals("R3", planner.plan(routes, req).id());
    }

    @Test
    void testAutomaticStrategySelection() {
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 1000.0, 60, false);
        RouteStrategy strategy = StrategySelector.selectStrategy(req);
        assertInstanceOf(FastestRouteStrategy.class, strategy);
    }

    @Test
    void testHardConstraintFilteringUnavailable() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 2000.0, 60, false);
        // R4 быстрее всех по расстоянию/времени, но она false (unavailable)
        Route result = planner.plan(routes, req);
        assertNotEquals("R4", result.id());
    }

    @Test
    void testBudgetRestrictionException() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.ECONOMY, 100.0, 60, false); // слишком маленький бюджет
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(routes, req));
    }

    @Test
    void testDeadlineRestrictionException() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 1000.0, 5, false); // дедлайн 5 минут
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(routes, req));
    }

    @Test
    void testNoValidRouteExceptionEmptyList() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 1000.0, 60, false);
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(List.of(), req));
    }

    @Test
    void testInvalidBalancedWeightsException() {
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(0.5, 0.5, 0.5, 0.5));
    }

    @Test
    void testFifthStrategyPremium() {
        RoutePlanner planner = new RoutePlanner(new PremiumRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 1500.0, 60, false);
        assertNotNull(planner.plan(routes, req));
    }

    @Test
    void testStrategyComparisonMode() {
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 1000.0, 60, false);
        List<RouteStrategy> strategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2)
        );

        for (RouteStrategy s : strategies) {
            Route r = s.selectRoute(routes, req);
            assertNotNull(r);
        }
    }

    @Test
    void testBoundaryCaseExactBudget() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.ECONOMY, 500.0, 60, false); // R1 стоит ровно 500
        Route result = planner.plan(routes, req);
        assertEquals("R1", result.id());
    }

    @Test
    void testFailureScenarioMissingDataHandling() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        // Проверка обработки некорректного запроса через перехват исключения
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(null, null));
    }
}