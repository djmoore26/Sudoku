/**
 * This class contains all the methods related to getting,
 * creating and displaying the Sudoku Board. There are also
 * several helper methods to make code a little more
 * concise that are reused elsewhere.
 */

package sudoku;

import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Board extends GridPane{
    public UndoRedoController undoRedoController;
	private static Cell[][] board = new Cell[9][9]; // The board itself
	private boolean doneChecked = false;
	private int boardNumber;
	// List of the possible boards for each difficulty
	private static ArrayList<ArrayList<Integer>> attemptedBoardNumbers;
	private static int difficulty;
    private String title;

	Board() {
		undoRedoController = new UndoRedoController();
		attemptedBoardNumbers  = new ArrayList<>();

		// Initialize the lists with boards 1-20 for each difficulty
		for (int i = 0; i < 3; i++) {
            ArrayList<Integer> initialList = new ArrayList<>();
            for (int k = 1; k <= 20; k++) {
                initialList.add(Integer.valueOf(k));
            }
            attemptedBoardNumbers.add(initialList);
        }

        getBoard(0, false);
    }

    /*
     * Separate get method that returns only the current board rather
     * than creating a new one. Returns the Cell array.
     */
	public Cell[][] getCurrentBoard() {
		return board;
	}

	/*
	 * This is an extensive method that creates the GridPane containing
	 * the board as well as populate it with the initial values and
	 * handlers.
	 */
	public GridPane getBoard(int difficulty, boolean solver) {
		// Passing a difficulty will get a new board of the requested difficulty
        if (difficulty > 0) {
            this.difficulty = difficulty;

            // If possible, gets a random boardNumber of the requested difficulty
            ArrayList<Integer> thisList = attemptedBoardNumbers.get(difficulty-1);
            if (thisList.size() > 0) {
				boardNumber = thisList.get((int) (Math.random() * thisList.size()));
				thisList.remove(Integer.valueOf(boardNumber));
			}
        }
        // Difficulty 0 is an empty board
        else if (difficulty == 0)
            this.difficulty = difficulty;
        // else difficulty -1 will return the current initial board

        doneChecked = false;

        int[][] boardValues = getBoardValues(); // get the values of the chosen boardNumber

		// Create the GridPane that will be returned and shown on the stage
		GridPane boardGrid = SceneController.getBackgroundGridPane(1,1);
		boardGrid.setGridLinesVisible(true);

		for (int i = 0; i < boardValues.length; i++) {
			for (int j = 0; j < boardValues[i].length; j++) {
				// Create Cell with the uploaded value
				Cell curCell = new Cell(boardValues[i][j],i,j, solver, this);
				curCell.setOnMouseClicked(e -> curCell.buttonClickBehavior(curCell));
			 	curCell.setBlockBorders(i, j);
			 	curCell.setOnMouseDragReleased(e -> curCell.onDragReleased());
				boardGrid.add(curCell,j,i); // Add to column i, row j of the board
			 	board[i][j] = curCell;
			}
		}

		// Check solution is called to initialize the possible Tooltip values
		checkSolution(solver);

		return boardGrid;
	}

	/*
	 * This method finds in the correct file and returns the 2D int
	 * array of values for the initial board. It also sets the Board
	 * title based on the difficulty and board number
	 */
	private int[][] getBoardValues() throws InputMismatchException{
		int[][] boardValues = new int[9][9];

		File boardFile;
		switch (difficulty) {
			case 1:
				boardFile = new File("easy-puzzles.txt");
				title = "Easy - " + boardNumber;
				break;
			case 2:
				boardFile = new File("medium-puzzles.txt");
                title = "Medium - " + boardNumber;
                break;
			case 3:
				boardFile = new File("hard-puzzles.txt");
                title = "Hard - " + boardNumber;
                break;
			default:
				return new int[9][9]; // empty board for difficulty 0
		}

		// Try with resources to open and read from teh boardFile of determined difficutly
		try (Scanner fileReader = new Scanner(boardFile)) {
            String clear; // used to skip n number of lines in the file to the correct board

			// loop to skip lines until the correct starting board
			for (int f = 0; f <= (boardNumber - 1)* 10; f++) {
				clear = fileReader.nextLine();

				// does not count empty lines, but the line has already been cleared
				if (clear.trim().length() == 0)
					f--;
			}

			// Read integers from the board file until the board is filled
			// (including zeroes)
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					boardValues[i][j] = fileReader.nextInt();
				}
			}
		}
		// If the file is not found, it will exit the application
		// TODO add default board and/or board generator
		catch (FileNotFoundException ex) {
			System.out.println("file not found");
			System.exit(1);
		}
		// InputMisMatchException means something is wrong with the board file
		catch (InputMismatchException ex1) {
		    throw ex1;
        }
		finally {
			return boardValues;
		}
	}

	/*
	 * Checks if the current board is a valid solution. It is also called
	 * to remove impossible values from every Cell's possible list.
	 */
	public boolean checkSolution(boolean solver ) {
		boolean allValid = true, curValid;
		Cell curCell;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				curCell = board[i][j];

				// Called to update possible
				boolean temp = checkRelatedValues(curCell, i, j);

				// An empty Cell in player mode returns false so the background can be highlighted
                if (curCell.getValue() == 0 && !solver)
					curValid = false;
				// An empty Cell in solver mode returns true so the background is not highlighted
				else if (curCell.getValue() == 0 && solver)
					curValid = true;
				// If the Cell is not empty, it checks for validity using rules of Sudoku
				else
					curValid = temp;

				// Updates the background if applicable
				if ((doneChecked && difficulty != 3) || solver)
					board[i][j].updateBackground(curValid);

				// Separate boolean saying the whole board is invalid even if the last Cell is
				if (!curValid) {
					allValid = false;
				}
			}
		}

		return allValid;
	}

	/*
	 * This method checks the validity of a Cell's value by comparing it to the
	 * values of Cells in the same row, column and bow. It also updates the
	 * possible values for the current Cell
	 */
	public static boolean checkRelatedValues(Cell curCell, int i, int j) {
		int curValue = curCell.getValue(), boxRow = findBoxStartingPoint(i), boxColumn = findBoxStartingPoint(j);
		boolean valid = true;

		// Reset possible values in case some were added
		curCell.resetPossible();

		for (int k = 0; k < 9; k++) {
			// Check each number in the current row
			if (curValue == board[i][k].getValue() && k != j)
				valid = false;

            // Remove each number in the current row
            curCell.possible.remove(Integer.valueOf(board[i][k].getValue()));

			// Check each number in the current column
			if (curValue == board[k][j].getValue() && k != i)
				valid = false;

            // Remove each number in the current column
            curCell.possible.remove(Integer.valueOf(board[k][j].getValue()));

			// Check each number in the current box
			if (curValue == board[boxRow][boxColumn].getValue()
					&& boxRow != i && boxColumn != j)
				valid = false;

            // Remove each number in the current box
            curCell.possible.remove(Integer.valueOf(board[boxRow][boxColumn].getValue()));

            // If-else block to increment boxRow and boxColumn properly to check all 8
			// values in the same box
			if (boxColumn % 3 == 2) {
				boxColumn -= 2;
				boxRow++;
			}
			else
				boxColumn++;
		}

		// Updates the tooltip of each empty Cell
        curCell.updateTooltip();

		return valid;
	}

	/*
	 * Handler for when Done in play mode is clicked
	 */
	public boolean checkComplete(boolean solver) {
		doneChecked = true;
		boolean complete = checkSolution(solver);

		// If complet solution is valid, all Cells cannot be updated
		if (complete) {
		    for (int i = 0; i <9; i++) {
		        for (int j = 0; j < 9; j++) {
		            board[i][j].setUpdateable(false);
                }
            }
        }

		return complete;
	}

	/*
	 * Helper method to find the correct row and column starting point to
	 * iterate inside a 3x3 box
	 */
	public static int findBoxStartingPoint(int cell) {
		if (cell/3 == 0)
			return 0;
		else if (cell/3 == 1)
			return 3;
		else
			return 6;
	}

	/*
	 * Returns a formatted textbox containing the title of the current Board
	 */
	public Text getTitleField() {
	    Text titleField = new Text(title);
        titleField.setFont(Font.font("Verdana", FontWeight.BLACK, 24));
	    return titleField;
    }

    /*
	 * Sets the title field with a manual title (as opposed to the
	 * difficulty-determined one)before returning the formatted
	 * textbox containing the title of the current Board. This is
	 * called when all boards of this difficulty are played
     */
    public Text getTitleField(String newTitle) {
	    boardNumber = 0;
	    title = newTitle;
        return getTitleField();
    }

    /*
     * Static method to return the difficulty of the current board
     */
    public static int getDifficulty() {
	    return difficulty;
    }

    /*
     * Set method to manually update the current difficulty
     */
    public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/*
	 * Returns the number of remaining boards in the desired difficulty
	 */
	public static int getAttemptedBoardNumbersSize(int testDifficulty) {
		return attemptedBoardNumbers.get(testDifficulty-1).size();
	}

	/*
	 * Finds the difficulty used when Random is clicked. It is recursive
	 * to loop until it selects a difficulty that is not empty.
	 * TODO all 60 boards played then random is clicked will cause error
	 */
	public static int getRandomDifficulty() {
		int random = (int)(Math.random() *3) + 1;
		if (getAttemptedBoardNumbersSize(random) > 0)
			return random;
		else
			return getRandomDifficulty();
	}

	/*
	 * Typical toString method of 2D array. Not actually used anywhere, it was
	 * added during initial programming before the GUI was built.
	 */
	@Override
	public String toString() {
		String s ="";

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				s += board[i][j].getValue();
			}
			s += "\n";
		}

		return s;
	}
}

