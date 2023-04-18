package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // TODO: Send request for questions/answers and parse response from server
        TextView question = (TextView) findViewById(R.id.question);
        question.setText("This is a placeholder question?");

        Button buttonA = (Button) findViewById(R.id.buttonA);
        buttonA.setText("A");
        buttonA.setOnClickListener(this);

        Button buttonB = (Button) findViewById(R.id.buttonB);
        buttonB.setText("B");
        buttonB.setOnClickListener(this);

        Button buttonC = (Button) findViewById(R.id.buttonC);
        buttonC.setText("C");
        buttonC.setOnClickListener(this);

        Button buttonD = (Button) findViewById(R.id.buttonD);
        buttonD.setText("D");
        buttonD.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonA:
                break;
            case R.id.buttonB:
                break;
            case R.id.buttonC:
                break;
            case R.id.buttonD:
                break;
        }
        // TODO: Send response of answer to server

        Button buttonA = (Button) findViewById(R.id.buttonA);
        buttonA.setEnabled(false);

        Button buttonB = (Button) findViewById(R.id.buttonB);
        buttonB.setEnabled(false);

        Button buttonC = (Button) findViewById(R.id.buttonC);
        buttonC.setEnabled(false);

        Button buttonD = (Button) findViewById(R.id.buttonD);
        buttonD.setEnabled(false);
    }
}