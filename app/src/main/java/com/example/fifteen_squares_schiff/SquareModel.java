package com.example.fifteen_squares_schiff;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * SquareModel: Holds the data and methods required to display and manipulate
 *              the board.
 *
 * @author **** Henry Schiff ****
 * @version **** September 30th 2022 ****
 */
public class SquareModel {

    // Basic dimensions variables.
    public int size;
    public int area;

    // Position of zero within board, and the 2D array of the board's values.
    public int[] zeroPosition;
    public int[][] board;

    // Variables needed to handle motion events. Start is the initial position
    // of the mouse when first clicked, difference is the mouse's displacement,
    // selected is the index of the clicked square, and selectedDirection is
    // a vector that represents what direction the blank tile is in. All of
    // these variables are given these "null" starter values that can be checked
    // to tell whether a movement is in progress.
    public float[] start = {0, 0};
    public float[] difference = {0, 0};
    public int[] selected = {-1, -1};
    public int[] selectedDirection = {0, 0};

    // The size that is set by the seekbar and used when the board resets.
    public int nextSize;

    // The dimensions of the on-screen elements. The base dimensions are established
    // first so that the actual dimensions can be scaled properly upon the board's
    // size being changed.
    public final int baseSquareSpacing = 15;
    public final int baseSquareDrawSize = 140;
    public final int baseNumberTextSize = 60;
    public final int baseNumberOffsetY = 18;
    public int squareSpacing = baseSquareSpacing;
    public int squareDrawSize = baseSquareDrawSize;
    public int numberTextSize = baseSquareDrawSize;
    public int numberOffsetY = baseNumberOffsetY;

    // The distance that the mouse must be dragged in order for a release of the
    // mouse to be considered a complete movement. This is necessary so that the
    // user can't just drag their mouse over by a few pixels and see the square
    // snap into place.
    public int releaseThreshold = squareDrawSize / 3;

    // The relative positions of neighboring squares, used to check for zeros.
    public final int[][] neighborPositions = {
            new int[] {1, 0}, new int[] {0, 1}, new int[] {-1, 0}, new int[] {0, -1},
    };

    public SquareModel() {
        size = 4;
        area = size * size;
        nextSize = size;
        resetBoard();
    }


    /**
     * Resets the board to a new, solvable configuration using the size specified
     * by the seekbar.
     */
    public void resetBoard() {

        // Change size to the next one (value from seekbar) and calculate area.
        size = nextSize;
        area = size * size;

        // Rescale the on-screen dimensions according to their base dimensions
        // and the new size of the board. The default is four, so its multiplied
        // four over the new size.
        squareSpacing = baseSquareSpacing * 4 / size;
        squareDrawSize = baseSquareDrawSize * 4 / size;
        numberTextSize = baseNumberTextSize * 4 / size;
        numberOffsetY = baseNumberOffsetY * 4 / size;

        // Also recalculate the release threshold.
        releaseThreshold = squareDrawSize / 3;

        // Create empty arrays for the new board and it's numbers.
        ArrayList<Integer> numbers = new ArrayList<>();
        int[][] newBoard;

        // This while loop will keep generating new boards until it creates one
        // that is solvable.
        while (true) {

            // Fill numbers array
            for (int i = 0; i < area; i ++) {
                numbers.add(i);
            }

            // Initialize 2D array
            newBoard = new int[size][size];

            // Until the numbers array is empty, take a random number out and
            // add it to the 2D board array.
            for (int i = 0; i < area; i++) {
                int index = (int) (Math.random() * numbers.size());
                int number = numbers.get(index);

                // Using integer division and modulo, the row and column can
                // be calculated.
                newBoard[i / size][i % size] = number;
                numbers.remove(index);

                // Keep track of the zero's (blank square) indices.
                if (number == 0) {
                    zeroPosition = new int[]{i / size, i % size};
                }
            }

            // Check if the board is solvable
            boolean possible = checkSolubility(newBoard);

            // If so, exit the while loop. Otherwise, generate a new one.
            if (possible) {
                break;
            }
        }

        // Reassign the new, solvable board.
        board = newBoard;
    }


