/**
 * This method contains everything required to show different
 * scenes on the primaryStage.
 */

package sudoku;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class SceneController {
    private Stage primaryStage;
    private Board board;
    private SolverController solverController;
    private HintController hintController;
    private SudokuPopUp popup;

    SceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.centerOnScreen();
        board = new Board();
        popup = new SudokuPopUp(primaryStage, this);
    }

    /*
     * Creates the scene containing the Main Menu
     */
    public Scene getMainMenu() {
        GridPane root = getBackgroundGridPane(10,50);

        ImageView logo = createImageView("sudoku-logo.jpg", 400, true);

        MenuButton player = new MenuButton("Play", 150, e -> primaryStage.setScene(getPlayMenu()));
        MenuButton solver = new MenuButton("Solver", 150, e -> primaryStage.setScene(getSolveMenu()));
        MenuButton help = new MenuButton("Help", 150, e -> primaryStage.setScene(getHelpMenu()));

        // Creates HBox with the Bottom contents
        HBox buttons = new HBox(20, player, solver, help);

        root.add(logo, 0, 0);
        root.add(buttons, 0, 1);

        // Center everything in the scene
        root.setHalignment(logo, HPos.CENTER);
        root.setHalignment(buttons, HPos.CENTER);

        Scene mainScene = new Scene(root, 800, 500, Color.BLACK);
        return mainScene;
    }

    /*
     * Creates the scene containing the Menu for choosing which Play mode
     */
    private Scene getPlayMenu() {
        FlowPane root = getBackgroundFlowPane();

        // Create all of the game mode selector buttons
        MenuButton easy = new MenuButton("Easy", 150, e -> newGameClick(1));
        MenuButton medium = new MenuButton("Medium", 150, e -> newGameClick(2));
        MenuButton hard = new MenuButton("Hard", 150, e -> newGameClick(3));
        MenuButton random = new MenuButton("Random", 150, e -> newGameClick(Board.getRandomDifficulty()));
        MenuButton back = new MenuButton("Back", 150, e -> primaryStage.setScene(getMainMenu()));

        // Creates VBox with the right side contents
        VBox options = new VBox(10, board.getTitleField("Play"), easy, medium, hard, random, back);
        options.setAlignment(Pos.CENTER);

        root.getChildren().add(board.getBoard(0, false));
        root.getChildren().add(options);

        Scene playerScene = new Scene(root, 800, 500, Color.BLACK);
        return playerScene;
    }

    /*
     * Creates the scene containing the Solver Scene
     */
    public Scene getSolveMenu() {
        FlowPane root = getBackgroundFlowPane();

        // Adds the board to the scene
        root.getChildren().add(board.getBoard(0, true));
        root.setMargin(board, new Insets(-12)); // Places the board in the correct spot
        // SolverController constructed here because it is only used with this menu
        solverController = new SolverController(board, this);

        // Creates VBox with the right side contents
        VBox options = new VBox(10, board.getTitleField("Solver"), getFillButtons(), getSolveButtons());
        options.setAlignment(Pos.CENTER);
        root.setMargin(options, new Insets(-12)); // Places the board in the correct spot
        root.getChildren().add(options);

        Scene solveScene = new Scene(root, 800, 500, Color.BLACK);
        // Adds the KeyEvent handlers to the scene rather than the Cells
        solveScene.setOnKeyPressed(e -> board.undoRedoController.handleKeyPressed(e));
        solveScene.setOnKeyReleased(e -> Cell.handleKeyEvent(e, board));

        return solveScene;
    }

    /*
     * Creates the scene containing the Help Scene
     */
    public Scene getHelpMenu() {
        // The How-To paragraph is stored in a single String to display
        final String howTo = "Sudoku (Japanese meaning number place) is the name given to a popular puzzle concept. Its origin" +
                " is unclear, but credit must be attributed to Leonhard Euler who invented a similar, and much more difficult," +
                " puzzle idea called Latin Squares. The objective of Su Doku puzzles, however, is to replace the blanks " +
                "(or zeros) in a 9 by 9 grid in such that each row, column, and 3 by 3 box contains each of the digits 1 to 9." +
                " \n\nThis is an example of a typical completed puzzle grid.. A well constructed Sudoku " +
                "puzzle has a unique solution and can be solved by logic, although it may be necessary to employ \"guess and" +
                " test\" methods in order to eliminate options (there is much contested opinion over this). The complexity of" +
                " the search determines the difficulty of the puzzle; this example is considered easy because it can be" +
                " solved by straight forward direct deduction. \n\nSource: https://projecteuler.net/problem=96";

        FlowPane root = getBackgroundFlowPane();

        ImageView completedBoard = createImageView("completed-board.jpg", 350, false);
        MenuButton back = new MenuButton("Back", 150, e -> primaryStage.setScene(getMainMenu()));

        VBox leftSide = new VBox(20, completedBoard, back);
        leftSide.setAlignment(Pos.CENTER);

        root.getChildren().add(leftSide);
        root.setMargin(leftSide, new Insets(-12)); // Places the board in the correct spot

        // Creates the displayable How-To Text paragraph
        Text help = new Text(howTo);
        help.setFont(Font.font("Verdana", FontWeight.MEDIUM, 14.5));
        help.setWrappingWidth(350);
        root.setMargin(help, new Insets(-12)); // Places the board in the correct spot
        root.getChildren().add(help);

        Scene helpScene = new Scene(root, 800, 500, Color.BLACK);

        return helpScene;
    }

    /*
     * Creates the scene containing the Player Board Scene
     */
    public Scene getBoardMenu(int difficulty) {
        FlowPane root = getBackgroundFlowPane();

        // Adds the board to the scene
        root.getChildren().add(board.getBoard(difficulty, false));
        root.setMargin(board, new Insets(-12)); // Places the board in the correct spot
        // HintController constructed here because it is only used with this menu
        hintController = new HintController(board, this);

        // Creates VBox with the right side contents
        VBox options = new VBox(10, board.getTitleField(), getFillButtons(), getPlayButtons());
        options.setAlignment(Pos.CENTER);
        root.getChildren().add(options);
        root.setMargin(options, new Insets(-12)); // Places the board in the correct spot

        Scene playScene = new Scene(root, 800, 500, Color.BLACK);
        // Adds the KeyEvent handlers to the scene rather than the Cells
        playScene.setOnKeyPressed(e -> board.undoRedoController.handleKeyPressed(e));
        playScene.setOnKeyReleased(e -> Cell.handleKeyEvent(e, board));

        return playScene;
    }

    /*
     * Creates and formats a background GridPane for reuse
     */
    public static GridPane getBackgroundGridPane(int vGap, int hGap) {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setVgap(vGap);
        root.setHgap(hGap);
        root.setBackground(new Background(new BackgroundFill(Color.SANDYBROWN, null, null)));

        return root;
    }

    /*
     * Creates and formats a background FlowPane for reuse
     */
    private FlowPane getBackgroundFlowPane() {
        FlowPane root = new FlowPane(Orientation.HORIZONTAL);
        root.setAlignment(Pos.CENTER);
        root.setVgap(10);
        root.setHgap(50);
        root.setBackground(new Background(new BackgroundFill(Color.SANDYBROWN, null, null)));
        return root;
    }

    /*
     * Creates the Fill buttons and formats them in a GridPane
     */
    private GridPane getFillButtons() {
        GridPane buttons = getBackgroundGridPane(5,5);

        int row = 0, col = 0;

        // Creates all the Fill buttons and adds their handlers
        for (int i = 1; i <= 9; i++) {
            FillButton button = new FillButton(i);
            button.setOnMousePressed(e -> button.buttonClickBehavior(button));
            button.setOnDragDetected(e -> button.startFullDrag());
            buttons.add(button, col, row);

            // If-else block to increment boxRow and boxColumn properly to check all 8
            // values in the same box
            if (col % 3 == 2) {
                col -= 2;
                row++;
            }
            else
                col++;
        }

        return buttons;
    }

    /*
     * Creates the Menu buttons and that assist in play mode
     */
    private GridPane getPlayButtons() {
        GridPane playButtons = getBackgroundGridPane(5,5);
        popup = new SudokuPopUp(primaryStage, this, true);

        MenuButton undo = new MenuButton("Undo", 55, e -> board.undoRedoController.undo());
        playButtons.add(undo, 0, 0);

        MenuButton redo = new MenuButton("Redo", 55, e -> board.undoRedoController.redo());
        playButtons.add(redo, 1, 0);

        MenuButton hint = new MenuButton("Hint", 55, e -> hintController.handleClick());
        hintController.installHintTooltip(hint);
        playButtons.add(hint, 2, 0);

        MenuButton done = new MenuButton("Done", 175, e -> {if (board.checkComplete(false)) popup.showPopup();});
        playButtons.add(done, 0, 1, 3, 1);

        MenuButton restart = new MenuButton("Restart", 55, e -> primaryStage.setScene(getBoardMenu(-1)));
        playButtons.add(restart, 0, 2);

        MenuButton newGame = new MenuButton("New", 55, e -> newGameClick(board.getDifficulty()));
        playButtons.add(newGame, 1, 2);

        MenuButton back = new MenuButton("Back", 55, e -> primaryStage.setScene(getPlayMenu()));
        playButtons.add(back, 2, 2);

        return playButtons;
    }

    /*
     * Creates the Menu buttons and that assist in solve mode
     */
    private GridPane getSolveButtons() {
        GridPane solveButtons = getBackgroundGridPane(5,5);
        popup = new SudokuPopUp(primaryStage, this, false);

        MenuButton undo = new MenuButton("Undo", 85, e -> board.undoRedoController.undo());
        solveButtons.add(undo, 0, 0);

        MenuButton redo = new MenuButton("Redo",85, e -> board.undoRedoController.redo());
        solveButtons.add(redo, 1, 0);

        MenuButton solve = new MenuButton("Solve!", 175, e -> solverController.handleClick());
        solveButtons.add(solve, 0,1, 2, 1);

        MenuButton restart = new MenuButton("Start Over", 85, e -> primaryStage.setScene(getSolveMenu()));
        solveButtons.add(restart, 0,2);

        MenuButton back = new MenuButton("Back", 85, e -> primaryStage.setScene(getMainMenu()));
        solveButtons.add(back, 1,2);

        return solveButtons;
    }

    /*
     * Returns the current popup object attached with the current scene
     */
    public SudokuPopUp getPopUp() {
        return popup;
    }

    /*
     * Handler for the various places a new game can be created from
     * Shows an error message if applicable
     */
    private void newGameClick(int difficulty) {
        board.setDifficulty(difficulty); // Required for clicking Random
        // If no boards of the selected difficulty are available, show an error popup
        if (board.getAttemptedBoardNumbersSize(difficulty) == 0){
            popup.setPopup(new SudokuPopUp(primaryStage, this, false).getPopup());
            popup.showPopup();
        }
        // Else show the new board
        else
            primaryStage.setScene(getBoardMenu(difficulty));
    }

    /*
     * Reused method to create and format an Image object for displaying
     * Adds a tooltip if applicable
     */
    private ImageView createImageView(String fileName, int dimensions, boolean tooltip) {
        // Create the Image object for displaying
        ImageView logo = new ImageView(new Image(new File(fileName).toURI().toString()));
        logo.setFitHeight(dimensions);
        logo.setFitWidth(dimensions);

        // The Main Menu logo has a tooltip showing the source URL
        if (tooltip) {
            Tooltip logoSource = new Tooltip("Source: http://miyabiweb.info/sudoku-logo/" +
                    "sudoku-logo-sudoku-followers-for-instagram-templates/");
            logoSource.setShowDelay(Duration.seconds(1.75));
            logoSource.setHideDelay(Duration.seconds(0));
            logoSource.setFont(Font.font("Verdana", 10));

            Tooltip.install(logo, logoSource);
        }

        return logo;
    }
}