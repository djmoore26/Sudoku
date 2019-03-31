/**
 * This class represents a Controller to handle popup messages. The popups
 * utilize a second stage with Application Modality, meaning ActionEvents
 * cannot occur outside of the popup, forcing the user to click one of the
 * available buttons. There are three types of popups: congratulations for
 * solving a board, Solution not Found for a solver TimeoutException, and
 * all boards done for clicking a new game difficulty when all 20 boards
 * have been opened in this session.
 */

package sudoku;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SudokuPopUp {
    private String messageText; // Text on the popup
    private SceneController controller;
    private Stage primaryStage; // Owner stage
    private Stage popup; // Popup stage
    private BorderPane root;
    private SudokuPopUp newPopup;


    /*
     * Constructor without the boolean is used internally
     */
    SudokuPopUp(Stage primaryStage, SceneController controller) {
        this.controller = controller;
        this.primaryStage = primaryStage;
    }

    SudokuPopUp(Stage primaryStage, SceneController controller, boolean valid) {
        this.controller = controller;
        this.primaryStage = primaryStage;
        createPopUp(valid);
    }

    /*
     * Method separate from the constructor that creates and populates the Popup Window
     */
    public void createPopUp(boolean valid) {
        popup = new Stage(StageStyle.UNDECORATED); // removes the Window border (including close X button)
        popup.centerOnScreen();
        popup.initModality(Modality.APPLICATION_MODAL); // Disables ActionEvents outside of the popup
        popup.initOwner(primaryStage);

        // Set the background and border properties of the scene
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.SANDYBROWN, null, null)));
        root.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,new CornerRadii(3), BorderStroke.THIN)));

        // Sets the message on the popup using a boolean selector
        setMessageText(!valid);

        // Uses a boolean to choose from two different sets of buttons
        // Padding is space around the button box to put them in the right place (T, R, B, L)
        HBox buttons;
        if (valid) {
            buttons = congratulationsButtons();
            buttons.setPadding(new Insets(0,40,25,40));
        }
        else {
            buttons = solverButtons();
            buttons.setPadding(new Insets(0,95,25,95));
        }

        root.setBottom(buttons);

        Scene scene = new Scene(root,400,200, Color.SANDYBROWN);

        // Close the popup if the escape key is clicked
        scene.setOnKeyReleased(e -> {if (e.getCode() == KeyCode.ESCAPE) this.closeScene();});

        // Sets the created popup to the stage, but only shows it when clicked
        popup.setScene(scene);
    }

    /*
     * When the valid boolean is true, the congratulations popup/buttons are used
     */
    private HBox congratulationsButtons() {
        // Button to return to the top main menu
        MenuButton mainMenu = new MenuButton("Main Menu", 100, e -> this.closeScene());

        // Button to get a new game in the same difficulty (if possible)
        MenuButton newGame = new MenuButton("New Game", 100, e -> {
            // If all 20 boards in this difficulty have been touched, a new error popup is created and shown
            if (Board.getAttemptedBoardNumbersSize(Board.getDifficulty()) == 0) {
                popup.close();
                newPopup = new SudokuPopUp(primaryStage, controller, false);
                newPopup.showPopup();
            }
            // Otherwise close the popup and show the new game board
            else {
                primaryStage.setScene(controller.getBoardMenu(Board.getDifficulty()));
                this.popup.close();
            }
        });

        // Button to close the popup and view the game board
        MenuButton review = new MenuButton("Continue", 100, e -> this.popup.close());

        HBox buttons = new HBox(10, mainMenu, newGame, review);

        return buttons;
    }

    /*
     * Internal method to set default titles associated with the Play Menu popups
     * If all boards in the difficulty, the title is "[difficulty] boards done"
     * Otherwise the board is a valid solution and this title is Congratulations
     */
    private void setMessageText(boolean error) {
        if (error) {
            switch (Board.getDifficulty()) {
                case 1:
                    messageText = "Easy";
                    break;
                case 2:
                    messageText = "Medium";
                    break;
                case 3:
                    messageText = "Hard";
                    break;
            }
            messageText += " boards done";
        }
        else
            messageText = "Congratulations!";

        Text message = new Text(messageText);
        message.setFont(Font.font ("Verdana", 36));
        root.setCenter(message);
    }


    /*
     * Uses a provided String as the title of the popup rather than using a default
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;

        Text message = new Text(messageText);
        message.setFont(Font.font ("Verdana", 36));
        root.setCenter(message);
    }

    /*
     * When the valid boolean is false, the solver-error popup/buttons are used
     */
    private HBox solverButtons() {
        // Button to return to the top main menu
        MenuButton mainMenu = new MenuButton("Main Menu", 100, e -> this.closeScene());

        // Button to close the popup and review the current screen
        MenuButton review = new MenuButton("Continue", 100, e -> this.popup.close());

        HBox buttons = new HBox(10, mainMenu, review);

        return buttons;
    }

    /*
     * Separate close method used with the Main Menu buttons and the escape key event
     * Sets the primaryStage scene to the main menu and closes the popup
     */
    private void closeScene(){
        primaryStage.setScene(controller.getMainMenu());
        this.popup.close();
    }

    /*
     * Part of the handleClick pipeline - this displays the popup windows
     */
    public void showPopup() {
        popup.show();
    }

    /*
     * Returns the popup stage/window. It is used to modify the popup elsewhere
     */
    public Stage getPopup() {
        return popup;
    }

    /*
     * Sets a new scene/stage to the existing popup window (independent of shownProperty)
     */
    public void setPopup(Stage popup) {
        this.popup = popup;
    }
}