    /**
     * Convert the mouse's position to the indices of the square it overlaps, if any.
     *
     * @param width the width of the squareView canvas.
     * @param height the height of the squareView canvas.
     * @param x the x-coordinate of the mouse's position.
     * @param y the y-coordinate of the mouse's position.
     *
     * @return the indices of the overlapped square. If no square is overlapped,
     *         returns the "null" array {-1, -1}.
     */
    public int[] mouseToSquare(int width, int height, float x, float y) {

        // Calculate x and y offsets of the board based on screen dimensions
        int xOffset = (width - (size * squareDrawSize + (size - 1) * squareSpacing)) / 2;
        int yOffset = (height - (size * squareDrawSize + (size - 1) * squareSpacing)) / 2;

        // Calculates the x-index of the moused-over square and the "overflow". The overflow
        // is greater than zero if the mouse is between squares, aka in the spacing.
        int xOverflow = (int) (x - xOffset) % (squareDrawSize + squareSpacing) - squareDrawSize;
        int xIndex = (int) (x - xOffset) / (squareDrawSize + squareSpacing);

        // Same calculations for y-index and y-overflow.
        int yOverflow = (int) (y - yOffset) % (squareDrawSize + squareSpacing) - squareDrawSize;
        int yIndex = (int) (y - yOffset) / (squareDrawSize + squareSpacing);

        // Checks several things to see if mouse is out of bounds. If overflows are greater than
        // zero, the mouse is between the squares. If the indices are greater than the size of
        // the board, their out of bounds. If the mouse's position is less than the offsets,
        // it's not even overlapping with the board.
        if (xOverflow > 0 || yOverflow > 0 ||
            xIndex > size - 1 || yIndex > size - 1 ||
            x < xOffset || y < yOffset)
        {
            // return the null value if any of these conditions are true.
            return new int[] {-1, -1};
        }

        // otherwise, return the calculated indices
        return new int[] {xIndex, yIndex};
    }


    /**
     * Checks for a neighboring zero square.
     *
     * @param square the indices of the square to be checked.
     *
     * @return the the direction that the neighboring zero square is in, if found.
     */
    public int[] checkForZero(int[] square) {

        // Loop through the relative neighbor positions.
        for (int[] nb : neighborPositions) {
            try {

                // Add relatives to the squares indices.
                int xIndex = square[0] + nb[0];
                int yIndex = square[1] + nb[1];

                // If the calculated square in that direction is blank, return
                // the direction used.
                if (board[xIndex][yIndex] == 0) {
                    return nb;
                }

            // This occurs if the checked square is on the edge of the board.
            } catch (Exception ArrayIndexOutOfBoundsException) {
                continue;
            }
        }

        // If no blank neighbor was found, return the null value.
        return new int[] {0, 0};
    }


    /**
     External Citation
         Date: 30 September 2022
         Problem: I did not know how to determine whether a randomly generated
                  is solvable or not.
         Solution: This article on Wolfram MathWorld gives a detailed method of
                   of how to determine solubility based on the amount of
                   inverse permutations in the board.
         Resource:
            https://mathworld.wolfram.com/15Puzzle.html
     */

    /**
     * Checks if a board is solvable.
     *
     * @param board the indices of the square to be checked.
     *
     * @return true if the board is solvable, false otherwise.
     */
    public boolean checkSolubility(int[][] board) {

        // Create a hashmap to link numbers to the amount of following numbers
        // that are less than them.
        Map<Integer, Integer> permInversions = new HashMap<>();

        // Loop through each square in board.
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {

                // Loop through each key already in the hashmap.
                for (Integer key : permInversions.keySet()) {

                    // If the currently indexed number is less than a number
                    // already in the map, than it is a permutation inversion.
                    // Technically, the zero is not actually a square so don't
                    // account for it.
                    if (board[r][c] < key && board[r][c] != 0) {

                        // increment the count in the map
                        int count = permInversions.get(key);
                        permInversions.put(key, count + 1);
                    }
                }

                // Now add the indexed number to the map with a count of zero.
                permInversions.put(board[r][c], 0);
            }
        }

