/**
 * The HintController class is a class the controls the ability to provide a hint.
 * The number of hints allowed depends on the board difficulty. It extends
 * SolverController because it utilizes a solved board to provide the hints.
 */

package sudoku;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;

public class HintController extends SolverController{
    // Stores [row,column] pairs of the empty cells available to populate with a hint
    private ArrayList<int[]> available;
    private Tooltip hoverHint;
    private int remaining;

    HintController(Board board, SceneController controller) {
        super(board, controller);
        setInitialBoard();
        solveWithTimeout(); // Solves the board using a super method so that the correct value can be hinted
        setInitialRemaining(board.getDifficulty()); // uses the current difficulty of the board
        initializeAvailable();
    }

    /*
     * Overrides the SolverController handleClick() method. When the hint button is clicked,
     * if a particular cell is selected, that is the cell that gets the hint. If a Cell is
     * not selected, a random available cell gets updated
     */
    @Override
    public void handleClick() {
        // There is a minimum number of hints allowed per game
        if (remaining > 0) {
            initializeAvailable(); // Update which cells are empty to provide a hint

            // If a Cell is selected, that Cell gets the hint
            if (SudokuButton.selected != null && SudokuButton.selected instanceof Cell) {
                getHint(SudokuButton.selected.getRow(), SudokuButton.selected.getColumn());
                SudokuButton.selected.updateSelected(null); // Unselect and unhighlight the hint Cell
            }
            // Check that the board is not full to prevent IndexOutOfBound exception
            else if (available.size() > 0){
                // A random empty cell is chosen to be updated with the hint value
                int availableIndex = (int) (Math.random() * available.size());
                int row = available.get(availableIndex)[0], column = available.get(availableIndex)[1];
                getHint(row, column);
            }
            remaining--; // Decrement the remaining allowed hints
            updateTipText();
        }
    }

    /*
     * This method walks the initial board to find the empty cells, then stores
     * those cell's [row,column] pairs in available
     */
    private void initializeAvailable() {
        currentBoard = board.getCurrentBoard();
        available = new ArrayList<>();

        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j++) {
                // Only stores empty cells
                if (currentBoard[i][j].getValue() == 0) {
                    int[] addPair = {i,j}; // Creates a [row,column] pair
                    available.add(addPair); // Stores that pair
                }
            }
        }
    }

    /*
     * The number of initial hints allowed per game depends on the difficulty.
     * This sets the initial number of hints allowed.
     */
    private void setInitialRemaining(int difficulty) {
        switch (difficulty) {
            case 1:
                remaining = 80;
                break;
            case 2:
                remaining = 6;
                break;
            case 3:
                remaining = 3;
        }
    }

    /*
     * When a hint is used, remaining is decremented. This method updates
     * the Tooltip's text to the new remaining number of hints.
     */
    private void updateTipText() {
        hoverHint.setText("Hints Remaining: " + remaining);
    }

    /*
     * Helper method that updates the correct cell with its correct value upon click
     */
    private void getHint(int row, int column) {
        // This is not an undo action, but the undo argument is set to true so that the
        // hint update is not added to the UndoStack
        currentBoard[row][column].updateValue(boardValues[row][column], true);
    }

    /*
     * Creates and installs a Tooltip, which shows the remaining number of hints allowed
     * when hovering over the Hint MenuButton
     */
    public void installHintTooltip(MenuButton hintButton) {
        hoverHint = new Tooltip("Hints Remaining: " + remaining);
        hoverHint.setShowDelay(Duration.seconds(1.25)); // Hover for 1.25 seconds before showing
        hoverHint.setHideDelay(Duration.seconds(0)); // Hide immediately after moving/clicking
        hoverHint.setFont(Font.font("Verdana", 10));

        hintButton.setTooltip(hoverHint); // add it to the passed (Hint)MenuButton
    }
}