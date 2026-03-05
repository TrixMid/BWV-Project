import javafx.application.Application;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        new MainMenuUI(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}