        // Calculate the sum of every permutation inversion.
        int sum = 0;
        for (Integer count : permInversions.values()) {
            sum += count;
        }

        // Return true if the sum is even, false otherwise.
        return (sum % 2 == 0);
    }


    /**
     * Handles what the user's attempt to click a square.
     *
     * @param x the x-coordinate of the mouse's location.
     * @param y the y-coordinate of the mouse's location.
     * @param width the width of the squareView's canvas.
     * @param height the height of the squareView's canvas.
     */
    public void handleDownEvent(float x, float y, int width, int height) {

        // Make sure no square is selected (-1 is the null value).
        if (selected[0] == -1) {

            // Get indices of hovered-over square.
            int[] square = mouseToSquare(width, height, x, y);

            // Make sure a square was detected.
            if (square[0] >= 0) {

                // Set the start coordinates of the drag, the selected square,
                // and determine if a zero is neighboring it.
                start = new float[]{x, y};
                selected = square;
                selectedDirection = checkForZero(square);
            }
        }
    }


    /**
     * Handles the release of a user's click, completing the dragging of a square.
     */
    public void handleUpEvent() {

        // Make sure a square is selected.
        if (selected[0] >= 0) {

            // Determine whether the mouse's displacement surpasses the
            // threshold for release.
            if (Math.abs(difference[0]) > releaseThreshold ||
                Math.abs(difference[1]) > releaseThreshold)
            {
                // Swap the position of the selected square and the blank square.
                board[zeroPosition[0]][zeroPosition[1]] = board[selected[0]][selected[1]];
                board[selected[0]][selected[1]] = 0;

                // Update the new position of the blank square.
                zeroPosition[0] = selected[0];
                zeroPosition[1] = selected[1];
            }

            /**
             External Citation
                 Date: 25 September 2022
                 Problem: I wanted an elegant way of resetting an array, as
                          opposed to manually changing the values.
                 Solution: From this post I learned about the fill method, which
                           replaces every value in the array with the provided one.
                 Resource:
                 https://stackoverflow.com/questions/4208655/empty-an-array-in-java-processing
             */

            // Reset relevant variables with their null values.
            Arrays.fill(selected, -1);
            Arrays.fill(start, 0);
            Arrays.fill(difference, 0);
        }
    }


    /**
     * Handles a user's dragging of the mouse, which should move a square.
     *
     * @param x the x-coordinate of the mouse's location.
     * @param y the y-coordinate of the mouse's location.
     */
    public void handleMoveEvent(float x, float y) {

        // Make sure a square is selected.
        if (selected[0] >= 0) {

            // See if the direction of the neighboring zero lies along the x-axis.
            if (selectedDirection[0] != 0) {

                // Calculate the mouse's displacement from where the drag began.
                difference[0] = x - start[0];

                // If the user is trying to drag the square backwards, reset its position.
                if ((difference[0] < 0) != (selectedDirection[0] < 0)) {
                    difference[0] = 0;

                // If the user is trying to drag the square too far, limit it to the edge.
                } else if (Math.abs(difference[0]) > squareDrawSize + squareSpacing) {
                    difference[0] = (squareDrawSize + squareSpacing) * selectedDirection[0];
                }


            // Repeat these conditional changes but for movement along the y-axis.
            } else if (selectedDirection[1] != 0) {
                difference[1] = y - start[1];

                if ((difference[1] < 0) != (selectedDirection[1] < 0)) {
                    difference[1] = 0;

                } else if (Math.abs(difference[1]) > squareDrawSize + squareSpacing) {
                    difference[1] = (squareDrawSize + squareSpacing) * selectedDirection[1];
                }
            }
        }
    }
}
