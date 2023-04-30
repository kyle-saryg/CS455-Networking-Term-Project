package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private final String SERVER_IP = "192.168.0.20";
    private final int SERVER_PORT = 8000;
    private boolean gameActive;
    Thread clientThread;
    TextView question;
    TextView timer;
    Button buttonA;
    Button buttonB;
    Button buttonC;
    Button buttonD;
    CountDownTimer cdTimer;
    long timeLeft;
    String userAnswer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameActive = true;

        // Start connection to server
        TCPClient client = new TCPClient();
        clientThread = new Thread(client);
        clientThread.start();

        question = findViewById(R.id.question);
        timer = findViewById(R.id.timer);
        timer.setText(String.format("%02d",
                20));
        buttonA = findViewById(R.id.buttonA);
        buttonB = findViewById(R.id.buttonB);
        buttonC = findViewById(R.id.buttonC);
        buttonD = findViewById(R.id.buttonD);

        buttonA.setOnClickListener(this);
        buttonB.setOnClickListener(this);
        buttonC.setOnClickListener(this);
        buttonD.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        JSONObject obj = new JSONObject();
        switch(v.getId()) {
            case R.id.buttonA:
                try {
                    obj.put("choice", 0);
                    obj.put("time", timeLeft);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.buttonB:
                try {
                    obj.put("choice", 1);
                    obj.put("time", timeLeft);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.buttonC:
                try {
                    obj.put("choice", 2);
                    obj.put("time", timeLeft);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.buttonD:
                try {
                    obj.put("choice", 3);
                    obj.put("time", timeLeft);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        userAnswer = obj.toString();

        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
        buttonD.setEnabled(false);
    }

    private void setGameState(String jsonString) throws JSONException {
        JSONObject gameState = new JSONObject(jsonString);

        if (gameState.getBoolean("isLastQuestion")) {
            gameActive = false;
        }

        // Set question
        question.setText(gameState.getString("Question"));

        // Set answers
        JSONArray answers = gameState.getJSONArray("Options");
        buttonA.setText(answers.getString(0));
        buttonB.setText(answers.getString(1));
        buttonC.setText(answers.getString(2));
        buttonD.setText(answers.getString(3));

        // Enable buttons
        buttonA.setEnabled(true);
        buttonB.setEnabled(true);
        buttonC.setEnabled(true);
        buttonD.setEnabled(true);

        // Start timer
        cdTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {
                timeLeft = (l / 1000) % 60;
                timer.setText(String.format("%02d", timeLeft));
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    class TCPClient implements Runnable {
        public PrintWriter out;
        public BufferedReader in;

        String response;

        public void run() {
            try {
                // Try to connect to server
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                // Setup input and output for socket connection
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                try {
                    while (gameActive) {
                        // Get game state JSON from server
                        response = receiveJsonData();
                        setGameState(response);
                        userAnswer = null;
                        // Busy wait for user response
                        while (timeLeft > 0 && userAnswer == null) {
                        }
                        if (timeLeft == 0) {
                            userAnswer = ""; // User didn't answer in time
                        }
                        sendJsonData(userAnswer);
                    }
                } catch (Exception e) {
                } finally {
                    in.close();
                    out.close();
                    socket.close();
                }

            } catch (Exception e) {

            }
        }

        public String receiveJsonData() throws Exception {
            StringBuilder response = new StringBuilder();
            char tmp;
            while ((tmp = (char)in.read()) != '#') {
                response.append(tmp);
            }

            return response.toString();
        }
        public void sendJsonData(String json) throws Exception {
            out.print(json + '#');
            out.flush();
        }
    }
}