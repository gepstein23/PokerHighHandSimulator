package animation;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class PokerTableAnimation extends Pane {

    private Rectangle tableBackground;
    private Text tableLabel;

    public PokerTableAnimation() {
        // Customize the appearance of the poker table
        tableBackground = new Rectangle(200, 150); // Set the size as needed
        tableBackground.setFill(Color.GREEN); // Adjust color as needed

        tableLabel = new Text("Poker Table");
        tableLabel.setStyle("-fx-font-size: 16;");

        // Add components to the pane
        getChildren().addAll(tableBackground, tableLabel);

        // Set the initial position or other properties as needed
    }

    // Add additional methods or logic for the poker table as needed

    // Example method to update the TV screen within the poker table
    public void updateTvScreen(String highHand, String minQualifiers) {
        // Implement logic to update the TV screen within the poker table
        // For example, update a label or display relevant information
        tableLabel.setText("High Hand: " + highHand + "\nMin Qualifiers: " + minQualifiers);
    }
}
