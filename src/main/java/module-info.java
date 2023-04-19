module com.example.algorithms2assignment1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.algorithms2assignment1 to javafx.fxml;
    exports com.example.algorithms2assignment1;
    exports resources;
}