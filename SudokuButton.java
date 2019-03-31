/**
 * This is an abstract class extending javafx.Buttons with properties that
 * are common to all the types of buttons in this application. There are
 * also several default methods where the implementation is shared by all
 * subclasses of this class.
 */

package sudoku;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public abstract class SudokuButton extends Button {
    public static SudokuButton selected = null; // stores whatever Cell or FillButton is selected for update
    public final Border DEFAULT_BORDER = new Border(new BorderStroke(Color.BLACK, null,
            null, null));
    public final Border SELECTED_BORDER = new Border(new BorderStroke(Color.CYAN, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, BorderStroke.MEDIUM));

    SudokuButton(String label, double width, Color color) {
        super(label); // Uses the javafx.Button constructor to create a button with the provided text
        setMinWidth(width); // Width varies depending on the button and is passed with the constructor
        setMinHeight(50); // Height is shared by all buttons
        setAlignment(Pos.CENTER); // All buttons are centered
        setBorder(DEFAULT_BORDER); // All buttons share the same initial default border
        //Buttons pass their background color as a parameter
        setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    /*
     * The setBlockBorders method is used while selecting a cell. The FillButtons get highlighted when selected
     * and return to default when unselected. The Cell border depends on the row-column pair as the 3x3 boxes
     * are separated by thick borders.
     */
    public abstract void setBlockBorders(int i, int j);

    /*
     * Method that returns the textContent of the button
     */
    public abstract int getValue();

    /*
     * Method that returns the row of the cell in the Grid. -1 for all
     * subclasses other than Cell, but necessary for the EventHandlers
     */
    public abstract int getRow();

    /*
     * Method that returns the column of the cell in the Grid. -1 for all
     * subclasses other than Cell, but necessary for the EventHandlers
     */
    public abstract int getColumn();

    /*
     * Method that changes the contents of the Button in the Grid. Only changes
     * text for all subclasses other than Cell, but necessary for the EventHandlers
     */
    public abstract void updateValue(int value, boolean undo);

    /*
     * Method that returns the isUpdateable property of the button. false for all
     * subclasses other than Cell, but necessary for the EventHandlers
     */
    public abstract boolean getUpdateable();

    /*
     * Updates the border of the button. Implmentation shared by all subclasses
     */
    protected void updateBorder(Border border) {
        setBorder(border);
    }

    /*
     * This method handles the click behavior of the buttons used with filling the game board.
     * Implmentation is shared by all subclasses, but does not affect MenuButton. curButton is
     * the button that got clicked
     */
    protected void buttonClickBehavior(SudokuButton curButton) {
        // If a button is clicked while selected is null, selected is updated if the clicked button
        // is an updateable Cell or a FillButton
        if (selected == null && (curButton instanceof FillButton || curButton.getUpdateable())) {
            // Cannot use updateSelected because selected starts as null
            selected = curButton;
            selected.updateBorder(SELECTED_BORDER);
        }
        // If cur and selected are the same type, change which one is selected
        else if ((selected instanceof FillButton && curButton instanceof FillButton) ||
                (selected instanceof Cell && curButton instanceof Cell && curButton.getUpdateable())) {
            updateSelected(curButton);
        }
        // If selected is a Cell, update its value with current FillButton's value
        else if (selected instanceof Cell && curButton instanceof FillButton) {
            selected.updateValue(curButton.getValue(), false);
            selected.setBlockBorders(selected.getRow(), selected.getColumn());
            selected = null;
        }
        // If selected is a FillButton, update the current Cell with its value
        else if (selected instanceof FillButton && curButton instanceof Cell) {
            curButton.updateValue(selected.getValue(), false);
            selected.setBlockBorders(selected.getRow(), selected.getColumn());
            selected = null;
        }
        // These last two unselect all buttons and resets their borders
    }

    /*
     * This method changes which button is selected and updates both of their borders.
     * Implmentation is shared by all subclasses, but does not affect MenuButton
     */
    protected void updateSelected(SudokuButton newSelected) {
        // Return the unselected cell to its default border
        selected.setBlockBorders(selected.getRow(), selected.getColumn());

        // Change which SudokuButton is selected
        selected = newSelected;

        // Update to the selected border if selected exists
        if (selected != null)
            selected.updateBorder(SELECTED_BORDER);
    }
}

