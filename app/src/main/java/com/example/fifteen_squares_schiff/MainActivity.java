package com.example.fifteen_squares_schiff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * 15 Squares: a simple puzzle game where you arrange the squares in ascending order.
 *
 * @author **** Henry Schiff ****
 * @version **** September 30th 2022 ****
 *
 * ENHANCEMENTS:
 *
 * - The user can drag the squares to change their positions. Most of the code to perform
 *   this is in the "handle" methods of the SquareModel class.
 *
 * - The puzzle is guaranteed to be solvable. This can be seen in the checkSolubility
 *   and resetBoard methods within the SquareModel class.
 *
 * - You can change the size of the puzzle to be anywhere between 4x4 to 10x10. This is
 *   done by sliding the seekbar and then hitting the reset button.
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views from the layout
        Button resetButton = findViewById(R.id.resetButton);
        SeekBar sizeChanger = findViewById(R.id.sizeChanger);
        TextView sizeText = findViewById(R.id.sizeText);

        // create the View and Controller, and by extension of the view, the Model
        SquareView squareView = findViewById(R.id.squareView);
        SquareController squareController = new SquareController(squareView);

        // pass the size textView to the View so it can update it later
        squareView.setSizeText(sizeText);

        // set listeners for the touchView, reset button and seek bar all to the Controller
        squareView.setOnTouchListener(squareController);
        resetButton.setOnClickListener(squareController);
        sizeChanger.setOnSeekBarChangeListener(squareController);
    }
}