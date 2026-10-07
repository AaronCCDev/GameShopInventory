package nclan.ac.gameshopapp.module;

import java.util.LinkedHashMap;
import java.util.Map;

public class Customer {

    private final long id;
    private final String username;

    private final String firstName;
    private final String lastName;

    private final Address address;

    private final String sessionToken;

    private boolean discountAvailable;

    private final Map<AbstractGame, Integer> basket =
            new LinkedHashMap<>();

    public Customer(
            long id,
            String username,
            String firstName,
            String lastName,
            Address address,
            String sessionToken,
            boolean discountAvailable
    ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.sessionToken = sessionToken;
        this.discountAvailable = discountAvailable;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Address getAddress() {
        return address;
    }

    public String getFullAddress() {

        if (address == null) {
            return "";
        }

        return address.getFullAddress();
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public boolean isDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(
            boolean discountAvailable
    ) {
        this.discountAvailable =
                discountAvailable;
    }

    public Map<AbstractGame, Integer> getBasket() {
        return basket;
    }

    public boolean addToBasket(
            AbstractGame selectedAbstractGame
    ) {

        if (selectedAbstractGame == null) {
            return false;
        }

        if (!selectedAbstractGame.isInStock()) {
            return false;
        }

        int currentQuantity =
                basket.getOrDefault(
                        selectedAbstractGame,
                        0
                );

        if (currentQuantity >= selectedAbstractGame.getStock()) {
            return false;
        }

        basket.put(
                selectedAbstractGame,
                currentQuantity + 1
        );

        return true;
    }

    public void removeFromBasket(
            AbstractGame selectedAbstractGame
    ) {

        if (selectedAbstractGame == null
                || !basket.containsKey(selectedAbstractGame)) {
            return;
        }

        int quantity =
                basket.get(selectedAbstractGame);

        if (quantity <= 1) {
            basket.remove(selectedAbstractGame);
        } else {
            basket.put(
                    selectedAbstractGame,
                    quantity - 1
            );
        }
    }

    public double getBasketSubtotal() {

        double subtotal = 0;

        for (Map.Entry<AbstractGame, Integer> entry :
                basket.entrySet()) {

            subtotal +=
                    entry.getKey().getPrice()
                            * entry.getValue();
        }

        return subtotal;
    }

    public double getDiscountPercent() {

        if (discountAvailable) {
            return 10.0;
        }

        return 0.0;
    }

    public double getDiscountAmount() {

        return getBasketSubtotal()
                * (getDiscountPercent() / 100.0);
    }

    public double getBasketTotal() {

        return getBasketSubtotal()
                - getDiscountAmount();
    }

    public void clearBasket() {
        basket.clear();
    }

    @Override
    public String toString() {

        return username
                + " - "
                + getFullName();
    }
}