import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenuUI {

    private final Stage primaryStage;
    private final MainMenuBackend backend;

    public MainMenuUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.backend = new MainMenuBackend(primaryStage);
    }

    public void show() {
        primaryStage.setTitle("Mein Gaming Projekt 2026");

        Button levelButton    = createStyledButton("LEVEL AUSWAHL", "#ffffff");
        Button settingsButton = createStyledButton("SETTINGS",       "#ffffff");
        Button exitButton     = createStyledButton("EXIT",           "#ffffff");

        levelButton.setOnAction(event    -> showLevelSelectionWindow());
        settingsButton.setOnAction(event -> showSettingsWindow());
        exitButton.setOnAction(event     -> backend.onExit());

        VBox menuLayout = new VBox(15);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 40px;");
        menuLayout.getChildren().addAll(levelButton, settingsButton, exitButton);

        Scene menuScene = new Scene(menuLayout, 400, 350);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    private void showLevelSelectionWindow() {
        Stage levelStage = new Stage();
        levelStage.setTitle("Level Auswahl");
        levelStage.initModality(Modality.APPLICATION_MODAL);
        levelStage.initOwner(primaryStage);

        Label title = new Label("Wähle dein Level");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("Wähle einen Schwierigkeitsgrad aus:");
        subtitle.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 13px;");

        String[][] levels = {
            {"Level 1", "Anfaenger – Einfache Gegner, kein Zeitlimit",   "#27ae60"},
            {"Level 2", "Mittel – Schnellere Gegner, Zeitlimit aktiv",   "#e67e22"},
            {"Level 3", "Fortgeschritten – Maximale Schwierigkeit",      "#c0392b"}
        };

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 35px;");
        layout.getChildren().addAll(title, subtitle);

        for (String[] level : levels) {
            String levelName  = level[0];
            String levelDesc  = level[1];
            String levelColor = level[2];

            Label nameLabel = new Label(levelName);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

            Label descLabel = new Label(levelDesc);
            descLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11px;");

            VBox levelInfo = new VBox(2, nameLabel, descLabel);
            levelInfo.setAlignment(Pos.CENTER_LEFT);

            Button selectBtn = new Button("SPIELEN");
            selectBtn.setStyle(
                "-fx-background-color: " + levelColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-min-width: 90px;"
            );
            selectBtn.setOnAction(e -> {
                backend.onLevelSelected(levelName);
                levelStage.close();
            });

            javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(20, levelInfo, selectBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                "-fx-background-color: #34495e;" +
                "-fx-padding: 12px 16px;" +
                "-fx-background-radius: 6px;" +
                "-fx-min-width: 360px;"
            );
            javafx.scene.layout.HBox.setHgrow(levelInfo, javafx.scene.layout.Priority.ALWAYS);

            layout.getChildren().add(row);
        }

        Button closeBtn = createStyledButton("ZURUECK", "#7f8c8d");
        closeBtn.setOnAction(e -> levelStage.close());
        layout.getChildren().add(closeBtn);

        levelStage.setScene(new Scene(layout, 450, 400));
        levelStage.show();
    }

    private void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Einstellungen");
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.initOwner(primaryStage);

        Label title = new Label("Einstellungen");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label placeholder = new Label("Hier kommen deine Settings hin...");
        placeholder.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 13px;");

        Button closeBtn = createStyledButton("ZURÜCK", "#7f8c8d");
        closeBtn.setOnAction(e -> settingsStage.close());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50; -fx-padding: 30px;");
        layout.getChildren().addAll(title, placeholder, closeBtn);

        settingsStage.setScene(new Scene(layout, 400, 300));
        settingsStage.show();
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-min-width: 200px;" +
            "-fx-cursor: hand;"
        );
        return btn;
    }
}