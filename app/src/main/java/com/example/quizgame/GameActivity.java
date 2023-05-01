package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar spinner;
    private TextView loading;
    private TextView question;
    private TextView timer;
    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;
    private CountDownTimer cdTimer;
    private int counter = 0;
    private TCPClient client;
    private int userId;

    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        client = new TCPClient(executor);
        executor.execute(client);

        question = findViewById(R.id.question);
        timer = findViewById(R.id.timer);
        loading = findViewById(R.id.loading);

        spinner = findViewById(R.id.spinner);
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
                    obj.put("time", counter);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.buttonB:
                try {
                    obj.put("choice", 1);
                    obj.put("time", counter);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.buttonC:
                try {
                    obj.put("choice", 2);
                    obj.put("time", counter);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.buttonD:
                try {
                    obj.put("choice", 3);
                    obj.put("time", counter);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        try {
            client.sendJsonData(obj.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
        buttonD.setEnabled(false);

        // Stop the timer
        cdTimer.cancel();

    }

    private void setGameState(String jsonString) throws JSONException {
        JSONObject gameState = new JSONObject(jsonString);

        // Set userID
        userId = gameState.optInt("id");

        String q = gameState.getString("Question");
        if (q.equals("Start Game")) {
            // Remove connecting layout and display game layout
            spinner.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            timer.setVisibility(View.VISIBLE);
            question.setVisibility(View.VISIBLE);
            buttonA.setVisibility(View.VISIBLE);
            buttonB.setVisibility(View.VISIBLE);
            buttonC.setVisibility(View.VISIBLE);
            buttonD.setVisibility(View.VISIBLE);
            return;
        } else if (q.equals("End Game")) {
            // Change to score activity when game ends
            client.endGame();
            Intent end = new Intent(this, ScoreActivity.class);
            JSONArray jsonScores = gameState.getJSONArray("Scores");
            int[] scores = new int[jsonScores.length()];
            for (int i = 0; i < jsonScores.length(); ++i) {
                scores[i] = jsonScores.optInt(i);
            }
            end.putExtra("scores", scores);
            end.putExtra("id", userId);
            startActivity(end);
            return;
        }
        // Set question
        question.setText(q);

        // Set answers
        JSONArray answers = gameState.getJSONArray("Selections");
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
            public void onTick(long ms) {
                if (Math.round((float)ms / 1000.0f) != counter) {
                    counter = Math.round((float)ms / 1000.0f);
                    timer.setText(String.valueOf(Math.round((float)ms / 1000.0f)));
                }
                Log.i("timer", "ms=" + ms + " til finished=" + counter);
            }

            @Override
            public void onFinish() {
                timer.setText("0");
                // If user didn't select an option in time
                JSONObject obj = new JSONObject();
                try {
                    obj.put("choice", -1);
                    obj.put("time", counter);
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    client.sendJsonData(obj.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                buttonA.setEnabled(false);
                buttonB.setEnabled(false);
                buttonC.setEnabled(false);
                buttonD.setEnabled(false);
            }
        }.start();

    }

    class TCPClient implements Runnable {
        private final String SERVER_IP = "192.168.0.11";
        private final int SERVER_PORT = 8000;
        private boolean gameActive;
        public PrintWriter out;
        public BufferedReader in;
        private final ExecutorService runner;

        String response;

        public TCPClient(ExecutorService runner) {
            this.runner = runner;
        }
        public void run() {
            gameActive = true;

            Log.i("connect", "Connecting...");
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
                        Log.i("server", "Server Response: " + response);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setGameState(response);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("connect", "Error: " + e);
                } finally {
                    Log.i("close", "Closing socket...");
                    in.close();
                    out.close();
                    socket.close();
                }

            } catch (Exception e) {
                Log.e("connect", "Error: " + e);
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
        public void sendJsonData(String json) {
            Log.i("send", "Sending to server: " + json);
            runner.execute(new Runnable() {
                @Override
                public void run() {
                    out.print(json + '#');
                    out.flush();
                }
            });
        }

        public void endGame() {
            Log.i("end", "Ending Game...");
            runner.execute(new Runnable() {
                @Override
                public void run() {
                    gameActive = false;
                }
            });
        }
    }
}