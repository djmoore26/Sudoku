/**
 * This class creates all buttons that do not form the Board Grid and that
 * are not used to fill the Grid Cells. Most of the abstract methods are
 * not used, but it is a type of Button used with this package so it still
 * extends from the parent SudokuButton class.
 */

package sudoku;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MenuButton extends SudokuButton{
    private final boolean updateable = false;

    /*
     * Uses the SudokuButton constructor, but also adds the MouseClicked handler.
     * All have variable widths but the same Color.KHAKI
     */
    MenuButton(String label, double width, EventHandler<? super MouseEvent> expression) {
        super(label, width, Color.KHAKI);
        setOnMouseClicked(expression);
    }

    /*
     * MenuButtons can only have the default border
     */
    public void setBlockBorders(int i, int j) {
        updateBorder(DEFAULT_BORDER);
    }

    /*
     * getValue() is never necessary for MenuButton
     */
    public int getValue() {
        return -1;
    }

    /*
     * getRow() is never necessary for MenuButton
     */
    public int getRow() {
        return -1;
    }

    /*
     * getColumn() is never necessary for MenuButton
     */
    public int getColumn() {
        return -1;
    }

    /*
     * updateValue() is never necessary for MenuButton
     */
    public void updateValue(int value, boolean undo) {
        setText(String.valueOf(value));
    }

    /*
     * getUpdateable() is never necessary for MenuButton
     */
    public boolean getUpdateable() {
        return updateable;
    }
}
