import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mein Gaming Projekt 2026");

        Button startButton = new Button("START GAME");
        Button settingsButton = new Button("SETTINGS");
        Button exitButton = new Button("EXIT");
    
        String buttonStyle = "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-min-width: 150px; -fx-cursor: hand;";
        startButton.setStyle(buttonStyle);
        settingsButton.setStyle(buttonStyle + "-fx-background-color: #3498db;"); 
        exitButton.setStyle(buttonStyle + "-fx-background-color: #e74c3c;");     

        startButton.setOnAction(event -> {

            VBox gameLayout = new VBox(20);
            gameLayout.setAlignment(Pos.CENTER);
            gameLayout.setStyle("-fx-background-color: #2c3e50;"); 
            
            Label gameLabel = new Label("Das Spiel läuft jetzt!");
            gameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
            
            Button backButton = new Button("ZURÜCK ZUM MENÜ");
            backButton.setOnAction(e -> start(primaryStage)); 
            
            gameLayout.getChildren().addAll(gameLabel, backButton);
            
            Scene gameScene = new Scene(gameLayout, 800, 600);
            primaryStage.setScene(gameScene);
        });

        settingsButton.setOnAction(event -> System.out.println("Settings werden geladen..."));

        exitButton.setOnAction(event -> primaryStage.close());

        VBox menuLayout = new VBox(15); // 15 Pixel Abstand zwischen den Elementen
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: #ecf0f1;"); // Hellgrauer Hintergrund
        menuLayout.getChildren().addAll(startButton, settingsButton, exitButton);

        Scene menuScene = new Scene(menuLayout, 400, 350);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public static void Main(String[] args) {
        launch(args);
    }
}