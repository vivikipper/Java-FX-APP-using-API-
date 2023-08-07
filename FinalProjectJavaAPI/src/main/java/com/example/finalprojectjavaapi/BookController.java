    package com.example.finalprojectjavaapi;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.ListView;
    import javafx.scene.control.TextField;
    import java.io.IOException;
    import java.net.URI;
    import java.net.http.HttpClient;
    import java.net.http.HttpRequest;
    import java.net.http.HttpResponse;
    import java.net.http.HttpResponse.BodyHandlers;
    import java.util.ArrayList;
    import java.util.List;
    import javafx.scene.image.Image;
    import javafx.stage.Stage;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;

    public class BookController {
        @FXML
        public Label clickToseeDetails;
        @FXML
        private Label bookName;
        @FXML
        private Button searchButton;
        @FXML
        private ListView<String> listView;
        @FXML
        private TextField bookInput;

        @FXML
        public void search(ActionEvent actionEvent) {
            String userInput = bookInput.getText().trim(); // get user input and removes all blank spaces in the beginning and in the final of the input.
            String escapedInput = userInput.replace(" ", "%20"); // Replace spaces for %20
            String url = "https://openlibrary.org/search.json?title=" + escapedInput; //Building a complete URL for the API calling

            HttpClient httpClient = HttpClient.newHttpClient(); // Doing a request.
            HttpRequest request = HttpRequest.newBuilder() // create a constructor for the request HTTP
                    .uri(URI.create(url)) //uri = uniform resource identifier
                    .build();

            try { // make the solicitation and receive the response
                HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
                int statusCode = response.statusCode();

                if (statusCode == 200) { // getting the status code from HTTP. The code 200 indicates the request was successful
                    String responseBody = response.body(); // getting and analysing information in JSON format.
                    JSONParser parser = new JSONParser();
                    JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
                    JSONArray results = (JSONArray) jsonResponse.get("docs");

                    List<String> bookTitles = new ArrayList<>();
                    for (int i = 0; i < results.size(); i++) { // iterating JSON results
                        JSONObject book = (JSONObject) results.get(i); // getting JSON object.
                        String title = (String) book.get("title"); // Accessing the key title to get the book title.
                        bookTitles.add(title); // adding to the list all title returned from API.
                    }

                    // Populating ListView with book names
                    listView.getItems().clear();//clear the list before adding new items
                    listView.getItems().addAll(bookTitles); //adding all list items from API

                }
            } catch (Exception e) { // handling exception
                e.printStackTrace();
            }
        }
        private void handleSelectedItem() { // It is called always the user select a title
            String selectedTitle = listView.getSelectionModel().getSelectedItem(); // return the selected item
            if (selectedTitle != null) { // if the selected title is not null, then return the other view
                openSecondView(selectedTitle);
            }
        }
        @FXML
        private void initialize() { // with initialize will be called automatically in JavaFX
            //  listener for item selection
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) { // when a new item is selected, will be executed the method.
                    handleSelectedItem();
                }
            });
        }
        @FXML
        private void openSecondView(String selectedTitle) { //Loading the second view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("secondBook-view.fxml"));
                Parent root = loader.load();
                SecondBookViewController detailsController = loader.getController();
                detailsController.setBookTitle(selectedTitle);
                Stage stage = new Stage();
                Scene scene = new Scene(root, 700, 700);
                String css = this.getClass().getResource("BookAplication.css").toExternalForm();
                scene.getStylesheets().add(css); // adding css stylesheet to the project
                stage.setScene(scene);
                stage.getIcons().add(new Image("Books.png")); //adding icon
                stage.setTitle("Book researcher!");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
