module com.example.finalprojectjavaapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires json.simple;


    opens com.example.finalprojectjavaapi to javafx.fxml;
    exports com.example.finalprojectjavaapi;
}