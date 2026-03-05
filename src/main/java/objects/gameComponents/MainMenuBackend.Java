import javafx.stage.Stage;

public class MainMenuBackend extends Application{

    private final Stage primaryStage;

    public MainMenuBackend(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void onLevelSelected(String levelName) {
        System.out.println("Level gestartet: " + levelName);
    }

    public void onSettingsSaved(String settingKey, String settingValue) {
        System.out.println("Einstellung gespeichert: " + settingKey + " = " + settingValue);
    }

    public void onExit() {
        System.out.println("Anwendung wird beendet.");
        primaryStage.close();
    }
}