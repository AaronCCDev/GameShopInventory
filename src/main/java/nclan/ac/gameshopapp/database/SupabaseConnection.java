package nclan.ac.gameshopapp.database;

import com.google.gson.*;

import nclan.ac.gameshopapp.enums.GameType;
import nclan.ac.gameshopapp.enums.Platform;
import nclan.ac.gameshopapp.module.Address;
import nclan.ac.gameshopapp.module.ConsoleGame;
import nclan.ac.gameshopapp.module.Customer;
import nclan.ac.gameshopapp.module.CustomerOrder;
import nclan.ac.gameshopapp.module.AbstractGame;
import nclan.ac.gameshopapp.module.OrderItem;
import nclan.ac.gameshopapp.module.PCGame;
import nclan.ac.gameshopapp.module.Staff;
import nclan.ac.gameshopapp.module.TradeInRecord;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class SupabaseConnection {

    private static final String SUPABASE_URL
            = "https://wzyfhyuwygmbflvvzcdk.supabase.co";

    private static final String SUPABASE_KEY
            = "sb_publishable_dLP09nOtNFaoqZ-urqd-bQ_-NRPJXuK";

    private static final String GAMES_URL
            = SUPABASE_URL + "/rest/v1/games";

    private static final String STAFF_URL
            = SUPABASE_URL + "/rest/v1/staff";

    private static final String AUTH_URL
            = SUPABASE_URL + "/auth/v1/token?grant_type=password";

    private static final String CREATE_CUSTOMER_URL
            = SUPABASE_URL + "/rest/v1/rpc/create_customer_account";

    private static final String CUSTOMER_LOGIN_URL
            = SUPABASE_URL + "/rest/v1/rpc/customer_login";

    private static final String CUSTOMER_LOGOUT_URL
            = SUPABASE_URL + "/rest/v1/rpc/customer_logout";

    private static final String PLACE_ORDER_URL
            = SUPABASE_URL + "/rest/v1/rpc/place_order";

    private static final String TRADE_IN_URL
            = SUPABASE_URL + "/rest/v1/rpc/trade_in_game";

    private static final String STAFF_SEARCH_CUSTOMERS_URL
            = SUPABASE_URL + "/rest/v1/rpc/staff_search_customers";

    private static final String STAFF_CUSTOMER_ORDERS_URL
            = SUPABASE_URL + "/rest/v1/rpc/staff_get_customer_orders";

    private static final String STAFF_ORDER_ITEMS_URL
            = SUPABASE_URL + "/rest/v1/rpc/staff_get_order_items";

    private static final String STAFF_CUSTOMER_TRADE_INS_URL
            = SUPABASE_URL + "/rest/v1/rpc/staff_get_customer_trade_ins";

    private final HttpClient client
            = HttpClient.newHttpClient();

    private final Gson gson
            = new Gson();

    private String staffAccessToken;

    public boolean isStaffLoggedIn() {
        return staffAccessToken != null
                && !staffAccessToken.isBlank();
    }

    public void logoutStaff() {
        staffAccessToken = null;
    }

    public Staff loginStaff(String staffId, String password)
    {
        if (staffId == null
                || staffId.isBlank()
                || password == null
                || password.isBlank()) {
            return null;
        }

        String normalisedId = staffId.trim().toUpperCase();
        String email = normalisedId.toLowerCase() + "@gameshop.local";
        JsonObject loginData = new JsonObject();
        loginData.addProperty("email", email);
        loginData.addProperty("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                        .header("apikey", SUPABASE_KEY)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(loginData)))
                        .build();
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (!isSuccessful(response)) {
                printDatabaseError("Staff login failed", response);
                return null;
            }

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            if (!responseJson.has("access_token")) {
                return null;
            }

            String accessToken = responseJson
                    .get("access_token")
                    .getAsString();

            Staff loggedInStaff = getStaffRecord(normalisedId, accessToken);

            if (loggedInStaff == null) {
                System.out.println("Authentication succeeded but no matching staff record was found.");
                return null;
            }

            staffAccessToken = accessToken;
            return loggedInStaff;

        } catch (IOException | InterruptedException | RuntimeException e) {
            handleException("Could not log in.", e);
            return null;
        }
    }

    private Staff getStaffRecord(String staffId, String accessToken)
    {

        String encodedStaffId = URLEncoder.encode(
                staffId,
                StandardCharsets.UTF_8
                );

        String url = STAFF_URL + "?staff_id=eq." + encodedStaffId + "&select=staff_id,name,role,user_id";

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("apikey", SUPABASE_KEY)
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isSuccessful(response)) {
                printDatabaseError("Could not load staff record", response);
                return null;
            }

            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

            if (array.isEmpty()) {
                return null;
            }

            JsonObject object = array.get(0).getAsJsonObject();

            String returnedStaffId = object.get("staff_id").getAsString();

            String name = object.get("name").getAsString();

            String role = getNullableString(object, "role");

            String userId = getNullableString(object, "user_id");

            return new Staff(returnedStaffId, name, role);

        } catch (IOException | InterruptedException | RuntimeException e) {
            handleException("Could not load staff record.", e);

            return null;
        }
    }

    public boolean createCustomerAccount(
            String username,
            String firstName,
            String lastName,
            Address customerAddress,
            String password
    ) {
        if (username == null || username.isBlank()
                || firstName == null || firstName.isBlank()
                || lastName == null || lastName.isBlank()
                || customerAddress == null || !customerAddress.isComplete()
                || password == null || password.length() < 8) {
            return false;
        }

        JsonObject data = new JsonObject();
        data.addProperty("p_username", username.trim());
        data.addProperty("p_first_name", firstName.trim());
        data.addProperty("p_last_name", lastName.trim());
        data.addProperty("p_house_number", customerAddress.getHouseNumber());
        data.addProperty("p_street", customerAddress.getStreet());
        data.addProperty("p_city", customerAddress.getCity());
        data.addProperty("p_postcode", customerAddress.getPostcode());
        data.addProperty("p_country", customerAddress.getCountry());
        data.addProperty("p_password", password);

        HttpRequest request = createPublicRequest(CREATE_CUSTOMER_URL)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isSuccessful(response)) {
                printDatabaseError("Could not create customer account", response);
                return false;
            }
            String body = response.body().trim();
            if (body.isEmpty() || body.equals("null")) return false;
            JsonElement result = JsonParser.parseString(body);
            if (!result.isJsonObject()) return false;
            JsonObject object = result.getAsJsonObject();
            return object.has("customer_id") || object.has("user_id");
        } catch (IOException | InterruptedException | RuntimeException e) {
            handleException("Could not create customer account.", e);
            return false;
        }
    }

    public Customer loginCustomer(
            String username,
            String password
    ) {

        if (username == null
                || username.isBlank()
                || password == null
                || password.isBlank()) {

            return null;
        }

        JsonObject data = new JsonObject();

        data.addProperty(
                "p_username",
                username.trim()
        );

        data.addProperty(
                "p_password",
                password
        );

        HttpRequest request =
                createPublicRequest(CUSTOMER_LOGIN_URL)
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Customer login failed",
                        response
                );

                return null;
            }

            String body =
                    response.body().trim();

            if (body.isEmpty()
                    || body.equals("null")) {

                return null;
            }

            JsonObject object =
                    JsonParser.parseString(body)
                            .getAsJsonObject();

            if (object.has("success")
                    && !object.get("success").getAsBoolean()) {

                return null;
            }

            long customerId =
                    object.has("customer_id")
                            && !object.get("customer_id").isJsonNull()
                            ? object.get("customer_id").getAsLong()
                            : 0;

            Address customerAddress =
                    new Address(
                            getNullableString(
                                    object,
                                    "house_number"
                            ),
                            getNullableString(
                                    object,
                                    "street"
                            ),
                            getNullableString(
                                    object,
                                    "city"
                            ),
                            getNullableString(
                                    object,
                                    "postcode"
                            ),
                            getNullableString(
                                    object,
                                    "country"
                            )
                    );

            String sessionToken =
                    getNullableString(
                            object,
                            "session_token"
                    );

            if (sessionToken.isBlank()) {
                return null;
            }

            boolean discountAvailable =
                    object.has("discount_available")
                            && !object.get("discount_available").isJsonNull()
                            && object.get("discount_available").getAsBoolean();

            return new Customer(
                    customerId,
                    getNullableString(
                            object,
                            "username"
                    ),
                    getNullableString(
                            object,
                            "first_name"
                    ),
                    getNullableString(
                            object,
                            "last_name"
                    ),
                    customerAddress,
                    sessionToken,
                    discountAvailable
            );

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not log customer in.",
                    e
            );

            return null;
        }
    }

    public boolean logoutCustomer(
            Customer currentCustomer
    ) {

        if (currentCustomer == null
                || currentCustomer.getSessionToken() == null
                || currentCustomer.getSessionToken().isBlank()) {

            return false;
        }

        JsonObject data =
                new JsonObject();

        data.addProperty(
                "p_session_token",
                currentCustomer.getSessionToken()
        );

        HttpRequest request =
                createPublicRequest(
                        CUSTOMER_LOGOUT_URL
                )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not log customer out",
                        response
                );

                return false;
            }

            String body =
                    response.body().trim();

            if (body.isEmpty()
                    || body.equals("null")) {

                return true;
            }

            JsonElement result =
                    JsonParser.parseString(body);

            if (result.isJsonPrimitive()
                    && result.getAsJsonPrimitive()
                    .isBoolean()) {

                return result.getAsBoolean();
            }

            if (result.isJsonObject()) {

                JsonObject object =
                        result.getAsJsonObject();

                if (object.has("success")) {

                    return object
                            .get("success")
                            .getAsBoolean();
                }
            }

            return true;

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not log customer out.",
                    e
            );

            return false;
        }
    }

    public List<AbstractGame> getGames() {

        List<AbstractGame> abstractGames =
                new ArrayList<>();

        String url =
                GAMES_URL
                        + "?select=id,title,price,stock,platform,game_type,release_year"
                        + "&order=title.asc";

        HttpRequest request =
                createPublicRequest(url)
                        .GET()
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not load games",
                        response
                );

                return abstractGames;
            }

            JsonArray array =
                    JsonParser.parseString(
                            response.body()
                    ).getAsJsonArray();

            for (JsonElement element : array) {

                JsonObject object =
                        element.getAsJsonObject();

                AbstractGame loadedAbstractGame =
                        gameFromJson(object);

                if (loadedAbstractGame != null) {

                    abstractGames.add(
                            loadedAbstractGame
                    );
                }
            }

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not load games from database.",
                    e
            );
        }

        return abstractGames;
    }

    public AbstractGame addGame(
            String title,
            double price,
            int stock,
            Platform selectedPlatform,
            GameType selectedGameType,
            int releaseYear
    ) {

        if (!isStaffLoggedIn()) {

            System.out.println(
                    "Add game rejected: staff is not authenticated."
            );

            return null;
        }

        JsonObject data =
                new JsonObject();

        data.addProperty(
                "title",
                title
        );

        data.addProperty(
                "price",
                price
        );

        data.addProperty(
                "stock",
                stock
        );

        data.addProperty(
                "platform",
                selectedPlatform.name()
        );

        data.addProperty(
                "game_type",
                selectedGameType.name()
        );

        data.addProperty(
                "release_year",
                releaseYear
        );

        HttpRequest request =
                createStaffRequest(
                        GAMES_URL
                )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .header(
                                "Prefer",
                                "return=representation"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not add game",
                        response
                );

                return null;
            }

            JsonArray array =
                    JsonParser.parseString(
                            response.body()
                    ).getAsJsonArray();

            if (array.isEmpty()) {

                return null;
            }

            return gameFromJson(
                    array.get(0)
                            .getAsJsonObject()
            );

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not add game.",
                    e
            );

            return null;
        }
    }

    public boolean updateGame(
            AbstractGame selectedAbstractGame
    ) {

        if (!isStaffLoggedIn()
                || selectedAbstractGame == null) {

            return false;
        }

        JsonObject data =
                new JsonObject();

        data.addProperty(
                "title",
                selectedAbstractGame.getTitle()
        );

        data.addProperty(
                "price",
                selectedAbstractGame.getPrice()
        );

        data.addProperty(
                "stock",
                selectedAbstractGame.getStock()
        );

        data.addProperty(
                "platform",
                selectedAbstractGame.getPlatform().name()
        );

        data.addProperty(
                "game_type",
                selectedAbstractGame.getGameType().name()
        );

        data.addProperty(
                "release_year",
                selectedAbstractGame.getReleaseYear()
        );

        String url =
                GAMES_URL
                        + "?id=eq."
                        + selectedAbstractGame.getId();

        HttpRequest request =
                createStaffRequest(url)
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .header(
                                "Prefer",
                                "return=representation"
                        )
                        .method(
                                "PATCH",
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not update game",
                        response
                );

                return false;
            }

            JsonArray array =
                    JsonParser.parseString(
                            response.body()
                    ).getAsJsonArray();

            return !array.isEmpty();

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not update game.",
                    e
            );

            return false;
        }
    }

    public boolean removeGame(
            AbstractGame selectedAbstractGame
    ) {

        if (!isStaffLoggedIn()
                || selectedAbstractGame == null) {

            return false;
        }

        String url =
                GAMES_URL
                        + "?id=eq."
                        + selectedAbstractGame.getId();

        HttpRequest request =
                createStaffRequest(url)
                        .header(
                                "Prefer",
                                "return=representation"
                        )
                        .DELETE()
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not remove game",
                        response
                );

                return false;
            }

            JsonArray array =
                    JsonParser.parseString(
                            response.body()
                    ).getAsJsonArray();

            return !array.isEmpty();

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not remove game.",
                    e
            );

            return false;
        }
    }

    public Integer placeOrder(
            Customer currentCustomer
    ) {

        if (currentCustomer == null
                || currentCustomer.getSessionToken() == null
                || currentCustomer.getSessionToken().isBlank()
                || currentCustomer.getBasket().isEmpty()) {

            return null;
        }

        JsonArray items =
                new JsonArray();

        currentCustomer.getBasket()
                .forEach(
                        (currentGame, quantity) -> {

                            JsonObject item =
                                    new JsonObject();

                            item.addProperty(
                                    "game_id",
                                    currentGame.getId()
                            );

                            item.addProperty(
                                    "quantity",
                                    quantity
                            );

                            items.add(item);
                        }
                );

        JsonObject data =
                new JsonObject();

        data.addProperty(
                "p_session_token",
                currentCustomer.getSessionToken()
        );

        data.add(
                "p_items",
                items
        );

        HttpRequest request =
                createPublicRequest(
                        PLACE_ORDER_URL
                )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not place order",
                        response
                );

                return null;
            }

            String body =
                    response.body().trim();

            if (body.isEmpty()
                    || body.equals("null")) {

                return null;
            }

            JsonElement result =
                    JsonParser.parseString(body);

            if (result.isJsonPrimitive()) {

                return result.getAsInt();
            }

            if (result.isJsonObject()) {

                JsonObject object =
                        result.getAsJsonObject();

                if (object.has("order_id")
                        && !object.get("order_id")
                        .isJsonNull()) {

                    return object
                            .get("order_id")
                            .getAsInt();
                }
            }

            return null;

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not place order.",
                    e
            );

            return null;
        }
    }

    public boolean tradeInGame(
            Customer currentCustomer,
            AbstractGame selectedAbstractGame
    ) {

        if (currentCustomer == null
                || selectedAbstractGame == null
                || currentCustomer.getSessionToken() == null
                || currentCustomer.getSessionToken().isBlank()) {

            return false;
        }

        if (!selectedAbstractGame.isRetro()) {

            return false;
        }

        if (selectedAbstractGame.getStock() >= 10) {

            return false;
        }

        JsonObject data =
                new JsonObject();

        data.addProperty(
                "p_session_token",
                currentCustomer.getSessionToken()
        );

        data.addProperty(
                "p_game_id",
                selectedAbstractGame.getId()
        );

        HttpRequest request =
                createPublicRequest(
                        TRADE_IN_URL
                )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not trade in game",
                        response
                );

                return false;
            }

            String body =
                    response.body().trim();

            if (body.isEmpty()
                    || body.equals("null")) {

                return false;
            }

            JsonElement result =
                    JsonParser.parseString(body);

            if (result.isJsonPrimitive()
                    && result.getAsJsonPrimitive()
                    .isBoolean()) {

                boolean success =
                        result.getAsBoolean();

                if (success) {

                    currentCustomer.setDiscountAvailable(
                            true
                    );
                }

                return success;
            }

            if (result.isJsonObject()) {

                JsonObject object =
                        result.getAsJsonObject();

                boolean success =
                        !object.has("success")
                                || object.get("success")
                                .getAsBoolean();

                if (success) {

                    currentCustomer.setDiscountAvailable(
                            true
                    );
                }

                return success;
            }

            return false;

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not trade in game.",
                    e
            );

            return false;
        }
    }

    public List<Customer> searchCustomers(
            String search
    ) {

        List<Customer> customers =
                new ArrayList<>();

        if (!isStaffLoggedIn()) {
            return customers;
        }

        JsonObject data =
                new JsonObject();

        data.addProperty(
                "p_search",
                search == null
                        ? ""
                        : search.trim()
        );

        HttpRequest request =
                createStaffRequest(
                        STAFF_SEARCH_CUSTOMERS_URL
                )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        gson.toJson(data)
                                )
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (!isSuccessful(response)) {

                printDatabaseError(
                        "Could not search customers",
                        response
                );

                return customers;
            }

            JsonArray array =
                    JsonParser.parseString(
                            response.body()
                    ).getAsJsonArray();

            for (JsonElement element : array) {

                JsonObject object =
                        element.getAsJsonObject();

                Address customerAddress =
                        new Address(
                                getNullableString(
                                        object,
                                        "house_number"
                                ),
                                getNullableString(
                                        object,
                                        "street"
                                ),
                                getNullableString(
                                        object,
                                        "city"
                                ),
                                getNullableString(
                                        object,
                                        "postcode"
                                ),
                                getNullableString(
                                        object,
                                        "country"
                                )
                        );

                boolean discountAvailable =
                        object.has("discount_available")
                                && !object.get("discount_available").isJsonNull()
                                && object.get("discount_available").getAsBoolean();

                Customer customer =
                        new Customer(
                                object.get("customer_id")
                                        .getAsLong(),
                                getNullableString(
                                        object,
                                        "username"
                                ),
                                getNullableString(
                                        object,
                                        "first_name"
                                ),
                                getNullableString(
                                        object,
                                        "last_name"
                                ),
                                customerAddress,
                                "",
                                discountAvailable
                        );

                customers.add(customer);
            }

        } catch (IOException
                 | InterruptedException
                 | RuntimeException e) {

            handleException(
                    "Could not search customers.",
                    e
            );
        }

        return customers;
    }

    public List<CustomerOrder> getCustomerOrders(long customerId) {
        List<CustomerOrder> orders = new ArrayList<>();
        if (!isStaffLoggedIn()) return orders;
        JsonObject data = new JsonObject(); data.addProperty("p_customer_id", customerId);
        try {
            HttpResponse<String> response = client.send(createStaffRequest(STAFF_CUSTOMER_ORDERS_URL).header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data))).build(), HttpResponse.BodyHandlers.ofString());
            if (!isSuccessful(response)) { printDatabaseError("Could not load customer orders", response); return orders; }
            for (JsonElement e : JsonParser.parseString(response.body()).getAsJsonArray()) {
                JsonObject o=e.getAsJsonObject();
                orders.add(new CustomerOrder(
                        o.get("order_id").getAsLong(),
                        parseDate(getNullableString(o, "order_date")),
                        getDouble(o, "total")
                ));
            }
        } catch (IOException | InterruptedException | RuntimeException e) { handleException("Could not load customer orders.", e); }
        return orders;
    }

    public List<OrderItem> getOrderItems(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        if (!isStaffLoggedIn()) return items;
        JsonObject data = new JsonObject(); data.addProperty("p_order_id", orderId);
        try {
            HttpResponse<String> response = client.send(createStaffRequest(STAFF_ORDER_ITEMS_URL).header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data))).build(), HttpResponse.BodyHandlers.ofString());
            if (!isSuccessful(response)) { printDatabaseError("Could not load order items", response); return items; }
            for (JsonElement e : JsonParser.parseString(response.body()).getAsJsonArray()) {
                JsonObject o=e.getAsJsonObject();
                items.add(new OrderItem(
                        getNullableString(o, "game_title"),
                        o.get("quantity").getAsInt(),
                        getDouble(o, "price"),
                        getDouble(o, "line_total")
                ));
            }
        } catch (IOException | InterruptedException | RuntimeException e) { handleException("Could not load order items.", e); }
        return items;
    }

    public List<TradeInRecord> getCustomerTradeIns(long customerId) {
        List<TradeInRecord> records = new ArrayList<>();
        if (!isStaffLoggedIn()) return records;
        JsonObject data = new JsonObject(); data.addProperty("p_customer_id", customerId);
        try {
            HttpResponse<String> response = client.send(createStaffRequest(STAFF_CUSTOMER_TRADE_INS_URL).header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data))).build(), HttpResponse.BodyHandlers.ofString());
            if (!isSuccessful(response)) { printDatabaseError("Could not load trade-ins", response); return records; }
            for (JsonElement e : JsonParser.parseString(response.body()).getAsJsonArray()) {
                JsonObject o=e.getAsJsonObject();
                records.add(new TradeInRecord(
                        getNullableString(o, "game_title"),
                        parseDate(getNullableString(o, "trade_in_date")),
                        getDouble(o, "discount_percent")
                ));
            }
        } catch (IOException | InterruptedException | RuntimeException e) { handleException("Could not load trade-ins.", e); }
        return records;
    }

    private AbstractGame gameFromJson(
            JsonObject object
    ) {

        int id =
                object.get("id")
                        .getAsInt();

        String title =
                object.get("title")
                        .getAsString();

        double price =
                object.get("price")
                        .getAsDouble();

        int stock =
                object.get("stock")
                        .getAsInt();

        Platform selectedPlatform =
                Platform.valueOf(
                        object.get("platform")
                                .getAsString()
                                .toUpperCase()
                );

        GameType selectedGameType =
                GameType.valueOf(
                        object.get("game_type")
                                .getAsString()
                                .toUpperCase()
                );

        int releaseYear =
                object.get("release_year")
                        .getAsInt();

        if (selectedGameType == GameType.PC_GAME) {

            return new PCGame(
                    id,
                    title,
                    price,
                    stock,
                    selectedPlatform,
                    releaseYear
            );
        }

        return new ConsoleGame(
                id,
                title,
                price,
                stock,
                selectedPlatform,
                releaseYear
        );
    }

    private String getNullableString(
            JsonObject object,
            String property
    ) {

        if (!object.has(property)
                || object.get(property).isJsonNull()) {

            return "";
        }

        return object
                .get(property)
                .getAsString();
    }

    private double getDouble(JsonObject object, String property) {
        return !object.has(property) || object.get(property).isJsonNull() ? 0.0 : object.get(property).getAsDouble();
    }

    private OffsetDateTime parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try { return OffsetDateTime.parse(value); } catch (RuntimeException e) { return null; }
    }

    private HttpRequest.Builder createPublicRequest(
            String url
    ) {

        return HttpRequest.newBuilder()
                .uri(
                        URI.create(url)
                )
                .header(
                        "apikey",
                        SUPABASE_KEY
                )
                .header(
                        "Authorization",
                        "Bearer "
                                + SUPABASE_KEY
                );
    }

    private HttpRequest.Builder createStaffRequest(
            String url
    ) {

        return HttpRequest.newBuilder()
                .uri(
                        URI.create(url)
                )
                .header(
                        "apikey",
                        SUPABASE_KEY
                )
                .header(
                        "Authorization",
                        "Bearer "
                                + staffAccessToken
                );
    }

    private boolean isSuccessful(
            HttpResponse<String> response
    ) {

        return response.statusCode() >= 200
                && response.statusCode() < 300;
    }

    private void printDatabaseError(
            String message,
            HttpResponse<String> response
    ) {

        System.out.println(
                message
                        + ": "
                        + response.statusCode()
        );

        System.out.println(
                response.body()
        );
    }

    private void handleException(
            String message,
            Exception e
    ) {

        if (e instanceof InterruptedException) {

            Thread.currentThread()
                    .interrupt();
        }

        System.out.println(message);

        e.printStackTrace();
    }
}