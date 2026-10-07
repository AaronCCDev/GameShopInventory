package nclan.ac.gameshopapp.ui;

import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.VBox;

import nclan.ac.gameshopapp.app.GameShopApp;
import nclan.ac.gameshopapp.module.Customer;
import nclan.ac.gameshopapp.module.AbstractGame;
import nclan.ac.gameshopapp.module.Sale;
import nclan.ac.gameshopapp.service.GameShop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BasketView {

    private final GameShopApp app;
    private final GameShop gameShop;

    private Customer currentCustomer;

    public BasketView(
            GameShopApp app,
            GameShop gameShop
    ) {

        this.app =
                app;

        this.gameShop =
                gameShop;
    }

    public VBox createBasket(
            Customer currentCustomer
    ) {

        this.currentCustomer =
                currentCustomer;

        VBox root =
                UIComponent.createLoginLayout(
                        "YOUR BASKET",
                        "Customer: "
                                + currentCustomer.getFullName()
                                + "  •  @"
                                + currentCustomer.getUsername()
                );

        ListView<String> basketList =
                new ListView<>();

        basketList.setPrefSize(
                650,
                320
        );

        basketList.setMaxWidth(
                700
        );

        UIComponent.styleBasketList(
                basketList
        );

        Label subtotalLabel =
                UIComponent.createLabel(
                        "Subtotal: £0.00",
                        17,
                        UIComponent.TEXT_SECONDARY
                );

        Label discountLabel =
                UIComponent.createLabel(
                        "Discount: £0.00",
                        17,
                        UIComponent.TEXT_SECONDARY
                );

        Label totalLabel =
                UIComponent.createLabel(
                        "Total: £0.00",
                        25,
                        UIComponent.GOLD
                );

        refreshBasket(
                basketList,
                subtotalLabel,
                discountLabel,
                totalLabel
        );

        Button removeButton =
                UIComponent.createSecondaryButton(
                        "Remove Selected Item"
                );

        Button buyButton =
                UIComponent.createButton(
                        "Buy All Games"
                );

        Button backButton =
                UIComponent.createSecondaryButton(
                        "Back to Store"
                );

        Button logoutButton =
                UIComponent.createSecondaryButton(
                        "Customer Logout"
                );

        for (Button button :
                List.of(
                        removeButton,
                        buyButton,
                        backButton,
                        logoutButton
                )) {

            button.setMaxWidth(
                    700
            );
        }

        removeButton.setOnAction(
                event -> {

                    int selectedIndex =
                            basketList
                                    .getSelectionModel()
                                    .getSelectedIndex();

                    if (selectedIndex < 0) {

                        UIComponent.showAlert(
                                "Select an item in your basket."
                        );

                        return;
                    }

                    AbstractGame selectedGame =
                            getBasketGame(
                                    selectedIndex
                            );

                    if (selectedGame != null) {

                        currentCustomer.removeFromBasket(
                                selectedGame
                        );

                        refreshBasket(
                                basketList,
                                subtotalLabel,
                                discountLabel,
                                totalLabel
                        );
                    }
                }
        );

        buyButton.setOnAction(
                event -> {

                    if (currentCustomer
                            .getBasket()
                            .isEmpty()) {

                        UIComponent.showAlert(
                                "Your basket is empty."
                        );

                        return;
                    }

                    double expectedTotal =
                            currentCustomer
                                    .getBasketTotal();

                    Alert confirmation =
                            new Alert(
                                    Alert.AlertType.CONFIRMATION
                            );

                    confirmation.setTitle(
                            "Complete Purchase"
                    );

                    confirmation.setHeaderText(
                            "Complete this order?"
                    );

                    confirmation.setContentText(
                            String.format(
                                    "Final total: £%.2f%s",
                                    expectedTotal,
                                    currentCustomer
                                            .isDiscountAvailable()
                                            ? "\nIncludes your 10% trade-in discount."
                                            : ""
                            )
                    );

                    UIComponent.styleDialog(
                            confirmation
                    );

                    Optional<ButtonType> result =
                            confirmation.showAndWait();

                    if (result.isEmpty()
                            || result.get()
                            != ButtonType.OK) {

                        return;
                    }

                    Sale completedSale =
                            gameShop.processSale(
                                    currentCustomer
                            );

                    if (completedSale == null) {

                        gameShop.loadGames();

                        UIComponent.showAlert(
                                "The order could not be completed. "
                                        + "One or more games may no longer have enough stock."
                        );

                        return;
                    }

                    showOrderPlaced(
                            completedSale
                    );
                }
        );

        backButton.setOnAction(
                event ->
                        app.showCustomerStore()
        );

        logoutButton.setOnAction(
                event ->
                        app.logoutCustomer()
        );

        root.getChildren().addAll(
                basketList,
                subtotalLabel,
                discountLabel,
                totalLabel,
                removeButton,
                buyButton,
                backButton,
                logoutButton
        );

        UIComponent.addBottomSpacer(
                root
        );

        return root;
    }

    private void refreshBasket(
            ListView<String> basketList,
            Label subtotalLabel,
            Label discountLabel,
            Label totalLabel
    ) {

        basketList
                .getItems()
                .clear();

        for (Map.Entry<AbstractGame, Integer> entry :
                currentCustomer
                        .getBasket()
                        .entrySet()) {

            AbstractGame currentGame =
                    entry.getKey();

            int quantity =
                    entry.getValue();

            double lineTotal =
                    currentGame.getPrice()
                            * quantity;

            basketList
                    .getItems()
                    .add(
                            currentGame.getTitle()
                                    + "  x"
                                    + quantity
                                    + "    £"
                                    + String.format(
                                    "%.2f",
                                    lineTotal
                            )
                    );
        }

        subtotalLabel.setText(
                String.format(
                        "Subtotal: £%.2f",
                        currentCustomer
                                .getBasketSubtotal()
                )
        );

        discountLabel.setText(
                String.format(
                        "Discount: -£%.2f",
                        currentCustomer
                                .getDiscountAmount()
                )
        );

        totalLabel.setText(
                String.format(
                        "Total: £%.2f",
                        currentCustomer
                                .getBasketTotal()
                )
        );
    }

    private AbstractGame getBasketGame(
            int index
    ) {

        List<AbstractGame> games =
                new ArrayList<>(
                        currentCustomer
                                .getBasket()
                                .keySet()
                );

        if (index < 0
                || index >= games.size()) {

            return null;
        }

        return games.get(
                index
        );
    }

    private void showOrderPlaced(
            Sale completedSale
    ) {

        VBox root =
                UIComponent.createLoginLayout(
                        "ORDER PLACED",
                        "Thank you, "
                                + completedSale.getCustomerName()
                                + "!"
                );

        root.setAlignment(
                Pos.CENTER
        );

        Label tick =
                UIComponent.createLabel(
                        "✓",
                        55,
                        UIComponent.GOLD
                );

        Label orderNumber =
                UIComponent.createLabel(
                        "Order #"
                                + completedSale.getOrderId(),
                        22,
                        "white"
                );

        Label total =
                UIComponent.createLabel(
                        String.format(
                                "Total: £%.2f",
                                completedSale.getTotal()
                        ),
                        28,
                        UIComponent.GOLD
                );

        Label message =
                UIComponent.createLabel(
                        "Your order has been placed successfully.",
                        16,
                        UIComponent.TEXT_SECONDARY
                );

        Button continueButton =
                UIComponent.createButton(
                        "Continue Shopping"
                );

        Button logoutButton =
                UIComponent.createSecondaryButton(
                        "Customer Logout"
                );

        continueButton.setMaxWidth(
                450
        );

        logoutButton.setMaxWidth(
                450
        );

        continueButton.setOnAction(
                event ->
                        app.showCustomerStore()
        );

        logoutButton.setOnAction(
                event ->
                        app.logoutCustomer()
        );

        root.getChildren().addAll(
                tick,
                orderNumber,
                total,
                message,
                continueButton,
                logoutButton
        );

        UIComponent.addBottomSpacer(
                root
        );

        app.changeScene(
                root
        );
    }
}