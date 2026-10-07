package nclan.ac.gameshopapp.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import nclan.ac.gameshopapp.app.GameShopApp;
import nclan.ac.gameshopapp.enums.GameType;
import nclan.ac.gameshopapp.enums.Platform;
import nclan.ac.gameshopapp.module.Customer;
import nclan.ac.gameshopapp.module.AbstractGame;
import nclan.ac.gameshopapp.service.GameShop;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class CustomerView {

    private final GameShopApp app;
    private final GameShop gameShop;

    private Customer currentCustomer;

    private enum storeView {

        ALL_GAMES("All Games"),
        RETRO_GAMES("Retro Games"),
        TRADE_IN("Trade In");

        private final String displayName;

        storeView(
                String displayName
        ) {

            this.displayName =
                    displayName;
        }

        @Override
        public String toString() {

            return displayName;
        }
    }

    public CustomerView(
            GameShopApp app,
            GameShop gameShop
    ) {

        this.app =
                app;

        this.gameShop =
                gameShop;
    }

    // =========================================================
    // CUSTOMER STORE
    // =========================================================

    public BorderPane createCustomerStore(
            Customer currentCustomer
    ) {

        this.currentCustomer =
                currentCustomer;

        BorderPane root =
                UIComponent.createMainLayout();

        Label title =
                UIComponent.createLabel(
                        "CUSTOMER STORE",
                        30,
                        UIComponent.GOLD
                );

        Label welcome =
                UIComponent.createLabel(
                        "Welcome, "
                                + currentCustomer.getFullName()
                                + "  •  @"
                                + currentCustomer.getUsername(),
                        15,
                        UIComponent.TEXT_SECONDARY
                );

        Label discountStatus =
                UIComponent.createLabel(
                        currentCustomer.isDiscountAvailable()
                                ? "Trade-in discount available: 10% off your next order"
                                : "No trade-in discount currently available",
                        13,
                        currentCustomer.isDiscountAvailable()
                                ? UIComponent.GOLD
                                : UIComponent.TEXT_SECONDARY
                );

        VBox header =
                new VBox(
                        6,
                        title,
                        welcome,
                        discountStatus
                );

        header.setAlignment(
                Pos.CENTER
        );

        header.setPadding(
                new Insets(
                        0,
                        0,
                        22,
                        0
                )
        );

        root.setTop(
                header
        );

        // =====================================================
        // VIEW SELECTION
        // =====================================================

        ComboBox<storeView> viewBox =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                storeView.values()
                        )
                );

        viewBox.setValue(
                storeView.ALL_GAMES
        );

        UIComponent.styleComboBox(
                viewBox
        );

        // =====================================================
        // SEARCH
        // =====================================================

        TextField searchField =
                UIComponent.createTextField(
                        "Search by title, platform or release year..."
                );

        // =====================================================
        // GAME LIST
        // =====================================================

        ListView<AbstractGame> gameList =
                new ListView<>();

        gameList.setPrefHeight(
                500
        );

        UIComponent.styleGameList(
                gameList
        );

        styleGameList(
                gameList
        );

        // =====================================================
        // GAMES PANEL
        // =====================================================

        VBox gamesPanel =
                UIComponent.createPanel(
                        "AVAILABLE GAMES"
                );

        gamesPanel.getChildren().addAll(
                viewBox,
                searchField,
                gameList
        );

        VBox.setVgrow(
                gameList,
                Priority.ALWAYS
        );

        // =====================================================
        // BASKET PANEL
        // =====================================================

        VBox basketPanel =
                UIComponent.createPanel(
                        "YOUR BASKET"
                );

        basketPanel.setPrefWidth(
                340
        );

        basketPanel.setMinWidth(
                310
        );

        Label basketCount =
                UIComponent.createLabel(
                        "",
                        14,
                        UIComponent.TEXT_SECONDARY
                );

        Label basketSubtotal =
                UIComponent.createLabel(
                        "",
                        15,
                        UIComponent.TEXT_SECONDARY
                );

        Label basketDiscount =
                UIComponent.createLabel(
                        "",
                        14,
                        UIComponent.TEXT_SECONDARY
                );

        Label basketTotal =
                UIComponent.createLabel(
                        "",
                        22,
                        UIComponent.GOLD
                );

        Button addButton =
                UIComponent.createButton(
                        "Add Selected Game"
                );

        Button tradeButton =
                UIComponent.createButton(
                        "Trade In Selected Game"
                );

        Button basketButton =
                UIComponent.createSecondaryButton(
                        "View Basket / Buy"
                );

        Button logoutButton =
                UIComponent.createSecondaryButton(
                        "Customer Logout"
                );

        basketPanel.getChildren().addAll(
                basketCount,
                basketSubtotal,
                basketDiscount,
                basketTotal,
                addButton,
                tradeButton,
                basketButton,
                logoutButton
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        HBox content =
                new HBox(
                        20,
                        gamesPanel,
                        basketPanel
                );

        HBox.setHgrow(
                gamesPanel,
                Priority.ALWAYS
        );

        root.setCenter(
                content
        );

        // =====================================================
        // REFRESH ACTION
        // =====================================================

        Runnable refresh =
                () -> {

                    storeView selectedView =
                            viewBox.getValue();

                    String search =
                            searchField
                                    .getText()
                                    .trim();

                    gameList.setItems(
                            getFilteredGames(
                                    selectedView,
                                    search
                            )
                    );

                    refreshBasketSummary(
                            basketCount,
                            basketSubtotal,
                            basketDiscount,
                            basketTotal
                    );

                    boolean tradeInMode =
                            selectedView
                                    == storeView.TRADE_IN;

                    addButton.setVisible(
                            !tradeInMode
                    );

                    addButton.setManaged(
                            !tradeInMode
                    );

                    tradeButton.setVisible(
                            tradeInMode
                    );

                    tradeButton.setManaged(
                            tradeInMode
                    );
                };

        viewBox.valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                refresh.run()
                );

        searchField.textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                refresh.run()
                );

        // =====================================================
        // ADD GAME
        // =====================================================

        addButton.setOnAction(
                event -> {

                    AbstractGame selectedGame =
                            gameList
                                    .getSelectionModel()
                                    .getSelectedItem();

                    if (selectedGame == null) {

                        UIComponent.showAlert(
                                "Select a game first."
                        );

                        return;
                    }

                    if (!selectedGame.isInStock()) {

                        UIComponent.showAlert(
                                "This game is currently out of stock."
                        );

                        return;
                    }

                    boolean added =
                            currentCustomer.addToBasket(
                                    selectedGame
                            );

                    if (!added) {

                        UIComponent.showAlert(
                                "You cannot add any more of this game. "
                                        + "There is not enough stock available."
                        );

                        return;
                    }

                    refresh.run();
                }
        );

        // =====================================================
        // TRADE IN
        // =====================================================

        tradeButton.setOnAction(
                event -> {

                    AbstractGame selectedGame =
                            gameList
                                    .getSelectionModel()
                                    .getSelectedItem();

                    if (selectedGame == null) {

                        UIComponent.showAlert(
                                "Select a game to trade in."
                        );

                        return;
                    }

                    handleTradeIn(
                            selectedGame
                    );

                    discountStatus.setText(
                            currentCustomer.isDiscountAvailable()
                                    ? "Trade-in discount available: 10% off your next order"
                                    : "No trade-in discount currently available"
                    );

                    discountStatus.setTextFill(
                            Color.web(
                                    currentCustomer.isDiscountAvailable()
                                            ? UIComponent.GOLD
                                            : UIComponent.TEXT_SECONDARY
                            )
                    );

                    refresh.run();
                }
        );

        // =====================================================
        // BASKET
        // =====================================================

        basketButton.setOnAction(
                event ->
                        app.showBasket()
        );

        // =====================================================
        // LOGOUT
        // =====================================================

        logoutButton.setOnAction(
                event ->
                        app.logoutCustomer()
        );

        refresh.run();

        return root;
    }

    // =========================================================
    // FILTER GAMES
    // =========================================================

    private ObservableList<AbstractGame> getFilteredGames(
            storeView selectedView,
            String search
    ) {

        List<AbstractGame> source;

        if (selectedView
                == storeView.RETRO_GAMES) {

            source =
                    gameShop.getRetroGames();

        } else if (selectedView
                == storeView.TRADE_IN) {

            source =
                    gameShop.getTradeInGames();

        } else {

            source =
                    gameShop.getGames();
        }

        ObservableList<AbstractGame> result =
                FXCollections.observableArrayList();

        String query =
                search == null
                        ? ""
                        : search
                        .trim()
                        .toLowerCase();

        for (AbstractGame currentGame : source) {

            if (query.isEmpty()) {

                result.add(
                        currentGame
                );

                continue;
            }

            String platformName =
                    getPlatformDisplayName(
                            currentGame.getPlatform()
                    ).toLowerCase();

            String year =
                    String.valueOf(
                            currentGame.getReleaseYear()
                    );

            String type =
                    getGameTypeDisplayName(
                            currentGame.getGameType()
                    ).toLowerCase();

            if (currentGame
                    .getTitle()
                    .toLowerCase()
                    .contains(query)
                    || platformName.contains(query)
                    || year.contains(query)
                    || type.contains(query)) {

                result.add(
                        currentGame
                );
            }
        }

        return result;
    }

    // =========================================================
    // TRADE IN
    // =========================================================

    private void handleTradeIn(
            AbstractGame selectedGame
    ) {

        if (currentCustomer
                .isDiscountAvailable()) {

            UIComponent.showAlert(
                    "You already have an unused 10% trade-in discount. "
                            + "Use it before trading in another game."
            );

            return;
        }

        if (!selectedGame.isRetro()) {

            UIComponent.showAlert(
                    "Only games released before 2005 can be traded in."
            );

            return;
        }

        if (selectedGame.getStock() >= 10) {

            UIComponent.showAlert(
                    "This game cannot be traded in because the shop "
                            + "already has 10 copies."
            );

            return;
        }

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Trade In Game"
        );

        confirmation.setHeaderText(
                "Trade in "
                        + selectedGame.getTitle()
                        + "?"
        );

        confirmation.setContentText(
                "The shop will add one copy to stock and your account "
                        + "will receive 10% off your next order."
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

        boolean success =
                gameShop.tradeInGame(
                        currentCustomer,
                        selectedGame
                );

        if (!success) {

            UIComponent.showAlert(
                    "The trade-in could not be completed. "
                            + "Refresh the store and try again."
            );

            return;
        }

        UIComponent.showAlert(
                "Trade-in completed. You now have 10% off your next order."
        );
    }

    // =========================================================
    // BASKET SUMMARY
    // =========================================================

    private void refreshBasketSummary(
            Label basketCount,
            Label subtotal,
            Label discount,
            Label total
    ) {

        int itemCount =
                0;

        for (Integer quantity :
                currentCustomer
                        .getBasket()
                        .values()) {

            itemCount +=
                    quantity;
        }

        basketCount.setText(
                itemCount == 1
                        ? "1 item in basket"
                        : itemCount
                        + " items in basket"
        );

        subtotal.setText(
                String.format(
                        "Subtotal: £%.2f",
                        currentCustomer
                                .getBasketSubtotal()
                )
        );

        discount.setText(
                String.format(
                        "Discount: -£%.2f",
                        currentCustomer
                                .getDiscountAmount()
                )
        );

        total.setText(
                String.format(
                        "Total: £%.2f",
                        currentCustomer
                                .getBasketTotal()
                )
        );
    }

    // =========================================================
    // GAME LIST STYLE
    // =========================================================

    private void styleGameList(
            ListView<AbstractGame> list
    ) {

        list.setCellFactory(
                listView ->
                        new ListCell<>() {

                            @Override
                            protected void updateItem(
                                    AbstractGame currentGame,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        currentGame,
                                        empty
                                );

                                setPadding(
                                        new Insets(0)
                                );

                                if (empty
                                        || currentGame == null) {

                                    setText(
                                            null
                                    );

                                    setGraphic(
                                            null
                                    );

                                    return;
                                }

                                setText(
                                        null
                                );

                                setGraphic(
                                        createGameRow(
                                                currentGame,
                                                isSelected()
                                        )
                                );
                            }

                            @Override
                            public void updateSelected(
                                    boolean selected
                            ) {

                                super.updateSelected(
                                        selected
                                );

                                AbstractGame currentGame =
                                        getItem();

                                if (currentGame != null) {

                                    setGraphic(
                                            createGameRow(
                                                    currentGame,
                                                    selected
                                            )
                                    );
                                }
                            }
                        }
        );
    }

    // =========================================================
    // GAME ROW
    // =========================================================

    private HBox createGameRow(
            AbstractGame currentGame,
            boolean selected
    ) {

        String leftText =
                currentGame.getTitle()
                        + "    •    "
                        + getPlatformDisplayName(
                        currentGame.getPlatform()
                )
                        + "    •    "
                        + currentGame.getReleaseYear()
                        + (
                        currentGame.isRetro()
                                ? "    •    RETRO"
                                : ""
                );

        String stockText =
                currentGame.getStock() <= 0
                        ? "OUT OF STOCK"
                        : "Stock: "
                        + currentGame.getStock();

        String rightText =
                "£"
                        + String.format(
                        "%.2f",
                        currentGame.getPrice()
                )
                        + "    •    "
                        + stockText;

        Label leftLabel =
                new Label(
                        leftText
                );

        Label rightLabel =
                new Label(
                        rightText
                );

        Font rowFont =
                Font.font(
                        "Arial",
                        14
                );

        leftLabel.setFont(
                rowFont
        );

        rightLabel.setFont(
                rowFont
        );

        String textColour;

        if (selected) {

            textColour =
                    "#111111";

        } else if (currentGame.getStock() <= 0) {

            textColour =
                    "#D07A7A";

        } else {

            textColour =
                    "#EEEEEE";
        }

        leftLabel.setTextFill(
                Color.web(
                        textColour
                )
        );

        rightLabel.setTextFill(
                Color.web(
                        textColour
                )
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox row =
                new HBox(
                        12,
                        leftLabel,
                        spacer,
                        rightLabel
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        13,
                        18,
                        13,
                        15
                )
        );

        row.setMaxWidth(
                Double.MAX_VALUE
        );

        return row;
    }

    // =========================================================
    // DISPLAY NAMES
    // =========================================================

    private String getPlatformDisplayName(
            Platform selectedPlatform
    ) {

        if (selectedPlatform == null) {

            return "";
        }

        return switch (selectedPlatform) {

            case PC ->
                    "PC";

            case PLAYSTATION ->
                    "PlayStation";

            case XBOX ->
                    "Xbox";

            case NINTENDO_SWITCH ->
                    "Nintendo Switch";
        };
    }

    private String getGameTypeDisplayName(
            GameType selectedType
    ) {

        if (selectedType == null) {

            return "";
        }

        return switch (selectedType) {

            case PC_GAME ->
                    "PC Game";

            case CONSOLE_GAME ->
                    "Console Game";
        };
    }
}