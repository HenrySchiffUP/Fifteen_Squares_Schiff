package com.example.fifteen_squares_schiff;

import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import java.util.Arrays;


/**
 * SquareController: implements interfaces for views and the seekbar, allowing
 *                   their listeners to be set.
 *
 * @author **** Henry Schiff ****
 * @version **** September 30th 2022 ****
 */
public class SquareController implements
        View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    // Declare member variables of controller
    private final SquareView squareView;
    private final SquareModel squareModel;

    public SquareController(SquareView _squareView) {

        // Assign the passed in squareView, then receive the squareModel from it.
        squareView = _squareView;
        squareModel = squareView.getSquareModel();
    }

    /**
     * Gets called when the reset button is clicked. Resets the board, and
     * updates the textSize label accordingly.
     *
     * @param view the view being clicked
     */
    @Override
    public void onClick(View view) {
        squareModel.resetBoard();
        squareView.numberPaint.setTextSize(squareModel.numberTextSize);
        squareView.invalidate();
    }

    /**
     * Gets called when a motion event occurs on the squareView. This event can
     * be a click, the release of a click, or the movement of the mouse while it
     * is clicked.
     *
     * @param view the view being clicked
     * @param motionEvent one of the three aforementioned events
     *
     * @return true to "consume" the event.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        // Get relevant variables from the event and squareView.
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int width = squareView.getWidth();
        int height = squareView.getHeight();

        // Switch statement that will run a case corresponding to the type of
        // motion event that occurred. Each case calls an appropriate handler
        // method from the squareModel, then breaks.
        switch (motionEvent.getAction()) {

            case (MotionEvent.ACTION_DOWN):
                squareModel.handleDownEvent(x, y, width, height);
                break;

            case (MotionEvent.ACTION_UP):
                squareModel.handleUpEvent();
                break;

            case (MotionEvent.ACTION_MOVE):
                squareModel.handleMoveEvent(x, y);
                break;
        }

        // Since the above handler methods all affect the view, invalidate to redraw.
        squareView.invalidate();
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        squareModel.nextSize = i;
        squareView.sizeText.setText(i+" x "+i);
    }


    // unused methods that were implemented with the seekbar listener interface

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
