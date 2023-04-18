package com.example.quizgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createRoom = (Button) findViewById(R.id.button);
        createRoom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                Intent createRoom = new Intent(this, GameActivity.class);
                startActivity(createRoom);
                break;
            case R.id.button3:
                Intent joinRoom = new Intent(this, GameActivity.class);
                startActivity(joinRoom);
                break;
        }
    }
}