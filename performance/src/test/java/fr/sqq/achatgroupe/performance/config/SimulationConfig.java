package fr.sqq.achatgroupe.performance.config;

public final class SimulationConfig {

    private SimulationConfig() {
    }

    public static final String BASE_URL = System.getProperty("gatling.baseUrl", "http://localhost:8081");
    public static final int USERS = Integer.getInteger("gatling.users", 500);
    public static final int PRODUCT_COUNT = Integer.getInteger("gatling.productCount", 8);
    public static final int STOCK_PER_PRODUCT = Integer.getInteger("gatling.stockPerProduct", 100);
    public static final int TIMESLOT_COUNT = Integer.getInteger("gatling.timeslotCount", 5);
    public static final int CAPACITY_PER_SLOT = Integer.getInteger("gatling.capacityPerSlot", 100);
    public static final String ADMIN_USERNAME = System.getProperty("gatling.adminUsername", "alice");
    public static final String ADMIN_PASSWORD = System.getProperty("gatling.adminPassword", "alice");
}
