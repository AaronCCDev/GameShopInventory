package nclan.ac.gameshopapp.service;

import nclan.ac.gameshopapp.database.SupabaseConnection;
import nclan.ac.gameshopapp.enums.GameType;
import nclan.ac.gameshopapp.enums.Platform;
import nclan.ac.gameshopapp.module.Address;
import nclan.ac.gameshopapp.module.Customer;
import nclan.ac.gameshopapp.module.CustomerOrder;
import nclan.ac.gameshopapp.module.AbstractGame;
import nclan.ac.gameshopapp.module.OrderItem;
import nclan.ac.gameshopapp.module.Sale;
import nclan.ac.gameshopapp.module.TradeInRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameShop {

    private final SupabaseConnection database;

    private final List<AbstractGame> games =
            new ArrayList<>();

    public GameShop() {

        database =
                new SupabaseConnection();

        loadGames();
    }

    public SupabaseConnection getDatabase() {
        return database;
    }

    public void loadGames() {

        games.clear();

        games.addAll(
                database.getGames()
        );
    }

    public List<AbstractGame> getGames() {

        return Collections.unmodifiableList(
                games
        );
    }

    public List<AbstractGame> getRetroGames() {

        List<AbstractGame> retroGames =
                new ArrayList<>();

        for (AbstractGame selectedGame : games) {

            if (selectedGame.isRetro()) {
                retroGames.add(selectedGame);
            }
        }

        return Collections.unmodifiableList(
                retroGames
        );
    }

    public List<AbstractGame> getTradeInGames() {

        List<AbstractGame> tradeInGames =
                new ArrayList<>();

        for (AbstractGame selectedGame : games) {

            if (selectedGame.isRetro()
                    && selectedGame.getStock() < 10) {

                tradeInGames.add(
                        selectedGame
                );
            }
        }

        return Collections.unmodifiableList(
                tradeInGames
        );
    }

    public boolean createCustomerAccount(
            String username,
            String firstName,
            String lastName,
            Address customerAddress,
            String password
    ) {

        if (username == null
                || username.isBlank()
                || firstName == null
                || firstName.isBlank()
                || lastName == null
                || lastName.isBlank()
                || customerAddress == null
                || !customerAddress.isComplete()
                || password == null
                || password.length() < 8) {

            return false;
        }

        return database.createCustomerAccount(
                username.trim(),
                firstName.trim(),
                lastName.trim(),
                customerAddress,
                password
        );
    }

    public Customer loginCustomer(
            String username,
            String password
    ) {

        return database.loginCustomer(
                username,
                password
        );
    }

    public boolean logoutCustomer(
            Customer selectedCustomer
    ) {

        if (selectedCustomer == null) {
            return false;
        }

        return database.logoutCustomer(
                selectedCustomer
        );
    }

    public List<Customer> searchCustomers(
            String search
    ) {

        return database.searchCustomers(
                search
        );
    }

    public List<CustomerOrder> getCustomerOrders(
            long customerId
    ) {

        return database.getCustomerOrders(
                customerId
        );
    }

    public List<OrderItem> getOrderItems(
            long orderId
    ) {

        return database.getOrderItems(
                orderId
        );
    }

    public List<TradeInRecord> getCustomerTradeIns(
            long customerId
    ) {

        return database.getCustomerTradeIns(
                customerId
        );
    }

    public AbstractGame addGame(
            String title,
            double price,
            int stock,
            Platform selectedPlatform,
            GameType selectedGameType,
            int releaseYear
    ) {

        if (title == null
                || title.isBlank()
                || price < 0
                || stock < 0
                || stock > 10
                || releaseYear <= 0
                || selectedPlatform == null
                || selectedGameType == null) {

            return null;
        }

        if (selectedGameType == GameType.PC_GAME
                && selectedPlatform != Platform.PC) {

            return null;
        }

        if (selectedGameType == GameType.CONSOLE_GAME
                && selectedPlatform == Platform.PC) {

            return null;
        }

        AbstractGame addedGame =
                database.addGame(
                        title.trim(),
                        price,
                        stock,
                        selectedPlatform,
                        selectedGameType,
                        releaseYear
                );

        if (addedGame != null) {
            games.add(addedGame);
        }

        return addedGame;
    }

    public boolean updateGame(
            AbstractGame selectedGame
    ) {

        if (selectedGame == null) {
            return false;
        }

        if (selectedGame.getTitle() == null
                || selectedGame.getTitle().isBlank()
                || selectedGame.getPrice() < 0
                || selectedGame.getStock() < 0
                || selectedGame.getStock() > 10
                || selectedGame.getReleaseYear() <= 0) {

            return false;
        }

        if (selectedGame.getGameType() == GameType.PC_GAME
                && selectedGame.getPlatform() != Platform.PC) {

            return false;
        }

        if (selectedGame.getGameType() == GameType.CONSOLE_GAME
                && selectedGame.getPlatform() == Platform.PC) {

            return false;
        }

        boolean updated =
                database.updateGame(
                        selectedGame
                );

        if (updated) {
            loadGames();
        }

        return updated;
    }

    public boolean removeGame(
            AbstractGame selectedGame
    ) {

        if (selectedGame == null) {
            return false;
        }

        boolean removed =
                database.removeGame(
                        selectedGame
                );

        if (removed) {
            games.remove(selectedGame);
        }

        return removed;
    }

    public boolean tradeInGame(
            Customer selectedCustomer,
            AbstractGame selectedGame
    ) {

        if (selectedCustomer == null
                || selectedGame == null) {

            return false;
        }

        if (selectedCustomer.isDiscountAvailable()) {
            return false;
        }

        if (!selectedGame.isRetro()) {
            return false;
        }

        if (selectedGame.getStock() >= 10) {
            return false;
        }

        boolean success =
                database.tradeInGame(
                        selectedCustomer,
                        selectedGame
                );

        if (success) {

            selectedCustomer.setDiscountAvailable(
                    true
            );

            loadGames();
        }

        return success;
    }

    public Sale processSale(
            Customer selectedCustomer
    ) {

        if (selectedCustomer == null
                || selectedCustomer
                .getBasket()
                .isEmpty()) {

            return null;
        }

        for (Map.Entry<AbstractGame, Integer> entry :
                selectedCustomer
                        .getBasket()
                        .entrySet()) {

            AbstractGame selectedGame =
                    entry.getKey();

            int quantity =
                    entry.getValue();

            if (quantity <= 0
                    || selectedGame.getStock()
                    < quantity) {

                return null;
            }
        }

        Map<AbstractGame, Integer> purchasedItems =
                new LinkedHashMap<>(
                        selectedCustomer.getBasket()
                );

        double discountAmount =
                selectedCustomer
                        .getDiscountAmount();

        double total =
                selectedCustomer
                        .getBasketTotal();

        Integer orderId =
                database.placeOrder(
                        selectedCustomer
                );

        if (orderId == null) {
            return null;
        }

        Sale completedSale =
                new Sale(
                        orderId,
                        selectedCustomer.getFullName(),
                        purchasedItems,
                        total
                );

        selectedCustomer.clearBasket();

        if (discountAmount > 0) {

            selectedCustomer
                    .setDiscountAvailable(
                            false
                    );
        }

        loadGames();

        return completedSale;
    }
}