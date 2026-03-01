package fr.sqq.achatgroupe.performance.simulation;

import fr.sqq.achatgroupe.performance.config.SimulationConfig;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class SaleRushSimulation extends Simulation {

    private static final String BASE_URL = SimulationConfig.BASE_URL;
    private static final int USERS = SimulationConfig.USERS;
    private static final int PRODUCT_COUNT = SimulationConfig.PRODUCT_COUNT;
    private static final int STOCK_PER_PRODUCT = SimulationConfig.STOCK_PER_PRODUCT;
    private static final int TIMESLOT_COUNT = SimulationConfig.TIMESLOT_COUNT;
    private static final int CAPACITY_PER_SLOT = SimulationConfig.CAPACITY_PER_SLOT;

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
            // 1. Get active vente
            .exec(
                    http("GET ventes")
                            .get("/api/ventes")
                            .check(
                                    status().is(200),
                                    jsonPath("$.data[0].id").saveAs("venteId")
                            )
            )
            .pause(Duration.ofMillis(500), Duration.ofMillis(2000))
            // 2. Get products
            .exec(
                    http("GET products")
                            .get("/api/ventes/#{venteId}/products")
                            .check(
                                    status().is(200),
                                    jsonPath("$.data[*].id").ofList().saveAs("productIds")
                            )
            )
            .pause(Duration.ofSeconds(1), Duration.ofSeconds(4))
            // 3. Get timeslots
            .exec(
                    http("GET timeslots")
                            .get("/api/ventes/#{venteId}/timeslots")
                            .check(
                                    status().is(200),
                                    jsonPath("$.data[*].id").ofList().saveAs("timeSlotIds")
                            )
            )
            .pause(Duration.ofMillis(500), Duration.ofMillis(1500))
            // 4. Select random products and timeslot, then place order
            .exec(session -> {
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
        System.out.println("=== Setting up test data ===");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("Users: " + USERS);
        System.out.println("Products: " + PRODUCT_COUNT + " (stock: " + STOCK_PER_PRODUCT + " each)");
        System.out.println("Timeslots: " + TIMESLOT_COUNT + " (capacity: " + CAPACITY_PER_SLOT + " each)");

        try {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .cookieHandler(cookieManager)
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            authenticateAdmin(client);

            // 1. Create vente
            Instant now = Instant.now();
            Instant endDate = now.plus(Duration.ofDays(30));
            String venteBody = """
                    {
                      "name": "Vente Performance Test %s",
                      "description": "Auto-generated for Gatling performance test",
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                    """.formatted(now.toEpochMilli(), now.toString(), endDate.toString());

            HttpResponse<String> venteResponse = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/api/admin/ventes"))
                            .header("Content-Type", "application/json")
                            .header("X-Requested-With", "XMLHttpRequest")
                            .POST(HttpRequest.BodyPublishers.ofString(venteBody))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (venteResponse.statusCode() != 201) {
                throw new RuntimeException("Failed to create vente: " + venteResponse.statusCode() + " - " + venteResponse.body());
            }

            String venteId = extractJsonValue(venteResponse.body(), "id");
            System.out.println("Created vente: " + venteId);

            // 2. Create products
            for (int i = 1; i <= PRODUCT_COUNT; i++) {
                String productBody = """
                        {
                          "name": "Produit Perf %d",
                          "description": "Produit de test performance",
                          "prixHt": %.2f,
                          "tauxTva": 20.0,
                          "supplier": "Fournisseur Test",
                          "stock": %d,
                          "reference": "PERF-%03d",
                          "category": "Test",
                          "brand": "PerfBrand",
                          "colisage": 1
                        }
                        """.formatted(i, 5.0 + i, STOCK_PER_PRODUCT, i);

                HttpResponse<String> productResponse = client.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/api/admin/ventes/" + venteId + "/products"))
                                .header("Content-Type", "application/json")
                                .header("X-Requested-With", "XMLHttpRequest")
                                .POST(HttpRequest.BodyPublishers.ofString(productBody))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );

                if (productResponse.statusCode() != 200) {
                    throw new RuntimeException("Failed to create product " + i + ": " + productResponse.statusCode() + " - " + productResponse.body());
                }
                System.out.println("  Created product " + i + "/" + PRODUCT_COUNT);
            }

            // 3. Create timeslots
            LocalDate baseDate = LocalDate.now().plusDays(7);
            for (int i = 0; i < TIMESLOT_COUNT; i++) {
                LocalDate slotDate = baseDate.plusDays(i);
                String timeslotBody = """
                        {
                          "date": "%s",
                          "startTime": "09:00",
                          "endTime": "12:00",
                          "capacity": %d
                        }
                        """.formatted(slotDate.format(DateTimeFormatter.ISO_LOCAL_DATE), CAPACITY_PER_SLOT);

                HttpResponse<String> timeslotResponse = client.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(BASE_URL + "/api/admin/ventes/" + venteId + "/timeslots"))
                                .header("Content-Type", "application/json")
                                .header("X-Requested-With", "XMLHttpRequest")
                                .POST(HttpRequest.BodyPublishers.ofString(timeslotBody))
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );

                if (timeslotResponse.statusCode() != 200) {
                    throw new RuntimeException("Failed to create timeslot " + (i + 1) + ": " + timeslotResponse.statusCode() + " - " + timeslotResponse.body());
                }
                System.out.println("  Created timeslot " + (i + 1) + "/" + TIMESLOT_COUNT);
            }

            // 4. Activate the vente
            HttpResponse<String> activateResponse = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/api/admin/ventes/" + venteId + "/activate"))
                            .header("Content-Type", "application/json")
                            .header("X-Requested-With", "XMLHttpRequest")
                            .PUT(HttpRequest.BodyPublishers.noBody())
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (activateResponse.statusCode() != 200) {
                throw new RuntimeException("Failed to activate vente: " + activateResponse.statusCode() + " - " + activateResponse.body());
            }
            System.out.println("Vente activated successfully!");
            System.out.println("=== Setup complete, starting simulation ===");

        } catch (Exception e) {
            throw new RuntimeException("Setup failed", e);
        }
    }

    private void authenticateAdmin(HttpClient client) throws Exception {
        String username = SimulationConfig.ADMIN_USERNAME;
        String password = SimulationConfig.ADMIN_PASSWORD;
        System.out.println("Authenticating as " + username + " via OIDC...");

        // 1. GET an admin endpoint → follows redirects to Keycloak login page
        HttpResponse<String> loginPage = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/api/admin/ventes"))
                        .header("Accept", "text/html,application/xhtml+xml,*/*")
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // If we got a JSON response, auth is disabled (test profile) — skip
        String contentType = loginPage.headers().firstValue("content-type").orElse("");
        if (contentType.contains("application/json") || loginPage.statusCode() == 200) {
            System.out.println("Admin endpoints accessible without auth (test profile?) — skipping login");
            return;
        }

        // 2. Parse the Keycloak login form action URL from HTML
        String formAction = extractFormAction(loginPage.body());
        if (formAction == null) {
            throw new RuntimeException("Could not find login form in Keycloak response (status="
                    + loginPage.statusCode() + "): " + loginPage.body().substring(0, Math.min(500, loginPage.body().length())));
        }

        // 3. POST credentials to Keycloak
        String formBody = "username=" + username + "&password=" + password;
        HttpResponse<String> loginResult = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(formAction))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(formBody))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("Authentication completed (status=" + loginResult.statusCode() + ")");
    }

    private static String extractFormAction(String html) {
        int formIndex = html.indexOf("<form");
        if (formIndex == -1) return null;
        int actionIndex = html.indexOf("action=\"", formIndex);
        if (actionIndex == -1) return null;
        int start = actionIndex + "action=\"".length();
        int end = html.indexOf("\"", start);
        if (end == -1) return null;
        return html.substring(start, end).replace("&amp;", "&");
    }

    private static String extractJsonValue(String json, String key) {
        // Simple extraction for "key": value or "key": "value"
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            throw new RuntimeException("Key '" + key + "' not found in JSON: " + json);
        }
        int colonIndex = json.indexOf(':', keyIndex + searchKey.length());
        int valueStart = colonIndex + 1;
        // Skip whitespace
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        if (json.charAt(valueStart) == '"') {
            // String value
            int valueEnd = json.indexOf('"', valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // Numeric value
            int valueEnd = valueStart;
            while (valueEnd < json.length() && (Character.isDigit(json.charAt(valueEnd)) || json.charAt(valueEnd) == '-' || json.charAt(valueEnd) == '.')) {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd);
        }
    }
}
