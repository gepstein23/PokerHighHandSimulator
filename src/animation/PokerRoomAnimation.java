package animation;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class PokerRoomAnimation extends Application {

    // Constants
    private static final int BACKGROUND_WIDTH = 800;
    private static final int BACKGROUND_HEIGHT = 600;
    private static final int TV_SCREEN_HEIGHT = 50;
    private static final int TV_SCREEN_WIDTH = 80;
    private static final int TV_SCREEN_OFFSET_Y = 6;
    private static final int GRID_MARGIN = 10;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Poker Room");

        // Create the main pane for the poker room background
        StackPane pokerRoomBackground = createPokerRoomBackground();

        // Create the TV screen at the top
        StackPane tvScreenPane = createTvScreenPane();
        StackPane.setAlignment(tvScreenPane, Pos.TOP_CENTER);
        StackPane.setMargin(tvScreenPane, new Insets(TV_SCREEN_OFFSET_Y, 0, 0, 0));

        // Create the grid of poker tables
        GridPane pokerTableGrid = createPokerTableGrid();

        // Create the layout and add components
        StackPane root = new StackPane();
        root.getChildren().addAll(pokerRoomBackground, tvScreenPane, pokerTableGrid);

        // Create the scene
        Scene scene = new Scene(root, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    private StackPane createPokerRoomBackground() {
        StackPane pokerRoomBackground = new StackPane();
        pokerRoomBackground.setStyle("-fx-background-color: black;");
        return pokerRoomBackground;
    }

    private StackPane createTvScreenPane() {
        StackPane tvScreenPane = new StackPane();

        // Create the TV screen
        Rectangle tvScreen = new Rectangle(TV_SCREEN_WIDTH, TV_SCREEN_HEIGHT);
        tvScreen.setFill(Color.LIGHTGRAY);
        tvScreen.setStroke(Color.DARKGRAY);
        tvScreen.setStrokeWidth(3);

        // Add the TV screen to the pane
        tvScreenPane.getChildren().add(tvScreen);

        return tvScreenPane;
    }

    private GridPane createPokerTableGrid() {
        GridPane grid = new GridPane();
        int numRows = 4; // Set the number of rows
        int numCols = 4; // Set the number of columns
        int circleMargin = 15;

        double circleSize = Math.min((BACKGROUND_WIDTH - (numCols - 1) * GRID_MARGIN) / numCols,
                (BACKGROUND_HEIGHT - TV_SCREEN_HEIGHT - TV_SCREEN_OFFSET_Y - (numRows - 1) * GRID_MARGIN) / numRows);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // Create an oval table with dynamically calculated size
                Ellipse table = new Ellipse(circleSize / 2.4 , circleSize / 3.2);
                table.setFill(Color.GREEN);
                table.setStroke(Color.BLACK);
                table.setStrokeWidth(2);

                // Add the table to the grid with margin
                grid.add(table, col, row);
                GridPane.setMargin(table, new Insets(GRID_MARGIN));

                // Add seats around the table
                addSeatsToTable(grid, col, row, circleSize / 2, 8);
            }
        }

        // Overlay the grid on the pokerRoomBackground
        grid.setMouseTransparent(true);

        return grid;
    }

    private void addSeatsToTable(GridPane grid, int col, int row, double tableSize, int numSeats) {
        double seatSize = tableSize / 8;
        double centerX = col * (tableSize + GRID_MARGIN) + tableSize / 2 + GRID_MARGIN;
        double centerY = row * (tableSize + GRID_MARGIN) + tableSize / 2 + GRID_MARGIN;

        for (int i = 0; i < numSeats; i++) {
            double angle = 2 * Math.PI * i / numSeats;
            double seatX = centerX + tableSize / 2 * Math.cos(angle);
            double seatY = centerY + tableSize / 2 * Math.sin(angle);

            // Create a dark grey seat with a darker grey outline
            Circle seat = new Circle(seatX, seatY, seatSize / 2, Color.DARKGRAY);
            seat.setStroke(Color.BLACK);
            seat.setStrokeWidth(1);
            seat.setStrokeType(StrokeType.OUTSIDE);

            grid.getChildren().add(seat);
        }
    }
}
