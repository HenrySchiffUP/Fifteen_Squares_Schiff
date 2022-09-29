package com.example.fifteen_squares_schiff;

import android.util.Log;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Arrays;

public class SquareModel {

    public int size;
    public int area;

    public int[] zeroPosition;
    public int[][] board;

    public float[] start;
    public float[] difference = {0, 0};
    public int[] selected = {-1, -1};
    public int[] selectedDirection = {0, 0};

    public int nextSize;

    public static final int baseSquareSpacing = 15;
    public static final int baseSquareDrawSize = 140;
    public static final int baseNumberTextSize = 60;
    public static int squareSpacing = baseSquareSpacing;
    public static int squareDrawSize = baseSquareDrawSize;
    public static int numberTextSize = baseSquareDrawSize;

//    public static int releaseThreshold = (squareSpacing + 2 * squareDrawSize) / 2 - (squareDrawSize) / 2;
    public static int releaseThreshold = squareDrawSize / 3;

    public static final int[][] neighborPositions = {
            new int[] {1, 0}, new int[] {0, 1}, new int[] {-1, 0}, new int[] {0, -1},
    };

    public SquareModel() {
        size = 4;
        area = size * size;
        nextSize = size;
        resetBoard();
    }


    public void resetBoard() {
        size = nextSize;
        area = size * size;

        squareSpacing = baseSquareSpacing * 4 / size;
        squareDrawSize = baseSquareDrawSize * 4 / size;
        numberTextSize = baseNumberTextSize * 4 / size;

        releaseThreshold = squareDrawSize / 3;

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < area; i ++) {
            numbers.add(i);
        }

        int[][] newBoard = new int[size][size];

        for (int i = 0; i < area; i ++) {
            int index = (int) (Math.random() * numbers.size());
            int number = numbers.get(index);
            newBoard[i / size][i % size] = number;
            numbers.remove(index);

            if (number == 0) {
                zeroPosition = new int[] {i / size, i % size};
            }
        }

        board = newBoard;
    }


    public int[] mouseToSquare(int width, int height, float x, float y) {

        int xOffset = (width - (size * squareDrawSize + (size - 1) * squareSpacing)) / 2;
        int yOffset = (height - (size * squareDrawSize + (size - 1) * squareSpacing)) / 2;

        int xOverflow = (int) (x - xOffset) % (squareDrawSize + squareSpacing) - squareDrawSize;
        int xIndex = (int) (x - xOffset) / (squareDrawSize + squareSpacing);

        int yOverflow = (int) (y - yOffset) % (squareDrawSize + squareSpacing) - squareDrawSize;
        int yIndex = (int) (y - yOffset) / (squareDrawSize + squareSpacing);

        if (xOverflow > 0 || yOverflow > 0 ||
            xIndex > size - 1 || yIndex > size - 1 ||
            x < xOffset || y < yOffset)
        {
            return new int[] {-1, -1};
        }

        return new int[] {xIndex, yIndex};
    }


    public int[] checkForZero(int[] square) {

        for (int[] nb : neighborPositions) {
            try {
                int xIndex = square[0] + nb[0];
                int yIndex = square[1] + nb[1];

                if (board[xIndex][yIndex] == 0) {
                    return nb;
                }

            } catch (Exception ArrayIndexOutOfBoundsException) {
                continue;
            }
        }

        return new int[] {0, 0};
    }

    public void handleMoveEvent(float x, float y) {
        if (selected[0] >= 0) {
            if (selectedDirection[0] != 0) {
                difference[0] = x - start[0];

                if ((difference[0] < 0) != (selectedDirection[0] < 0)) {
                    difference[0] = 0;

                } else if (Math.abs(difference[0]) > SquareModel.squareDrawSize + SquareModel.squareSpacing) {
                    difference[0] = (SquareModel.squareDrawSize + SquareModel.squareSpacing) * selectedDirection[0];
                }

            } else if (selectedDirection[1] != 0) {
                difference[1] = y - start[1];

                if ((difference[1] < 0) != (selectedDirection[1] < 0)) {
                    difference[1] = 0;

                } else if (Math.abs(difference[1]) > SquareModel.squareDrawSize + SquareModel.squareSpacing) {
                    difference[1] = (SquareModel.squareDrawSize + SquareModel.squareSpacing) * selectedDirection[1];
                }
            }
        }
    }
    
    public void handleUpEvent() {
        if (selected[0] >= 0) {
            if (Math.abs(difference[0]) > releaseThreshold || Math.abs(difference[1]) > releaseThreshold) {

                board[zeroPosition[0]][zeroPosition[1]] = board[selected[0]][selected[1]];
                board[selected[0]][selected[1]] = 0;

                zeroPosition[0] = selected[0];
                zeroPosition[1] = selected[1];
            }

            Arrays.fill(selected, -1);
            Arrays.fill(start, 0);
            Arrays.fill(difference, 0);
        }
    }
}
