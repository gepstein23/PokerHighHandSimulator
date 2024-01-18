package animation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PokerRoomAnimation extends Application {

    private Label tvScreen;
    private HBox pokerTables; // Container for poker tables

    // Existing code...

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Poker Room");

        // Create the TV screen at the top
        tvScreen = new Label("High Hand: -\nMinimum Qualifiers: -");
        tvScreen.setStyle("-fx-background-color: lightgray;");
        tvScreen.setMinHeight(100); // Adjust the height as needed

        // Create the container for poker tables
        pokerTables = new HBox();
        pokerTables.setStyle("-fx-background-color: green;"); // Adjust color as needed

        // Create the main layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(tvScreen);
        borderPane.setCenter(pokerTables); // Add the container for poker tables to the center

        // Create the scene
        Scene scene = new Scene(borderPane, 800, 600); // Adjust the size as needed
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();

        // Example: Add a poker table
        addPokerTable();
    }

    // Existing code...

    // Helper method to add a poker table to the poker room
    private void addPokerTable() {
        // Create a PokerTableAnimation and add it to the container
        PokerTableAnimation pokerTable = new PokerTableAnimation();
        pokerTables.getChildren().add(pokerTable);

        // Set the size (twice the size of the high hand board)
        double pokerTableSize = tvScreen.getMinHeight() * 2;
        pokerTable.setMinSize(pokerTableSize, pokerTableSize);

        // Example: Call updateTvScreen on the poker table
        pokerTable.updateTvScreen("Full House", "Two Pair or Better");
    }

    // Example method to simulate a poker round
    public void simulatePokerRound() {
        // Simulate actions and update the TV screen
        String simulatedHighHand = "Full House";
        String simulatedMinQualifiers = "Two Pair or Better";
        updateTvScreen(simulatedHighHand, simulatedMinQualifiers);

        // Add more poker tables if needed
        addPokerTable();
    }

    // Method to update the TV screen in the PokerRoom
    private void updateTvScreen(String highHand, String minQualifiers) {
        tvScreen.setText("High Hand: " + highHand + "\nMinimum Qualifiers: " + minQualifiers);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
