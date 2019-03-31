/**
 * This class creates the buttons that are used to fill the Grid Cells.
 * Some of the abstract methods are not used, but they are necessary
 * when handling click behavoir because selected can be a FillButton of
 * a Cell instance.
 */

package sudoku;

import javafx.scene.paint.Color;

class FillButton extends SudokuButton {
    private final boolean updateable = false;

    /*
     * Always 50 wide and Color.ANTIQUEWHITE
     */
    FillButton(int value) {
        super(String.valueOf(value), 50, Color.ANTIQUEWHITE);
    }

    /*
     * Returns the FillButton to the default border after being unselected
     */
    public void setBlockBorders(int i, int j) {
        updateBorder(DEFAULT_BORDER);
    }

    /*
     * Returns the integer value represented by the button's text. Used to update Cells
     */
    public int getValue() {
        return Integer.parseInt(getText());
    }

    /*
     * getRow() is never necessary for FillButton, but is required with click handlers
     */
    public int getRow() {
        return -1;
    }

    /*
     * getColumn() is never necessary for FillButton, but is required with click handlers
     */
    public int getColumn() {
        return -1;
    }


    /*
     * updateValue() is never necessary for FillButton, but is required with click handlers
     */
    public void updateValue(int value, boolean undo) {
        setText(String.valueOf(value));
    }

    /*
     * getUpdateable() is never necessary for FillButton, but is required with click handlers
     */
    public boolean getUpdateable() {
        return updateable;
    }
}
