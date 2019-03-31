/**
 * This class models a controller that is used
 * to control the ability to undo and redo actions
 * while playing a board or populating the solver.
 */

package sudoku;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;

public class UndoRedoController {
    public boolean controlDown = false; // public because it is used by Cell.handleKeyEvent
    private ArrayList<Cell> undoActionStack;
    private ArrayList<Integer> undoValueStack;
    private ArrayList<Cell> redoActionStack;
    private ArrayList<Integer> redoValueStack;
    // value of lastIndex stored to prevent IndexOutOfBounds exception
    private int lastUndoIndex, lastRedoIndex;

    UndoRedoController() {
        undoActionStack = new ArrayList<>();
        undoValueStack = new ArrayList<>();
        lastUndoIndex = -1;

        // setRedoSteck has its own method due to reuse
        resetRedoStack();
    }

    /*
     * When a cell is updated, that cell is added to the undoActionStack
     * and the pre-update value is added to the value stack.
     */
    public void addUndoAction(Cell cell) {
        undoActionStack.add(cell);
        undoValueStack.add(cell.getValue());
        lastUndoIndex++;

        // the RedoStack is only non-empty after undo has been click
        resetRedoStack();
    }

    /*
     * If the undoStack is non-empty clicking undo will pop the last updated cell
     * and set its value to the pre-update value from the valueStack at the
     * corresponding index. It will move this cell-value pair to the RedoStack.
     */
    public void undo() {
        if (undoActionStack.size() > 0 && undoValueStack.size() > 0) {
            Cell lastUpdated = undoActionStack.remove(lastUndoIndex); // Remove from undo stack
            redoActionStack.add(lastUpdated); // add to redo stack
            redoValueStack.add(lastUpdated.getValue()); // update last updated to pre-update value
            lastUpdated.updateValue(undoValueStack.remove(lastUndoIndex), true); // add to redo value stack
            lastUndoIndex--;
            lastRedoIndex++;
        }
    }

    /*
     * When an update other than undo occurs, both redo stacks are emptied.
     * Only an undo can be redone.
     */
    private void resetRedoStack() {
        redoActionStack = new ArrayList<>();
        redoValueStack = new ArrayList<>();
        lastRedoIndex = -1;
    }

    /*
     * If the redoStack is non-empty clicking redo will pop the last undone cell
     * and set its value to the pre-undo value from the valueStack at the
     * corresponding index. It will move this cell-value pair to the UndoStack.
     */
    public void redo() {
        if (redoActionStack.size() > 0 && redoValueStack.size() > 0) {
            Cell lastUpdated = redoActionStack.remove(lastRedoIndex); // remove from redo stack
            undoActionStack.add(lastUpdated); // add to undo stack
            undoValueStack.add(lastUpdated.getValue()); // add to undo value stack
            // update last undone to pre-undone value
            lastUpdated.updateValue(redoValueStack.remove(lastRedoIndex), true);
            lastRedoIndex--;
            lastUndoIndex++;
        }
    }

    /*
     * Only the control button is registered when pressing because it needs to be held to undo/redo
     */
    public void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.COMMAND)
            controlDown = true; // a boolean set to true when control is being held down
    }

    /*
     * Standalone handle key release method to handle undo/redo events. Control must be held down
     */
    public void handleKeyReleased(KeyEvent e) {
        // Holding control and tapping Z will undo
        if (e.getCode() == KeyCode.Z && controlDown)
            undo();
        // Holding control and tapping Y will redo
        else if (e.getCode() == KeyCode.Y && controlDown)
            redo();
        // Releasing control will set the boolean to false meaning it is not being held down
        else if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.COMMAND)
            controlDown = false;
            // Holding control and tapping Y will redo
    }
}
