package com.example.finalprojectjavaapi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class BookApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BookApplication.class.getResource("book-view.fxml"));//loading fxml file
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);// setting the scene size
        scene.getStylesheets().add(getClass().getResource("BookAplication.css").toExternalForm());// adding css external file
        stage.getIcons().add(new Image("Books.png")); // adding icon to the menu bar
        stage.setTitle("Book Researcher");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}