package com.example.fifteen_squares_schiff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.TextView;


/**
 * SquareView: Dynamically draws the play-board.
 *
 * @author **** Henry Schiff ****
 * @version **** September 30th 2022 ****
 */
public class SquareView extends SurfaceView {

    // Initialize paints for colors and fonts.
    Paint squarePaint = new Paint();
    Paint numberPaint = new Paint();
    Paint zeroPaint = new Paint();
    Paint selectPaint = new Paint();
    Paint correctPaint = new Paint();

    // Declare member variables
    TextView sizeText;
    SquareModel squareModel;


    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Allows onDraw method to be called
        setWillNotDraw(false);

        // Creates new squareModel
        squareModel = new SquareModel();

        // Set colors of the paints
        squarePaint.setColor(0xFFC755B5);  //violet-red
        squarePaint.setStyle(Paint.Style.FILL);
        zeroPaint.setColor(Color.GRAY);
        zeroPaint.setStyle(Paint.Style.FILL);
        selectPaint.setColor(0xFF963c88); //purple
        selectPaint.setStyle(Paint.Style.FILL);
        correctPaint.setColor(0xFFffcb1f); //yellow
        correctPaint.setStyle(Paint.Style.FILL);

        // Set the color and font specifications for the numbers.
        numberPaint.setColor(0xFF000000);  //black
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTextSize(squareModel.numberTextSize);
        numberPaint.setTextAlign(Paint.Align.CENTER);

        setBackgroundColor(Color.LTGRAY);
    }


    /**
     * Draws a singular square based on it's position within the board.
     *
     * @param canvas the canvas to be drawn to.
     * @param r the row of the square.
     * @param c the column of the square.
     *
     */
    public void drawSquare(Canvas canvas, int r, int c) {

        // Get the square's number from the board.
        int number = squareModel.board[r][c];

        // Initialize default draw-values
        Paint color = squarePaint;
        float dragX = 0;
        float dragY = 0;
        boolean drawBlank = false;

        // If square's number corresponds with it's position, it's in the correct
        // spot and should be drawn with a different color.
        if (c * squareModel.size + r + 1 == number) {
            color = correctPaint;
        }

        // If the number is zero, change paint color and enable "drawBlank".
        if (number == 0) {
            color = zeroPaint;
            drawBlank = true;

        // If the square is currently selected, change color and change the drag
        // the drag variables so that it's drawn with the offset of the user's mouse.
        } else if (squareModel.selected[0] == r && squareModel.selected[1] == c) {
            color = selectPaint;
            dragX = squareModel.difference[0];
            dragY = squareModel.difference[1];
            drawBlank = true;
        }

        // Access relevant variables and calculate the offsets of the board so that its
        // centered within the screen.
        int spacing = squareModel.squareSpacing;
        int drawSize = squareModel.squareDrawSize;
        int xOffset = (canvas.getWidth() - (squareModel.size * drawSize +
                      (squareModel.size - 1) * spacing)) / 2;
        int yOffset = (canvas.getHeight() - (squareModel.size * drawSize +
                      (squareModel.size - 1) * spacing)) / 2;

        // Accounting for the squares position, size, offsets and user's drag,
        // calculate it's x and y coordinates.
        float x = r * (drawSize + spacing) + xOffset + dragX;
        float y = c * (drawSize + spacing) + yOffset + dragY;

        // If the drawBlank boolean was enabled, draw the gray, blank square. This is only
        // drawn for the zero-square as well as the one that is selected, since you can see
        // behind it when it's being dragged.
        if (drawBlank) {
            canvas.drawRect(x - dragX, y - dragY, x + drawSize - dragX,
                     y + drawSize - dragY, zeroPaint);
        }

        // Finally, draw the regular square at it's calculated position and write its value.
        if (number != 0) {
            canvas.drawRect(x, y, x + drawSize, y + drawSize, color);
            canvas.drawText(""+number, x + drawSize / 2,
                         y + drawSize / 2 + squareModel.numberOffsetY, numberPaint);
        }
    }


    /**
     * This is called whenever the squareView is invalidated, and redraws each square.
     *
     * @param canvas the canvas to be drawn to.
     *
     */
    @Override
    public void onDraw(Canvas canvas) {

        // Draw the blank square first.
        drawSquare(canvas, squareModel.zeroPosition[0], squareModel.zeroPosition[1]);

        // Loop through every other square in the board and draw it.
        for (int r = 0; r < squareModel.size; r ++) {
            for (int c = 0; c < squareModel.size; c++) {
                if (!(r == squareModel.zeroPosition[0] && c == squareModel.zeroPosition[1])) {
                    drawSquare(canvas, r, c);
                }
            }
        }
    }


    /**
     * Simple getter method to retrieve the squareModel.
     *
     * @return the squareModel
     *
     */
    public SquareModel getSquareModel() {
        return squareModel;
    }


    /**
     * Setter method to pass the sizeText so that it can be updated later by
     * the controller.
     *
     * @param _sizeText the TextView displaying the size of the board.
     *
     */
    public void setSizeText(TextView _sizeText) {
        sizeText = _sizeText;
    }
}
