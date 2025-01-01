module com.example.tilemapgenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    opens com.example.tilemapgenerator to javafx.fxml;
    exports com.example.tilemapgenerator;
}