package com.example.fifteen_squares_schiff;

import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import java.util.Arrays;

public class SquareController implements
        View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private SquareView squareView;
    private SquareModel squareModel;

    public SquareController(SquareView _squareView) {
        squareView = _squareView;
        squareModel = squareView.getSquareModel();
    }

    @Override
    public void onClick(View view) {
        squareModel.resetBoard();
        squareView.numberPaint.setTextSize(SquareModel.numberTextSize);
        squareView.invalidate();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        switch (motionEvent.getAction()) {

            case (MotionEvent.ACTION_DOWN):
                if (squareModel.selected[0] == -1) {
                    int[] square = squareModel.mouseToSquare(squareView.getWidth(), squareView.getHeight(), x, y);

                    if (square[0] >= 0) {
                        squareModel.start = new float[]{motionEvent.getX(), motionEvent.getY()};
                        squareModel.selected = square;
                        squareModel.selectedDirection = squareModel.checkForZero(square);
                        squareView.invalidate();
                    }
                }
                break;

            case (MotionEvent.ACTION_UP):
                squareModel.handleUpEvent();
                squareView.invalidate();
                break;

            case (MotionEvent.ACTION_MOVE):
                squareModel.handleMoveEvent(motionEvent.getX(), motionEvent.getY());
                squareView.invalidate();
                break;
        }

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        squareModel.nextSize = i;
        squareView.sizeText.setText(i+" x "+i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
