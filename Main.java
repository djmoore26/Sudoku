/**
 * This class is a driver class that is only used to start the
 * applicaton, overriding the start() method from javafx.Application.
 */

package sudoku;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sudoku");

        SceneController controller = new SceneController(primaryStage);
        primaryStage.setScene(controller.getMainMenu());
        primaryStage.show();
    }
}
