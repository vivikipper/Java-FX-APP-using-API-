package com.example.finalprojectjavaapi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;


public class SecondBookViewController {

    @FXML
    private TextArea description;
    @FXML
    private Label title;
    @FXML
    private Label author;

    @FXML
    private ImageView coverBook;
    @FXML
    private Button backToFirstView;

    @FXML
    private String selectedTitle;

    @FXML
    public void setBookTitle(String title) {
        selectedTitle = title;
        fetchBookDetails();
    }

    @FXML
    private void fetchBookDetails() {
        // Making a new API call using book title.
        String escapedTitle = selectedTitle.replace(" ", "%20"); //replacing blank space using %20 convention.
        String url = "https://openlibrary.org/search.json?title=" + escapedTitle; //Building a complete URL for the API calling

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                String responseBody = response.body();
                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
                JSONArray results = (JSONArray) jsonResponse.get("docs");

                if (!results.isEmpty()) {
                    JSONObject book = (JSONObject) results.get(0); // Get the first book from the list
                    JSONArray authorsArray = (JSONArray) book.get("author_name");
                    String title = (String) book.get("title");
                    // Fetch complete book details with description
                    JSONObject completeBookDetails = fetchBookDetailsDescription((String) book.get("key"));
                    String description = (String) completeBookDetails.get("description");


                    // The authors in the API are stored in an Array
                    //then this block of code is going through each element in author array.
                    StringBuilder authorsStringBuilder = new StringBuilder();
                    if (authorsArray != null) {
                        for (int i = 0; i < authorsArray.size(); i++) {
                            String authorName = (String) authorsArray.get(i); // get the author name
                            if (i > 0) {
                                authorsStringBuilder.append(", "); // if more than one author append this.
                            }
                            authorsStringBuilder.append(authorName); // add author's name
                        }
                    }
                    String authorsString = authorsStringBuilder.toString(); // convert stringBuilder in one string separated by coma

                    // Update with the obtained information
                    this.title.setText("Title:   " + title);
                    this.author.setText("Author:   " + authorsString);
                    this.description.setText("Description:\n" + description );
                    System.out.println(description);
                    // Fetch the cover edition key
                    String coverEditionKey = (String) book.get("cover_edition_key");

                    if (coverEditionKey != null) {
                        // Constructing the cover image URL
                        String coverImageUrl = "https://covers.openlibrary.org/b/olid/" + coverEditionKey + "-L.jpg";
                        // Update the cover image
                        Image coverImage = new Image(coverImageUrl);
                        coverBook.setImage(coverImage);

                    } else {
                        // If no cover image available
                        coverBook.setImage(null);
                    }
                } else {
                    // If no book details found
                    this.title.setText("Title:   Not Available");
                    this.author.setText("Author:   Not Available");
                    this.description.setText("Description: Not Available");
                    coverBook.setImage(null);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML // calling again API for fetching book description.
    private JSONObject fetchBookDetailsDescription(String workKey) throws Exception {
        String url = "https://openlibrary.org" + workKey + ".json";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode == 200) {
            String responseBody = response.body();
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(responseBody);
            return jsonResponse;
        } else {
            throw new Exception("Failed to fetch book details");
        }
    }
    @FXML
    public void backView(ActionEvent actionEvent) {
        // when the back button is clicked, close this window and go to the first window.
        Stage stage = (Stage) backToFirstView.getScene().getWindow();
        stage.close();
    }
}