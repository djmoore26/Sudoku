/**
 * This class creates all buttons that form the Board Grid. All of the SudokuButton
 * abstract methods have specific implementations used with updating the value and
 * handling the click behavior. Cell additionally has many other methods that are
 * associated with play, including KeyEvent handlers. Cells used with play have
 * Tooltips that show hints and other get/set methods associated with that and with play.
 * Cells used with the solver do not have Tooltips but have other data fields and get/set
 * methods that are used with the solver algorithm.
 */

package sudoku;

import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.ArrayList;

public class Cell extends SudokuButton {
    private final Background INVALID_BACKGROUND = new Background(new BackgroundFill(Color.rgb(255,150,150), null, null));
    private final Background DEFAULT_BACKGROUND = new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null));
    private final boolean solver; // Indicates if the Cell is being used with the player or the solver
    private boolean solverValid; // If solver, this indicates if the current filled value is valid
    private boolean updateable; // Indicates if the Cell's contents may be changed
    private int curValue; // Stores the current int value of the Cell
    private int row;
    private int column;
    private Tooltip hoverHint; // Stores the Tooltip associated with the cell
    private Board board; // Link to the board that owns it
    public ArrayList<Integer> possible; // To use with hover and solver

    Cell(int curValue, int row, int column, boolean solver, Board board) {
        super("",50, Color.ANTIQUEWHITE); // Initializes with an empty cell

        this.curValue = curValue;
        this.row = row;
        this.column = column;
        this.solver = solver;
        this.board = board;
        solverValid = false; // Set to false; only changed by the solve algorithm

        if (board.getDifficulty() == 0 && curValue == 0 && !solver)
            updateable = false; // Empty play menu board is set to false so it can't be clicked and no tooltip
        else if (solver || curValue == 0)
            updateable = true;
        else if (curValue != 0) {
            updateable = false;
            setTextProperties(String.valueOf(curValue), Color.BLACK); // Text is only set if non-zero
        }

        resetPossible(); // Initializes possible with all 9 values
        hoverHint = new Tooltip(getTooltipText()); // Creates the Tooltip
        setUpTooltip(); // Sets up the Tooltip associated with this cell
    }

    /*
     * Implementation specific to Cells. The borders in rows 2, 3, 5, 6 and columns
     * 2, 3, 5, 6 are all thickened in order to separate the 3x3 boxes of the board
     */
    public void setBlockBorders(int i, int j) {
        BorderStroke stroke;
        if ((i == 2 && j == 2) || (i == 2 && j == 5) || (i == 5 && j == 2) || (i == 5 & j == 5))
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY, new BorderWidths(0,2,2,0), null);

        else if ((i == 2 && j == 3) || (i == 2 && j == 6) || (i == 5 && j == 3) || (i == 5 & j == 6))
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0,0,2,2), null);

        else if ((i == 3 && j == 2) || (i == 3 && j == 5) || (i == 6 && j == 2) || (i == 6 & j == 5))
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY, new BorderWidths(2, 2,0,0), null);

        else if ((i == 3 && j == 3) || (i == 3 && j == 6) || (i == 6 && j == 3) || (i == 6 & j == 6))
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(2,0,0,2), null);
        else if (i == 2 || i == 5)
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY, new BorderWidths(0,0,2,0), null);

        else if (i == 3 || i == 6)
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY, new BorderWidths(2,0,0,0), null);

        else if (j == 2 || j == 5)
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY, new BorderWidths(0,2,0,0), null);

        else if (j == 3 || j == 6)
            stroke = new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                    BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0,0,0,2), null);

        else
            stroke = null;

        updateBorder(new Border(stroke));
    }

    /*
     * Returns the row of this Cell on the Board
     */
    public int getRow() {
        return row;
    }

    /*
     * Returns the column of this Cell on the Board
     */
    public int getColumn() {
        return column;
    }

    /*
     * Returns the integer value of this Cell's contents
     */
    public int getValue() {
        return curValue;
    }

    /*
     * Cells are the only updateable buttons. This implementation changes the
     * contents of this Cell and updates associated values
     */
    public void updateValue(int value, boolean undo) {
        // Only updates if allowed and if it is a new value
        if (updateable && value != curValue) {
            // if UndoRedoController is not calling this method, add the update to the undoStack
            if (!undo)
                board.undoRedoController.addUndoAction(this);

            curValue = value;
            setValue(curValue, false);

            // Checks the board for being solved to update possible values and backgrounds
            board.checkSolution(solver);
         }
    }

    /*
     * This method changes the visual properties of the contents of the Cell. It
     * is called when updating the value, but chooses the correct color based on
     * what type of Cell it is and where in the pipeline the set occurs
     */
    public void setValue(int value, boolean solved) {
        // Used while filling the solver board
        if (solver && !solved && value > 0)
            setTextProperties(String.valueOf(value), Color.BLACK);
        // Used when solved or while filling the play board
        else if ((solved || !solver) && value > 0)
            setTextProperties(String.valueOf(value), Color.rgb(0,153,0));
        // If the value to set is 0, it sets an empty String (color is arbitrary)
        else
            setTextProperties("",Color.rgb(0,153,0));
    }

    /*
     * This method is called from setValue to actually do the set action
     * with the determined value and text color
     */
    private void setTextProperties(String text, Color color) {
        setText(text);
        setTextFill(color);
        setFont(Font.font("Verdana", FontWeight.BLACK, 12));
    }

    /*
     * This method updates the background depending on certain booleans.
     * The solver highlights invalid populated squares. The player
     * highlights invalid and empty squares only after done has been checked
     */
    public void updateBackground(boolean valid) {
        if (valid && getBackground() != DEFAULT_BACKGROUND)
            setBackground(DEFAULT_BACKGROUND);
        else if (!valid && (getBackground() != INVALID_BACKGROUND || solver))
            setBackground(INVALID_BACKGROUND);
    }

    /*
     * Get method for the private updateable property
     */
    public boolean getUpdateable() {
        return updateable;
    }

    /*
     * Set method for the private updateable property
     */
    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }

    /*
     * Get method for the private solverValid property. Used with the solve
     * algorithm to touch or not touch squares during a recursive iteration
     */
    public boolean getSolverValid() {
        return solverValid;
    }

    /*
     * Set method for the private solverValid propertty
     */
    public void setSolverValid(boolean solverValid) {
        this.solverValid = solverValid;
    }

    /*
     * This method uses an ActionEvent, dragReleased, to update a Cell's value.
     * One of the ways to fill the Board Grid is by dragging the FillButton
     * to the desired Cell and releasing it.
     */
    public void onDragReleased() {
        if (selected != null) {
            updateValue(selected.getValue(), false);
            selected.setBlockBorders(selected.getRow(), selected.getColumn());
            selected = null;
        }
    }

    /*
     * This method contains all of the possible valid KeyEvents that
     * have a result. The handler applies to Cells which is why it is
     * listed here, but the listener is attached to the Stage. Key
     * Events do not reset selected to null like click events do
     */
    public static void handleKeyEvent(KeyEvent e, Board board) {
        // Checks if control is held, then calls the UndoRedo handler
        if (board.undoRedoController.controlDown) {
            board.undoRedoController.handleKeyReleased(e);
            // TODO ADD ctrl+H to get a hint 
            //if (e.getCode() == KeyCode.H && board.undoRedoController.controlDown)
        }
        // The rest of the methods affect only Cells
        else if (selected instanceof Cell) {
            int newRow = selected.getRow(), newColumn = selected.getColumn();
            KeyCode code = e.getCode();
            e.consume();

            // Move selected up one Cell (if possible)
            if (code == KeyCode.UP) {
                if (newRow > 0)
                    newRow--;
            }
            // Move selected down one Cell (if possible)
            else if (code == KeyCode.DOWN) {
                if (newRow < 8)
                    newRow++;
            }
            // Move selected left one Cell (if possible)
            else if (code == KeyCode.LEFT) {
                if (newColumn > 0)
                    newColumn--;
            }
            // Move selected right one Cell (if possible)
            else if (code == KeyCode.RIGHT) {
                if (newColumn < 8)
                    newColumn++;
            }
            // If backspace or delete is typed, it will clear the Cell value
            else if (selected.getUpdateable() && (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE)) {
                selected.updateValue(0, false);
                return;
            }

            // If a digit is pressed, it will update the Cell with that digit
            else if (selected.getUpdateable()) {
                String codeString = code.toString();
                // Digit KeyCodes of the form DIGIT#, so we need char 5 and unicode value between 48-57
                if (codeString.length() > 5 && codeString.charAt(5) <= 57 && codeString.charAt(5) >= 48) {
                    selected.updateValue(codeString.charAt(5) - 48, false);
                }
                return;
            }
            selected.updateSelected(board.getCurrentBoard()[newRow][newColumn]);
        }
    }

    /*
     *
     */
    private void setUpTooltip() {
        hoverHint.setText(getTooltipText());
        hoverHint.setShowDelay(Duration.seconds(1.75));
        hoverHint.setHideDelay(Duration.seconds(0));
        hoverHint.setFont(Font.font("Verdana", 10));

        if (!solver && updateable)
            setTooltip(hoverHint);
    }

    /*
     * This method updates the Tooltip message to the current possible values
     */
    public void updateTooltip() {
        if (!solver && updateable)
            hoverHint.setText(getTooltipText());
    }

    /*
     * Builds a string of the current possible values to be displayed
     */
    private String getTooltipText() {
        String s = "Possible: ";

        for (int i= 0; i < possible.size(); i++) {
            s += possible.get(i);

            if (i < possible.size()-1)
                s += ", ";
        }
        return s;
    }

    /*
     * Resets possible to default (all 9 values) to be removed correctly
     */
    public void resetPossible() {
        possible = new ArrayList<>();

        for (int i = 1; i <=9; i++) {
            possible.add(Integer.valueOf(i));
        }
    }
}
