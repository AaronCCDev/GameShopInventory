package nclan.ac.gameshopapp.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

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
import nclan.ac.gameshopapp.module.CustomerOrder;
import nclan.ac.gameshopapp.module.AbstractGame;
import nclan.ac.gameshopapp.module.OrderItem;
import nclan.ac.gameshopapp.module.TradeInRecord;
import nclan.ac.gameshopapp.module.Staff;
import nclan.ac.gameshopapp.service.GameShop;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public class StaffView {

    private final GameShopApp app;
    private final GameShop gameShop;

    public StaffView(
            GameShopApp app,
            GameShop gameShop
    ) {

        this.app = app;
        this.gameShop = gameShop;
    }

    public BorderPane createStaffStore(
            Staff currentStaff
    ) {

        BorderPane root =
                UIComponent.createMainLayout();

        Label title =
                UIComponent.createLabel(
                        "STAFF INVENTORY",
                        30,
                        UIComponent.GOLD
                );

        Label loggedIn =
                UIComponent.createLabel(
                        "Logged in as: "
                                + currentStaff.getName()
                                + " ("
                                + currentStaff.getStaffId()
                                + ")  •  "
                                + currentStaff.getRole(),
                        15,
                        UIComponent.TEXT_SECONDARY
                );

        VBox header =
                new VBox(
                        6,
                        title,
                        loggedIn
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

        TextField searchField =
                UIComponent.createTextField(
                        "Search inventory..."
                );

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

        gameList.setItems(
                getFilteredGames("")
        );

        VBox gamesPanel =
                UIComponent.createPanel(
                        "INVENTORY"
                );

        Label gameCount =
                UIComponent.createLabel(
                        gameShop.getGames().size()
                                + " games in inventory",
                        13,
                        UIComponent.TEXT_SECONDARY
                );

        gamesPanel.getChildren().addAll(
                gameCount,
                searchField,
                gameList
        );

        VBox.setVgrow(
                gameList,
                Priority.ALWAYS
        );

        VBox controls =
                UIComponent.createPanel(
                        "ADD / EDIT GAME"
                );

        controls.setPrefWidth(
                390
        );

        controls.setMinWidth(
                350
        );

        ScrollPane controlScroll =
                new ScrollPane(
                        controls
                );

        controlScroll.setFitToWidth(
                true
        );

        controlScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        controlScroll.getStyleClass().add(
                "control-scroll"
        );

        controlScroll.setPrefWidth(
                410
        );

        Label details =
                UIComponent.createLabel(
                        "Select a game to edit, or enter new details.",
                        13,
                        UIComponent.TEXT_SECONDARY
                );

        details.setWrapText(
                true
        );

        Label titleLabel =
                UIComponent.createSmallHeading(
                        "GAME TITLE"
                );

        TextField titleField =
                UIComponent.createTextField(
                        "Game title"
                );

        Label priceLabel =
                UIComponent.createSmallHeading(
                        "PRICE"
                );

        TextField priceField =
                UIComponent.createTextField(
                        "e.g. 49.99"
                );

        Label stockLabel =
                UIComponent.createSmallHeading(
                        "STOCK (0 - 10)"
                );

        TextField stockField =
                UIComponent.createTextField(
                        "e.g. 10"
                );

        Label yearLabel =
                UIComponent.createSmallHeading(
                        "RELEASE YEAR"
                );

        TextField yearField =
                UIComponent.createTextField(
                        "e.g. 2004"
                );

        Label typeLabel =
                UIComponent.createSmallHeading(
                        "GAME TYPE"
                );

        ComboBox<GameType> typeBox =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                GameType.values()
                        )
                );

        typeBox
                .getSelectionModel()
                .selectFirst();

        UIComponent.styleComboBox(
                typeBox
        );

        Label platformLabel =
                UIComponent.createSmallHeading(
                        "PLATFORM"
                );

        ComboBox<Platform> platformBox =
                new ComboBox<>();

        UIComponent.styleComboBox(
                platformBox
        );

        Runnable updatePlatformChoices =
                () -> {

                    GameType selectedType =
                            typeBox.getValue();

                    if (selectedType
                            == GameType.PC_GAME) {

                        platformBox.setItems(
                                FXCollections.observableArrayList(
                                        Platform.PC
                                )
                        );

                        platformBox.setValue(
                                Platform.PC
                        );

                        platformBox.setDisable(
                                true
                        );

                    } else {

                        Platform previousPlatform =
                                platformBox.getValue();

                        platformBox.setItems(
                                FXCollections.observableArrayList(
                                        Platform.PLAYSTATION,
                                        Platform.XBOX,
                                        Platform.NINTENDO_SWITCH
                                )
                        );

                        platformBox.setDisable(
                                false
                        );

                        if (previousPlatform
                                == Platform.PLAYSTATION
                                || previousPlatform
                                == Platform.XBOX
                                || previousPlatform
                                == Platform.NINTENDO_SWITCH) {

                            platformBox.setValue(
                                    previousPlatform
                            );

                        } else {

                            platformBox.setValue(
                                    Platform.PLAYSTATION
                            );
                        }
                    }
                };

        typeBox
                .valueProperty()
                .addListener(
                        (
                                observable,
                                oldType,
                                newType
                        ) ->
                                updatePlatformChoices.run()
                );

        updatePlatformChoices.run();

        Button addButton =
                UIComponent.createButton(
                        "Add Game"
                );

        Button editButton =
                UIComponent.createButton(
                        "Save Changes"
                );

        Button removeButton =
                UIComponent.createDangerButton(
                        "Remove Selected Game"
                );

        Button refreshButton =
                UIComponent.createSecondaryButton(
                        "Refresh Inventory"
                );

        Button logoutButton =
                UIComponent.createSecondaryButton(
                        "Staff Logout"
                );

        gameList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (
                                observable,
                                oldGame,
                                selectedGame
                        ) -> {

                            if (selectedGame == null) {
                                return;
                            }

                            titleField.setText(
                                    selectedGame.getTitle()
                            );

                            priceField.setText(
                                    String.format(
                                            "%.2f",
                                            selectedGame.getPrice()
                                    )
                            );

                            stockField.setText(
                                    String.valueOf(
                                            selectedGame.getStock()
                                    )
                            );

                            yearField.setText(
                                    String.valueOf(
                                            selectedGame.getReleaseYear()
                                    )
                            );

                            typeBox.setValue(
                                    selectedGame.getGameType()
                            );

                            updatePlatformChoices.run();

                            platformBox.setValue(
                                    selectedGame.getPlatform()
                            );
                        }
                );

        searchField
                .textProperty()
                .addListener(
                        (
                                observable,
                                oldValue,
                                newValue
                        ) -> {

                            gameList.setItems(
                                    getFilteredGames(
                                            newValue
                                    )
                            );

                            updateGameCount(
                                    gameCount
                            );
                        }
                );

        // =====================================================
        // ADD GAME
        // =====================================================

        addButton.setOnAction(
                event -> {

                    try {

                        String gameTitle =
                                titleField
                                        .getText()
                                        .trim();

                        double price =
                                Double.parseDouble(
                                        priceField
                                                .getText()
                                                .trim()
                                );

                        int stock =
                                Integer.parseInt(
                                        stockField
                                                .getText()
                                                .trim()
                                );

                        int releaseYear =
                                Integer.parseInt(
                                        yearField
                                                .getText()
                                                .trim()
                                );

                        if (!validateGame(
                                gameTitle,
                                price,
                                stock,
                                releaseYear,
                                typeBox.getValue(),
                                platformBox.getValue()
                        )) {

                            return;
                        }

                        AbstractGame newGame =
                                gameShop.addGame(
                                        gameTitle,
                                        price,
                                        stock,
                                        platformBox.getValue(),
                                        typeBox.getValue(),
                                        releaseYear
                                );

                        if (newGame == null) {

                            UIComponent.showAlert(
                                    "The game could not be added. "
                                            + "Check that it is not already stored "
                                            + "for the selected platform."
                            );

                            return;
                        }

                        refreshGameList(
                                gameList,
                                searchField.getText()
                        );

                        updateGameCount(
                                gameCount
                        );

                        clearFields(
                                titleField,
                                priceField,
                                stockField,
                                yearField
                        );

                        gameList
                                .getSelectionModel()
                                .clearSelection();

                        typeBox
                                .getSelectionModel()
                                .selectFirst();

                        updatePlatformChoices.run();

                        UIComponent.showAlert(
                                "Game added successfully."
                        );

                    } catch (NumberFormatException exception) {

                        UIComponent.showAlert(
                                "Price must be a number. "
                                        + "Stock and release year must be whole numbers."
                        );
                    }
                }
        );

        editButton.setOnAction(
                event -> {

                    AbstractGame selectedGame =
                            gameList
                                    .getSelectionModel()
                                    .getSelectedItem();

                    if (selectedGame == null) {

                        UIComponent.showAlert(
                                "Select a game to edit."
                        );

                        return;
                    }

                    try {

                        String gameTitle =
                                titleField
                                        .getText()
                                        .trim();

                        double price =
                                Double.parseDouble(
                                        priceField
                                                .getText()
                                                .trim()
                                );

                        int stock =
                                Integer.parseInt(
                                        stockField
                                                .getText()
                                                .trim()
                                );

                        int releaseYear =
                                Integer.parseInt(
                                        yearField
                                                .getText()
                                                .trim()
                                );

                        GameType newType =
                                typeBox.getValue();

                        Platform newPlatform =
                                platformBox.getValue();

                        if (!validateGame(
                                gameTitle,
                                price,
                                stock,
                                releaseYear,
                                newType,
                                newPlatform
                        )) {

                            return;
                        }

                        if (newType
                                != selectedGame.getGameType()) {

                            UIComponent.showAlert(
                                    "Game type cannot be changed while editing. "
                                            + "Remove the game and add it again under "
                                            + "the new game type."
                            );

                            typeBox.setValue(
                                    selectedGame.getGameType()
                            );

                            updatePlatformChoices.run();

                            platformBox.setValue(
                                    selectedGame.getPlatform()
                            );

                            return;
                        }

                        String oldTitle =
                                selectedGame.getTitle();

                        double oldPrice =
                                selectedGame.getPrice();

                        int oldStock =
                                selectedGame.getStock();

                        Platform oldPlatform =
                                selectedGame.getPlatform();

                        int oldReleaseYear =
                                selectedGame.getReleaseYear();

                        selectedGame.setTitle(
                                gameTitle
                        );

                        selectedGame.setPrice(
                                price
                        );

                        selectedGame.setStock(
                                stock
                        );

                        selectedGame.setPlatform(
                                newPlatform
                        );

                        selectedGame.setReleaseYear(
                                releaseYear
                        );

                        boolean updated =
                                gameShop.updateGame(
                                        selectedGame
                                );

                        if (!updated) {

                            selectedGame.setTitle(
                                    oldTitle
                            );

                            selectedGame.setPrice(
                                    oldPrice
                            );

                            selectedGame.setStock(
                                    oldStock
                            );

                            selectedGame.setPlatform(
                                    oldPlatform
                            );

                            selectedGame.setReleaseYear(
                                    oldReleaseYear
                            );

                            gameShop.loadGames();

                            refreshGameList(
                                    gameList,
                                    searchField.getText()
                            );

                            UIComponent.showAlert(
                                    "The game could not be updated in the database."
                            );

                            return;
                        }

                        refreshGameList(
                                gameList,
                                searchField.getText()
                        );

                        updateGameCount(
                                gameCount
                        );

                        clearFields(
                                titleField,
                                priceField,
                                stockField,
                                yearField
                        );

                        gameList
                                .getSelectionModel()
                                .clearSelection();

                        typeBox
                                .getSelectionModel()
                                .selectFirst();

                        updatePlatformChoices.run();

                        UIComponent.showAlert(
                                "Game updated successfully."
                        );

                    } catch (NumberFormatException exception) {

                        UIComponent.showAlert(
                                "Price must be a number. "
                                        + "Stock and release year must be whole numbers."
                        );
                    }
                }
        );

        removeButton.setOnAction(
                event -> {

                    AbstractGame selectedGame =
                            gameList
                                    .getSelectionModel()
                                    .getSelectedItem();

                    if (selectedGame == null) {

                        UIComponent.showAlert(
                                "Select a game to remove."
                        );

                        return;
                    }

                    Alert confirmation =
                            new Alert(
                                    Alert.AlertType.CONFIRMATION
                            );

                    confirmation.setTitle(
                            "Remove Game"
                    );

                    confirmation.setHeaderText(
                            "Remove "
                                    + selectedGame.getTitle()
                                    + "?"
                    );

                    confirmation.setContentText(
                            "This will permanently remove the game from the database."
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

                    boolean removed =
                            gameShop.removeGame(
                                    selectedGame
                            );

                    if (!removed) {

                        UIComponent.showAlert(
                                "The game could not be removed from the database."
                        );

                        return;
                    }

                    refreshGameList(
                            gameList,
                            searchField.getText()
                    );

                    updateGameCount(
                            gameCount
                    );

                    clearFields(
                            titleField,
                            priceField,
                            stockField,
                            yearField
                    );

                    gameList
                            .getSelectionModel()
                            .clearSelection();

                    typeBox
                            .getSelectionModel()
                            .selectFirst();

                    updatePlatformChoices.run();

                    UIComponent.showAlert(
                            "Game removed successfully."
                    );
                }
        );

        refreshButton.setOnAction(
                event -> {

                    gameShop.loadGames();

                    refreshGameList(
                            gameList,
                            searchField.getText()
                    );

                    updateGameCount(
                            gameCount
                    );

                    clearFields(
                            titleField,
                            priceField,
                            stockField,
                            yearField
                    );

                    gameList
                            .getSelectionModel()
                            .clearSelection();

                    typeBox
                            .getSelectionModel()
                            .selectFirst();

                    updatePlatformChoices.run();
                }
        );

        logoutButton.setOnAction(
                event ->
                        app.logoutStaff()
        );

        controls.getChildren().addAll(
                details,

                titleLabel,
                titleField,

                priceLabel,
                priceField,

                stockLabel,
                stockField,

                yearLabel,
                yearField,

                typeLabel,
                typeBox,

                platformLabel,
                platformBox,

                addButton,
                editButton,
                removeButton,
                refreshButton,
                logoutButton
        );

        HBox content =
                new HBox(
                        20,
                        gamesPanel,
                        controlScroll
                );

        content.getStyleClass().add(
                "staff-inventory-content"
        );

        HBox.setHgrow(
                gamesPanel,
                Priority.ALWAYS
        );

        Tab inventoryTab = new Tab("Inventory", content);
        inventoryTab.setClosable(false);

        Tab customersTab = new Tab(
                "Customers",
                createCustomerManagement()
        );
        customersTab.setClosable(false);

        TabPane tabPane = new TabPane(
                inventoryTab,
                customersTab
        );

        tabPane.getStyleClass().add(
                "staff-tab-pane"
        );

        root.setCenter(tabPane);

        return root;
    }

    private VBox createCustomerManagement() {

        VBox root = new VBox(18);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("customer-management");

        VBox customerPanel =
                UIComponent.createPanel("CUSTOMER ACCOUNTS");

        TextField searchField =
                UIComponent.createTextField("Search customers...");

        Button searchButton =
                UIComponent.createButton("Search");

        HBox searchBox = new HBox(10, searchField, searchButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        ListView<Customer> customerList = new ListView<>();
        customerList.setPrefHeight(220);
        customerList.getStyleClass().add("customer-list");

        customerPanel.getChildren().addAll(searchBox, customerList);

        VBox detailsPanel =
                UIComponent.createPanel("CUSTOMER DETAILS");

        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(7);
        detailsArea.getStyleClass().add("customer-details");

        detailsPanel.getChildren().add(detailsArea);

        VBox ordersPanel =
                UIComponent.createPanel("ORDER HISTORY");

        ListView<CustomerOrder> orderList = new ListView<>();
        orderList.setPrefHeight(170);
        orderList.getStyleClass().add("customer-list");

        ordersPanel.getChildren().add(orderList);

        VBox itemsPanel =
                UIComponent.createPanel("SELECTED ORDER ITEMS");

        ListView<OrderItem> itemList = new ListView<>();
        itemList.setPrefHeight(150);
        itemList.getStyleClass().add("customer-list");

        itemsPanel.getChildren().add(itemList);

        VBox tradeInPanel =
                UIComponent.createPanel("TRADE-IN HISTORY");

        ListView<TradeInRecord> tradeInList = new ListView<>();
        tradeInList.setPrefHeight(150);
        tradeInList.getStyleClass().add("customer-list");

        tradeInPanel.getChildren().add(tradeInList);

        Runnable runSearch = () -> {

            List<Customer> results =
                    gameShop.searchCustomers(searchField.getText());

            customerList.setItems(
                    FXCollections.observableArrayList(results)
            );

            detailsArea.clear();
            orderList.getItems().clear();
            itemList.getItems().clear();
            tradeInList.getItems().clear();
        };

        searchButton.setOnAction(event -> runSearch.run());
        searchField.setOnAction(event -> runSearch.run());

        customerList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldCustomer, selectedCustomer) -> {

                    if (selectedCustomer == null) {
                        return;
                    }

                    detailsArea.setText(
                            "Customer ID: " + selectedCustomer.getId()
                                    + "\nUsername: " + selectedCustomer.getUsername()
                                    + "\nName: " + selectedCustomer.getFullName()
                                    + "\n\nHouse number: " + selectedCustomer.getAddress().getHouseNumber()
                                    + "\nStreet: " + selectedCustomer.getAddress().getStreet()
                                    + "\nCity: " + selectedCustomer.getAddress().getCity()
                                    + "\nPostcode: " + selectedCustomer.getAddress().getPostcode()
                                    + "\nCountry: " + selectedCustomer.getAddress().getCountry()
                                    + "\n\nFull address: " + selectedCustomer.getFullAddress()
                    );

                    orderList.setItems(
                            FXCollections.observableArrayList(
                                    gameShop.getCustomerOrders(selectedCustomer.getId())
                            )
                    );

                    tradeInList.setItems(
                            FXCollections.observableArrayList(
                                    gameShop.getCustomerTradeIns(selectedCustomer.getId())
                            )
                    );

                    itemList.getItems().clear();
                });

        orderList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldOrder, selectedOrder) -> {

                    if (selectedOrder == null) {
                        itemList.getItems().clear();
                        return;
                    }

                    itemList.setItems(
                            FXCollections.observableArrayList(
                                    gameShop.getOrderItems(selectedOrder.getOrderId())
                            )
                    );
                });

        root.getChildren().addAll(
                customerPanel,
                detailsPanel,
                ordersPanel,
                itemsPanel,
                tradeInPanel
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("customer-scroll");

        VBox wrapper = new VBox(scroll);
        wrapper.getStyleClass().add("customer-management");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        runSearch.run();

        return wrapper;
    }

    private ObservableList<AbstractGame> getFilteredGames(
            String search
    ) {

        ObservableList<AbstractGame> result =
                FXCollections.observableArrayList();

        String query =
                search == null
                        ? ""
                        : search
                        .trim()
                        .toLowerCase();

        for (AbstractGame currentGame :
                gameShop.getGames()) {

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

            String gameTypeName =
                    getGameTypeDisplayName(
                            currentGame.getGameType()
                    ).toLowerCase();

            String releaseYear =
                    String.valueOf(
                            currentGame.getReleaseYear()
                    );

            if (currentGame
                    .getTitle()
                    .toLowerCase()
                    .contains(query)
                    || platformName.contains(query)
                    || gameTypeName.contains(query)
                    || releaseYear.contains(query)) {

                result.add(
                        currentGame
                );
            }
        }

        return result;
    }

    private void refreshGameList(
            ListView<AbstractGame> gameList,
            String search
    ) {

        gameList.setItems(
                getFilteredGames(
                        search
                )
        );
    }

    private void updateGameCount(
            Label gameCount
    ) {

        int count =
                gameShop
                        .getGames()
                        .size();

        gameCount.setText(
                count
                        + (
                        count == 1
                                ? " game in inventory"
                                : " games in inventory"
                )
        );
    }

    private boolean validateGame(
            String title,
            double price,
            int stock,
            int releaseYear,
            GameType selectedGameType,
            Platform selectedPlatform
    ) {

        if (title == null
                || title.isBlank()) {

            UIComponent.showAlert(
                    "Enter a game title."
            );

            return false;
        }

        if (price < 0) {

            UIComponent.showAlert(
                    "Price cannot be negative."
            );

            return false;
        }

        if (stock < 0
                || stock > 10) {

            UIComponent.showAlert(
                    "Stock must be between 0 and 10."
            );

            return false;
        }

        int currentYear =
                Year.now()
                        .getValue();

        if (releaseYear < 1950
                || releaseYear > currentYear + 1) {

            UIComponent.showAlert(
                    "Enter a valid release year."
            );

            return false;
        }

        if (selectedGameType == null) {

            UIComponent.showAlert(
                    "Select a game type."
            );

            return false;
        }

        if (selectedPlatform == null) {

            UIComponent.showAlert(
                    "Select a platform."
            );

            return false;
        }

        if (selectedGameType
                == GameType.PC_GAME
                && selectedPlatform
                != Platform.PC) {

            UIComponent.showAlert(
                    "PC games must use the PC platform."
            );

            return false;
        }

        if (selectedGameType
                == GameType.CONSOLE_GAME
                && selectedPlatform
                == Platform.PC) {

            UIComponent.showAlert(
                    "Console games cannot use the PC platform."
            );

            return false;
        }

        return true;
    }

    private void clearFields(
            TextField titleField,
            TextField priceField,
            TextField stockField,
            TextField yearField
    ) {

        titleField.clear();
        priceField.clear();
        stockField.clear();
        yearField.clear();

        titleField.requestFocus();
    }

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

                                    setText(null);
                                    setGraphic(null);
                                    return;
                                }

                                setText(null);

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

                                super.updateSelected(selected);

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
                new Label(leftText);

        Label rightLabel =
                new Label(rightText);

        Font rowFont =
                Font.font(
                        "Arial",
                        14
                );

        leftLabel.setFont(rowFont);
        rightLabel.setFont(rowFont);

        String textColour;

        if (selected) {

            textColour = "#FFFFFF";

        } else if (currentGame.getStock() <= 0) {

            textColour = "#D07A7A";

        } else {

            textColour = "#EEEEEE";
        }

        leftLabel.setTextFill(
                Color.web(textColour)
        );

        rightLabel.setTextFill(
                Color.web(textColour)
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
            GameType selectedGameType
    ) {

        if (selectedGameType == null) {

            return "";
        }

        return switch (selectedGameType) {

            case PC_GAME ->
                    "PC Game";

            case CONSOLE_GAME ->
                    "Console Game";
        };
    }
}