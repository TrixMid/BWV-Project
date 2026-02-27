module mein.spiel {
    requires javafx.controls;
    requires javafx.fxml;

    opens dein.package to javafx.graphics, javafx.fxml;

    exports dein.package;
}