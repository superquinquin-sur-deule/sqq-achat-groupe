package fr.sqq.achatgroupe.performance.config;

public final class SimulationConfig {

    private SimulationConfig() {
    }

    public static final String BASE_URL = System.getProperty("gatling.baseUrl", "http://localhost:8081");
    public static final int USERS = Integer.getInteger("gatling.users", 500);
}
