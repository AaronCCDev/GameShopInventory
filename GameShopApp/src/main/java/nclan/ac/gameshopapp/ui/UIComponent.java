package nclan.ac.gameshopapp.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class UIComponent {

    private UIComponent() {
    }

    // =========================================================
    // COLOURS
    // =========================================================

    public static final String DARK =
            "#111111";

    public static final String PANEL =
            "#181818";

    public static final String PANEL_LIGHT =
            "#202020";

    public static final String FIELD =
            "#242424";

    public static final String BORDER =
            "#333333";

    public static final String GOLD =
            "#F5DC96";

    public static final String GOLD_HOVER =
            "#FFE9A8";

    public static final String TEXT_SECONDARY =
            "#BDBDBD";

    public static final String DANGER =
            "#D65A5A";

    // =========================================================
    // STYLESHEET
    // =========================================================

    public static void applyStyleSheet(
            Scene scene
    ) {

        if (scene == null) {
            return;
        }

        var styleSheet =
                UIComponent.class.getResource(
                        "/Styles.css"
                );

        if (styleSheet == null) {

            System.out.println(
                    "WARNING: Styles.css could not be found."
            );

            return;
        }

        String styleSheetUrl =
                styleSheet.toExternalForm();

        if (!scene
                .getStylesheets()
                .contains(styleSheetUrl)) {

            scene
                    .getStylesheets()
                    .add(styleSheetUrl);
        }
    }

    // =========================================================
    // MAIN LAYOUT
    // =========================================================

    public static BorderPane createMainLayout() {

        BorderPane root =
                new BorderPane();

        root.setPadding(
                new Insets(28)
        );

        root.getStyleClass().add(
                "main-background"
        );

        return root;
    }

    // =========================================================
    // LOGIN LAYOUT
    // =========================================================

    public static VBox createLoginLayout(
            String titleText,
            String subtitleText
    ) {

        VBox root =
                new VBox(16);

        root.setAlignment(
                Pos.CENTER
        );

        root.setPadding(
                new Insets(60)
        );

        root.getStyleClass().add(
                "main-background"
        );

        Region topSpacer =
                new Region();

        VBox.setVgrow(
                topSpacer,
                Priority.ALWAYS
        );

        Region bottomSpacer =
                new Region();

        VBox.setVgrow(
                bottomSpacer,
                Priority.ALWAYS
        );

        Label title =
                createLabel(
                        titleText,
                        34,
                        GOLD
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        34
                )
        );

        Label subtitle =
                createLabel(
                        subtitleText,
                        15,
                        TEXT_SECONDARY
                );

        subtitle.setWrapText(
                true
        );

        subtitle.setMaxWidth(
                650
        );

        subtitle.setAlignment(
                Pos.CENTER
        );

        root.getChildren().addAll(
                topSpacer,
                title,
                subtitle
        );

        return root;
    }

    // =========================================================
    // FINISH LOGIN LAYOUT
    // =========================================================

    public static void addBottomSpacer(
            VBox root
    ) {

        Region bottomSpacer =
                new Region();

        VBox.setVgrow(
                bottomSpacer,
                Priority.ALWAYS
        );

        root.getChildren().add(
                bottomSpacer
        );
    }

    // =========================================================
    // LABEL
    // =========================================================

    public static Label createLabel(
            String text,
            double size,
            String colour
    ) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Arial",
                        size
                )
        );

        label.setTextFill(
                Color.web(colour)
        );

        return label;
    }

    // =========================================================
    // SMALL HEADING
    // =========================================================

    public static Label createSmallHeading(
            String text
    ) {

        Label label =
                createLabel(
                        text,
                        12,
                        GOLD
                );

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        return label;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    public static Button createButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(
                45
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.getStyleClass().add(
                "primary-button"
        );

        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    public static Button createSecondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(
                45
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.getStyleClass().add(
                "secondary-button"
        );

        return button;
    }

    // =========================================================
    // DANGER BUTTON
    // =========================================================

    public static Button createDangerButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(
                45
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.getStyleClass().add(
                "danger-button"
        );

        return button;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    public static TextField createTextField(
            String prompt
    ) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(
                43
        );

        field.getStyleClass().add(
                "game-text-field"
        );

        return field;
    }

    // =========================================================
    // PASSWORD FIELD
    // =========================================================

    public static PasswordField createPasswordField(
            String prompt
    ) {

        PasswordField field =
                new PasswordField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(
                43
        );

        field.getStyleClass().add(
                "game-text-field"
        );

        return field;
    }

    // =========================================================
    // PANEL
    // =========================================================

    public static VBox createPanel(
            String title
    ) {

        VBox panel =
                new VBox(12);

        panel.setPadding(
                new Insets(20)
        );

        panel.getStyleClass().add(
                "content-panel"
        );

        Label heading =
                createSmallHeading(
                        title
                );

        panel.getChildren().add(
                heading
        );

        return panel;
    }

    // =========================================================
    // COMBO BOX
    // =========================================================

    public static void styleComboBox(
            ComboBox<?> comboBox
    ) {

        comboBox.setPrefHeight(
                43
        );

        comboBox.setMaxWidth(
                Double.MAX_VALUE
        );

        comboBox.getStyleClass().add(
                "game-combo-box"
        );
    }

    // =========================================================
    // GAME LIST
    // =========================================================

    public static void styleGameList(
            ListView<?> listView
    ) {

        listView.getStyleClass().add(
                "game-list"
        );
    }

    // =========================================================
    // BASKET LIST
    // =========================================================

    public static void styleBasketList(
            ListView<?> listView
    ) {

        listView.getStyleClass().add(
                "basket-list"
        );
    }

    // =========================================================
    // ALERT
    // =========================================================

    public static void showAlert(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Game Shop"
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        styleDialog(
                alert
        );

        alert.showAndWait();
    }

    // =========================================================
    // DIALOG STYLE
    // =========================================================

    public static void styleDialog(
            Alert alert
    ) {

        if (alert == null) {
            return;
        }

        alert
                .getDialogPane()
                .getStyleClass()
                .add(
                        "game-dialog"
                );

        var styleSheet =
                UIComponent.class.getResource(
                        "/Styles.css"
                );

        if (styleSheet != null) {

            alert
                    .getDialogPane()
                    .getStylesheets()
                    .add(
                            styleSheet.toExternalForm()
                    );
        }
    }
}