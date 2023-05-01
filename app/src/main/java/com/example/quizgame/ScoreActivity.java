package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {
    private int[] scores;
    private int id;
    private Button menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scores = extras.getIntArray("scores");
            id = extras.getInt("id");
        }

        Log.i("scores", "Player: " + id);

        List<Score> scoreList = new ArrayList<Score>();
        for (int i = 0; i < scores.length; ++i) {
            if (i == id) {
                scoreList.add(new Score(String.format("Player %d (You)", (i + 1)), scores[i]));
            } else {
                scoreList.add(new Score(String.format("Player %d", (i + 1)), scores[i]));
            }
        }

        RecyclerView recyclerView = findViewById(R.id.score_table);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(getApplicationContext(),scoreList));

        menuButton = findViewById(R.id.menu);
        menuButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent menu = new Intent(this, MainActivity.class);
        startActivity(menu);
    }
}