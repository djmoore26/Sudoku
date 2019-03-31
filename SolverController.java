/**
 * This class controls the handling of the Solver mode. The primary
 * method is the recursive solver. There are also handler methods,
 * a method to output the solution, and other helper methods
 */

package sudoku;

import java.util.concurrent.TimeoutException;

public class SolverController {
    protected Board board;
    protected SceneController controller;
    protected int[][] boardValues = new int[9][9];
    protected Cell[][] currentBoard;
    private final long TIMEOUT = 10000;
    private long startTime;

    SolverController(Board board, SceneController controller) {
        this.board = board;
        this.controller = controller;
    }

    /*
     * Handler method for when the Solve button is clicked
     */
    public void handleClick() {
        setInitialBoard();
        if (solveWithTimeout()) {
            // Display the solved board on the grid
            outputSolvedBoard();
            // Additional check for correct solution
            board.checkSolution(false);
        }
        else {
            // Set all cells to be updateable to change the board to check
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    currentBoard[i][j].setUpdateable(true);
                    currentBoard[i][j].setSolverValid(false);
                }
            }
            // Create and show the invalid solution stage/popup
            SudokuPopUp popup = controller.getPopUp();
            popup.setMessageText("Solution not found.");
            popup.showPopup();
        }
    }

    /*
     * Grabs the current board and creates a new integer array that
     * is used to solve the board instead of using the Cell array.
     */
    protected void setInitialBoard() {
        currentBoard = board.getCurrentBoard();
        int updateValue;

        // Populate integer array
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                updateValue = currentBoard[i][j].getValue();
                boardValues[i][j] = updateValue;
                Board.checkRelatedValues(currentBoard[i][j],i, j);

                if (updateValue > 0)
                    currentBoard[i][j].setUpdateable(false);
            }
        }
    }

    /*
     * Output the found solution to the UI board by updating all the values
     */
    private void outputSolvedBoard() {
        Cell[][] currentBoard = board.getCurrentBoard();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (currentBoard[i][j].getUpdateable()) {
                    currentBoard[i][j].setValue(boardValues[i][j], true);
                    currentBoard[i][j].setUpdateable(false);
                }
            }
        }
    }

    /*
     * Help method that calls the actual solve() method and catches any
     * timeout that occurs with an invalid solution
     */
    protected boolean solveWithTimeout() {
        try {
            // Set the start time to check for timeout
            startTime = System.currentTimeMillis();
            solve();
            return true;
        }
        catch (TimeoutException ex) {
            return false;
        }
    }

    /*
     * Method that solves the sudoku board using the populated integer array
     */
    private boolean solve() throws TimeoutException {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Check if it is time to throw a TimeoutException
                if (System.currentTimeMillis() - startTime > TIMEOUT)
                    throw new TimeoutException();

                Cell curCell = currentBoard[i][j];
                // Check if the Cell's value can be changed
                if (curCell.getUpdateable() && !curCell.getSolverValid()) {
                    // Every Cell has possible values stored; only try those
                    for (int k = 0; k < curCell.possible.size(); k++) {
                        // Update the value of the integer array
                        boardValues[i][j] = curCell.possible.get(k);

                        // Check if the above value is possible
                        if (checkRelatedValuesInt(i, j)) {
                            curCell.setSolverValid(true);

                            // Recursively try the next Cell, or backtrack and try this next one instead
                            if (solve())
                                return true;
                        }
                    }
                    // Reset to 0 and invalid if this value is not correct
                    curCell.setSolverValid(false);
                    boardValues[i][j] = 0;
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * Same check method as in Board, but applies to this integer array, not
     * the Board Cell array
     */
    private boolean checkRelatedValuesInt(int i, int j) {
        int curValue = boardValues[i][j], boxRow = Board.findBoxStartingPoint(i), boxColumn = Board.findBoxStartingPoint(j);

        for (int k = 0; k < 9; k++) {
            // Check each number in the current row
            if (curValue == boardValues[i][k] && k != j)
                return false;

            // Check each number in the current column
            if (curValue == boardValues[k][j] && k != i)
                return false;

            // Check each number in the current box
            if (curValue == boardValues[boxRow][boxColumn] && boxRow != i && boxColumn != j)
                return false;

            // If-else block to increment boxRow and boxColumn properly to check all 8
            // values in the same box
            if (boxColumn % 3 == 2) {
                boxColumn -= 2;
                boxRow++;
            } else
                boxColumn++;
        }

        return true;
    }
}
