package com.example.fifteen_squares_schiff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button resetButton = findViewById(R.id.resetButton);
        SeekBar sizeChanger = findViewById(R.id.sizeChanger);
        TextView sizeText = findViewById(R.id.sizeText);

        SquareView squareView = findViewById(R.id.squareView);
        SquareController squareController = new SquareController(squareView);

        squareView.setSizeText(sizeText);
        squareView.setOnTouchListener(squareController);
        resetButton.setOnClickListener(squareController);
        sizeChanger.setOnSeekBarChangeListener(squareController);
    }
}