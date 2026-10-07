package nclan.ac.gameshopapp.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import nclan.ac.gameshopapp.module.Customer;
import nclan.ac.gameshopapp.module.Staff;
import nclan.ac.gameshopapp.service.GameShop;
import nclan.ac.gameshopapp.ui.BasketView;
import nclan.ac.gameshopapp.ui.CustomerView;
import nclan.ac.gameshopapp.ui.LoginView;
import nclan.ac.gameshopapp.ui.StaffView;
import nclan.ac.gameshopapp.ui.UIComponent;

public class GameShopApp extends Application {

    private Stage stage;
    private final GameShop gameShop =
            new GameShop();

    private Customer currentCustomer;
    private Staff currentStaff;
    private LoginView loginView;
    private CustomerView customerView;
    private BasketView basketView;
    private StaffView staffView;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Game Shop");
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        initialiseViews();
        showLogin();
        stage.show();
    }

    private void initialiseViews() {
        loginView = new LoginView(this, gameShop);
        customerView = new CustomerView(this, gameShop);
        basketView = new BasketView(this, gameShop);
        staffView = new StaffView(this, gameShop);
    }

    public void changeScene(Parent root)
    {
        Scene currentScene = stage.getScene();
        if (currentScene == null) {Scene scene = new Scene(root,
                            1000,
                            700);

            UIComponent.applyStyleSheet(scene);
            stage.setScene(scene);

        } else {
            currentScene.setRoot(root);
            UIComponent.applyStyleSheet(currentScene);
        }
    }

    public void showLogin() {
        changeScene(loginView.createMainLogin());
    }

    public void showCustomerLogin() {
        changeScene(loginView.createCustomerLogin());
    }

    public void showCreateCustomerAccount() {
        changeScene(loginView.createCustomerAccount());
    }

    public void showStaffLogin() {
        changeScene(loginView.createStaffLogin());
    }

    public void showCustomerStore() {
        if (currentCustomer == null) {showLogin();
            return;
        }
        changeScene(customerView.createCustomerStore(currentCustomer));
    }

    public void showBasket() {
        if (currentCustomer == null) {showLogin();return;
        }

        changeScene(basketView.createBasket(currentCustomer));
    }

    public void showStaffStore() {
        if (currentStaff == null) {showLogin();
            return;
        }
        changeScene(staffView.createStaffStore(currentStaff));
    }

    public void setCurrentCustomer(Customer currentCustomer)
    {
        this.currentCustomer = currentCustomer;
    }

    public void logoutCustomer() {
        if (currentCustomer != null) {
            boolean loggedOut = gameShop.logoutCustomer(currentCustomer);

            if (!loggedOut) {UIComponent.showAlert(
                        "The server could not confirm the logout. " + "The local session will still be closed.");
            }
        }
        currentCustomer = null;showLogin();
    }

    public void setCurrentStaff(Staff currentStaff) {
        this.currentStaff = currentStaff;
    }

    public void logoutStaff() {
        gameShop.getDatabase().logoutStaff();
        currentStaff = null;showLogin();
    }

    public void closeApplication() {
        stage.close();
    }
}