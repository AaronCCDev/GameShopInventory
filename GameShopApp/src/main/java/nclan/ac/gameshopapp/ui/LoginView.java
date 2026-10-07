package nclan.ac.gameshopapp.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nclan.ac.gameshopapp.app.GameShopApp;
import nclan.ac.gameshopapp.module.Address;
import nclan.ac.gameshopapp.module.Customer;
import nclan.ac.gameshopapp.module.Staff;
import nclan.ac.gameshopapp.service.GameShop;

public class LoginView {
    private final GameShopApp app;
    private final GameShop gameShop;

    public LoginView(GameShopApp app, GameShop gameShop) {
        this.app = app;
        this.gameShop = gameShop;
    }

    public Parent createMainLogin() {
        VBox root = createPage();
        root.setAlignment(Pos.CENTER);
        Label title = UIComponent.createLabel("GAME SHOP", 42, UIComponent.GOLD);
        Label subtitle = UIComponent.createLabel("Choose how you want to continue", 16, UIComponent.TEXT_SECONDARY);
        Button customerButton = UIComponent.createButton("Customer Login");
        Button staffButton = UIComponent.createSecondaryButton("Staff Login");
        Button exitButton = UIComponent.createDangerButton("Exit");
        customerButton.setOnAction(event -> app.showCustomerLogin());
        staffButton.setOnAction(event -> app.showStaffLogin());
        exitButton.setOnAction(event -> app.closeApplication());
        VBox panel = new VBox(15, title, subtitle, customerButton, staffButton, exitButton);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(460);
        root.getChildren().add(panel);
        return root;
    }

    public Parent createCustomerLogin() {
        VBox root = createPage();
        root.setAlignment(Pos.CENTER);
        Label title = UIComponent.createLabel("CUSTOMER LOGIN", 32, UIComponent.GOLD);
        TextField usernameField = UIComponent.createTextField("Username");
        PasswordField passwordField = UIComponent.createPasswordField("Password");
        Button loginButton = UIComponent.createButton("Login");
        Button createButton = UIComponent.createSecondaryButton("Create Account");
        Button backButton = UIComponent.createSecondaryButton("Back");

        loginButton.setOnAction(event -> {
            Customer loggedIn = gameShop.loginCustomer(usernameField.getText(), passwordField.getText());
            if (loggedIn == null) {
                UIComponent.showAlert("Invalid username or password.");
                return;
            }
            app.setCurrentCustomer(loggedIn);
            app.showCustomerStore();
        });
        passwordField.setOnAction(event -> loginButton.fire());
        createButton.setOnAction(event -> app.showCreateCustomerAccount());
        backButton.setOnAction(event -> app.showLogin());

        VBox panel = new VBox(12, title, usernameField, passwordField, loginButton, createButton, backButton);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(460);
        root.getChildren().add(panel);
        return root;
    }

    public Parent createCustomerAccount() {
        VBox root = createPage();
        Label title = UIComponent.createLabel("CREATE ACCOUNT", 32, UIComponent.GOLD);
        Label subtitle = UIComponent.createLabel("Enter your account and delivery address details", 15, UIComponent.TEXT_SECONDARY);
        TextField usernameField = UIComponent.createTextField("Username");
        TextField firstNameField = UIComponent.createTextField("First name");
        TextField lastNameField = UIComponent.createTextField("Last name");
        TextField houseNumberField = UIComponent.createTextField("House number");
        TextField streetField = UIComponent.createTextField("Street");
        TextField cityField = UIComponent.createTextField("City");
        TextField postcodeField = UIComponent.createTextField("Postcode");
        TextField countryField = UIComponent.createTextField("Country");
        PasswordField passwordField = UIComponent.createPasswordField("Password");
        PasswordField confirmPasswordField = UIComponent.createPasswordField("Confirm password");
        Button createButton = UIComponent.createButton("Create Account");
        Button backButton = UIComponent.createSecondaryButton("Back");

        createButton.setOnAction(event -> {
            Address customerAddress = new Address(houseNumberField.getText(), streetField.getText(),
                    cityField.getText(), postcodeField.getText(), countryField.getText());
            if (usernameField.getText().isBlank() || firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
                UIComponent.showAlert("Username, first name and last name are required."); return;
            }
            if (!customerAddress.isComplete()) {
                UIComponent.showAlert("House number, street, city, postcode and country are required."); return;
            }
            if (passwordField.getText().length() < 8) {
                UIComponent.showAlert("Password must contain at least 8 characters."); return;
            }
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                UIComponent.showAlert("Passwords do not match."); return;
            }
            boolean created = gameShop.createCustomerAccount(usernameField.getText(), firstNameField.getText(),
                    lastNameField.getText(), customerAddress, passwordField.getText());
            if (!created) { UIComponent.showAlert("The account could not be created."); return; }
            UIComponent.showAlert("Account created successfully.");
            app.showCustomerLogin();
        });
        backButton.setOnAction(event -> app.showCustomerLogin());

        VBox form = new VBox(12, title, subtitle, usernameField, firstNameField, lastNameField,
                houseNumberField, streetField, cityField, postcodeField, countryField, passwordField, confirmPasswordField, createButton, backButton);
        form.setMaxWidth(500);
        form.setMinWidth(420);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20, 20, 40, 20));

        VBox formWrapper = new VBox(form);
        formWrapper.setAlignment(Pos.TOP_CENTER);
        formWrapper.setPadding(new Insets(10, 20, 20, 20));
        formWrapper.setStyle("-fx-background-color: #111111;");

        ScrollPane scrollPane = new ScrollPane(formWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("control-scroll");
        scrollPane.setStyle(
                "-fx-background: #111111;"
                        + "-fx-background-color: #111111;"
                        + "-fx-border-color: transparent;"
        );

        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        root.getChildren().add(scrollPane);
        return root;
    }

    public Parent createStaffLogin() {
        VBox root = createPage();
        root.setAlignment(Pos.CENTER);
        Label title = UIComponent.createLabel("STAFF LOGIN", 32, UIComponent.GOLD);
        TextField staffIdField = UIComponent.createTextField("Staff ID");
        PasswordField passwordField = UIComponent.createPasswordField("Password");
        Button loginButton = UIComponent.createButton("Login");
        Button backButton = UIComponent.createSecondaryButton("Back");
        loginButton.setOnAction(event -> {
            Staff loggedIn = gameShop.getDatabase().loginStaff(staffIdField.getText(), passwordField.getText());
            if (loggedIn == null) { UIComponent.showAlert("Invalid staff ID or password."); return; }
            app.setCurrentStaff(loggedIn);
            app.showStaffStore();
        });
        passwordField.setOnAction(event -> loginButton.fire());
        backButton.setOnAction(event -> app.showLogin());
        VBox panel = new VBox(12, title, staffIdField, passwordField, loginButton, backButton);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(460);
        root.getChildren().add(panel);
        return root;
    }

    private VBox createPage() {

        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #111111;");

        return root;
    }

}
