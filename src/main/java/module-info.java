module com.example.j5 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.j5 to javafx.fxml;
    exports com.example.j5;
}