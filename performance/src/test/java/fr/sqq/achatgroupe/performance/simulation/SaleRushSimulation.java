package fr.sqq.achatgroupe.performance.simulation;

import fr.sqq.achatgroupe.performance.config.SimulationConfig;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Simulates a sale rush using existing seed data (dev profile).
 * No admin setup required — relies on active ventes, products, and timeslots
 * already present in the database via Flyway repeatable migration R__dev_seed_data.sql.
 */
public class SaleRushSimulation extends Simulation {

    private static final String BASE_URL = SimulationConfig.BASE_URL;
    private static final int USERS = SimulationConfig.USERS;

    private static final String[] FIRST_NAMES = {
            "Alice", "Bob", "Claire", "David", "Emma", "François", "Gabrielle", "Hugo",
            "Inès", "Jules", "Karine", "Louis", "Marie", "Nicolas", "Olivia", "Pierre",
            "Quentin", "Rose", "Sophie", "Thomas", "Ursule", "Victor", "Wendy", "Xavier"
    };

    private static final String[] LAST_NAMES = {
            "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard", "Petit", "Durand",
            "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David",
            "Bertrand", "Roux", "Vincent", "Fournier", "Morel", "Girard", "André", "Mercier"
    };

    private final AtomicInteger userCounter = new AtomicInteger(0);
    private volatile String venteId;

    private Iterator<Map<String, Object>> customerFeeder() {
        return Stream.generate(() -> {
            int index = userCounter.incrementAndGet();
            ThreadLocalRandom rng = ThreadLocalRandom.current();
            String firstName = FIRST_NAMES[rng.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[rng.nextInt(LAST_NAMES.length)];
            return Map.<String, Object>of(
                    "customerFirstName", firstName,
                    "customerLastName", lastName,
                    "email", "user" + index + "@test-perf.sqq.fr",
                    "phone", String.format("06%08d", index)
            );
        }).iterator();
    }

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder saleRush = scenario("Ruée à l'ouverture")
            .feed(customerFeeder())
            // Inject the venteId discovered in before()
            .exec(session -> session.set("venteId", venteId))
            // 1. Get products
            .exec(
                    http("GET products")
                            .get("/api/ventes/#{venteId}/products")
                            .check(
                                    status().is(200),
                                    jsonPath("$.data[*].id").findAll().saveAs("productIds")
                            )
            )
            .pause(Duration.ofSeconds(1), Duration.ofSeconds(4))
            // 2. Get timeslots
            .exec(
                    http("GET timeslots")
                            .get("/api/ventes/#{venteId}/timeslots")
                            .check(
                                    status().is(200),
                                    jsonPath("$.data[*].id").findAll().optional().saveAs("timeSlotIds")
                            )
            )
            .pause(Duration.ofMillis(500), Duration.ofMillis(1500))
            // 3. Select random products and timeslot, then place order
            .doIf(session -> {
                List<?> timeSlotIds = session.getList("timeSlotIds");
                return timeSlotIds != null && !timeSlotIds.isEmpty();
            }).then(
                    exec(session -> {
                        ThreadLocalRandom rng = ThreadLocalRandom.current();

                        List<?> productIds = session.getList("productIds");
                        List<?> timeSlotIds = session.getList("timeSlotIds");

                        // Random timeslot
                        Object timeSlotId = timeSlotIds.get(rng.nextInt(timeSlotIds.size()));

                        // Random 1-3 products with quantities 1-3
                        int itemCount = rng.nextInt(1, Math.min(4, productIds.size() + 1));
                        List<Integer> indices = new ArrayList<>();
                        for (int i = 0; i < productIds.size(); i++) indices.add(i);
                        Collections.shuffle(indices, rng);

                        StringBuilder itemsJson = new StringBuilder("[");
                        for (int i = 0; i < itemCount; i++) {
                            if (i > 0) itemsJson.append(",");
                            int quantity = rng.nextInt(1, 4);
                            itemsJson.append("{\"productId\":").append(productIds.get(indices.get(i)))
                                    .append(",\"quantity\":").append(quantity).append("}");
                        }
                        itemsJson.append("]");

                        return session
                                .set("selectedTimeSlotId", timeSlotId)
                                .set("orderItems", itemsJson.toString());
                    })
                    .exec(
                            http("POST order")
                                    .post("/api/ventes/#{venteId}/orders")
                                    .body(StringBody("""
                                            {
                                              "customerFirstName": "#{customerFirstName}",
                                              "customerLastName": "#{customerLastName}",
                                              "email": "#{email}",
                                              "phone": "#{phone}",
                                              "timeSlotId": #{selectedTimeSlotId},
                                              "items": #{orderItems}
                                            }
                                            """))
                                    .check(
                                            status().in(201, 400, 409, 422)
                                    )
                    )
            );

    {
        int earlyBirds = (int) (USERS * 0.10);
        int rush = (int) (USERS * 0.60);
        int late = (int) (USERS * 0.20);
        int stragglers = USERS - earlyBirds - rush - late;

        setUp(
                saleRush.injectOpen(
                        rampUsers(earlyBirds).during(Duration.ofSeconds(10)),
                        nothingFor(Duration.ofMillis(500)),
                        rampUsers(rush).during(Duration.ofSeconds(15)),
                        nothingFor(Duration.ofMillis(500)),
                        rampUsers(late).during(Duration.ofSeconds(20)),
                        nothingFor(Duration.ofMillis(500)),
                        rampUsers(stragglers).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol)
                .assertions(
                        global().successfulRequests().percent().gt(80.0),
                        global().responseTime().percentile3().lt(5000),
                        global().responseTime().mean().lt(2000)
                );
    }

    @Override
    public void before() {
        System.out.println("=== Sale Rush Simulation ===");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("Users: " + USERS);

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // Find an active vente that has products
            String ventesJson = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/api/ventes"))
                            .header("Accept", "application/json")
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            ).body();

            List<String> venteIds = extractAllJsonValues(ventesJson, "id");
            if (venteIds.isEmpty()) {
                throw new RuntimeException("No active ventes found. Is the dev seed data loaded?");
            }

            for (String candidateId : venteIds) {
                String productsJson = client.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/api/ventes/" + candidateId + "/products"))
                                .header("Accept", "application/json")
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                ).body();

                List<String> productIds = extractAllJsonValues(productsJson, "id");
                if (!productIds.isEmpty()) {
                    venteId = candidateId;
                    System.out.println("Using vente " + venteId + " with " + productIds.size() + " products");
                    break;
                }
            }

            if (venteId == null) {
                throw new RuntimeException("No active vente with products found. Is the dev seed data loaded?");
            }

            // Verify timeslots exist
            String timeslotsJson = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/api/ventes/" + venteId + "/timeslots"))
                            .header("Accept", "application/json")
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            ).body();

            List<String> timeslotIds = extractAllJsonValues(timeslotsJson, "id");
            if (timeslotIds.isEmpty()) {
                System.out.println("WARNING: No available timeslots — orders will be skipped. Restart app to reset seed data.");
            } else {
                System.out.println("Found " + timeslotIds.size() + " available timeslots");
            }
            System.out.println("=== Starting simulation ===");

        } catch (Exception e) {
            throw new RuntimeException("Setup failed", e);
        }
    }

    private static List<String> extractAllJsonValues(String json, String key) {
        List<String> values = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([\"\\d][^,}]*)");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String value = matcher.group(1).trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            values.add(value);
        }
        return values;
    }
}
