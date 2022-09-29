package com.example.fifteen_squares_schiff;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.TextView;

public class SquareView extends SurfaceView {

    public static int numberOffsetY = 18;

    Paint squarePaint = new Paint();
    Paint numberPaint = new Paint();
    Paint zeroPaint = new Paint();
    Paint selectPaint = new Paint();
    Paint correctPaint = new Paint();

    TextView sizeText;

    SquareModel squareModel;


    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //This is essential or your onDraw method won't get called
        setWillNotDraw(false);

        squareModel = new SquareModel();

        squarePaint.setColor(0xFFC755B5);  //violet-red
        squarePaint.setStyle(Paint.Style.FILL);
        zeroPaint.setColor(Color.GRAY);
        zeroPaint.setStyle(Paint.Style.FILL);
        selectPaint.setColor(Color.RED);
        selectPaint.setStyle(Paint.Style.FILL);
        correctPaint.setColor(Color.YELLOW);
        correctPaint.setStyle(Paint.Style.FILL);

        numberPaint.setColor(0xFF000000);  //black
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTextSize(squareModel.numberTextSize);
        numberPaint.setTextAlign(Paint.Align.CENTER);

        setBackgroundColor(Color.LTGRAY);  //better than black default

    }


    public void drawSquare(Canvas canvas, int r, int c) {
        int number = squareModel.board[r][c];

        Paint color = squarePaint;
        float dragX = 0;
        float dragY = 0;
        boolean drawBlank = false;

        if (c * squareModel.size + r + 1 == number) {
            color = correctPaint;
        }

        if (number == 0) {
            color = zeroPaint;
            drawBlank = true;

        } else if (squareModel.selected[0] == r && squareModel.selected[1] == c) {
            color = selectPaint;
            dragX = squareModel.difference[0];
            dragY = squareModel.difference[1];
            drawBlank = true;
        }

        int spacing = squareModel.squareSpacing;
        int drawSize = squareModel.squareDrawSize;
        int xOffset = (canvas.getWidth() - (squareModel.size * drawSize + (squareModel.size - 1) * spacing)) / 2;
        int yOffset = (canvas.getHeight() - (squareModel.size * drawSize + (squareModel.size - 1) * spacing)) / 2;

        float x = r * (drawSize + spacing) + xOffset + dragX;
        float y = c * (drawSize + spacing) + yOffset + dragY;

        if (drawBlank) {
            canvas.drawRect(x - dragX, y - dragY, x + drawSize - dragX,
                     y + drawSize - dragY, zeroPaint);
        }

        if (number != 0) {
            canvas.drawRect(x, y, x + drawSize, y + drawSize, color);
            canvas.drawText(""+number, x + drawSize / 2,
                         y + drawSize / 2 + numberOffsetY, numberPaint);
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        drawSquare(canvas, squareModel.zeroPosition[0], squareModel.zeroPosition[1]);

        for (int r = 0; r < squareModel.size; r ++) {
            for (int c = 0; c < squareModel.size; c++) {
                if (!(r == squareModel.zeroPosition[0] && c == squareModel.zeroPosition[1])) {
                    drawSquare(canvas, r, c);
                }
            }
        }
    }


    public SquareModel getSquareModel() {
        return squareModel;
    }

    public void setSizeText(TextView _sizeText) {
        sizeText = _sizeText;
    }
}
