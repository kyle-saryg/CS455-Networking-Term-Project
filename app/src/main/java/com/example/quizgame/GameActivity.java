package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    TextView question = (TextView) findViewById(R.id.question);
    Button buttonA = (Button) findViewById(R.id.buttonA);
    Button buttonB = (Button) findViewById(R.id.buttonB);
    Button buttonC = (Button) findViewById(R.id.buttonC);
    Button buttonD = (Button) findViewById(R.id.buttonD);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // TODO: Send request for questions/answers and parse response from server
        question.setText("This is a placeholder question?");


        buttonA.setText("A");
        buttonA.setOnClickListener(this);


        buttonB.setText("B");
        buttonB.setOnClickListener(this);


        buttonC.setText("C");
        buttonC.setOnClickListener(this);

        buttonD.setText("D");
        buttonD.setOnClickListener(this);

        while (true) {
            String message = "";
            while (received == false) { // Wait for JSON from server
                message = messageFromServer();
            }
            JSONObject gs = null;
            try {
                gs = new JSONObject(message);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (gs == null) { // If server sends a game end signal
                break;
            }
            setGameState(gs);
        }

        endGame(); // Function to clean up and end game

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonA:
                sendResponse("A");
                break;
            case R.id.buttonB:
                sendResponse("B");
                break;
            case R.id.buttonC:
                sendResponse("C");
                break;
            case R.id.buttonD:
                sendResponse("D");
                break;
        }
        // TODO: Send response of answer to server

        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
        buttonD.setEnabled(false);
    }

    private void setGameState(JSONObject gameState) {
        question.setText(gameState.get("question"));
        buttonA.setText(gameState.get("a"));
        buttonB.setText(gameState.get("b"));
        buttonC.setText(gameState.get("c"));
        buttonD.setText(gameState.get("d"));

        buttonA.setEnabled(true);
        buttonB.setEnabled(true);
        buttonC.setEnabled(true);
        buttonD.setEnabled(true);

    }

    private void endGame() {

    }
}