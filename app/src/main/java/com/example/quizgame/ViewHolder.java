package com.example.quizgame;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView playerView, scoreView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        playerView = itemView.findViewById(R.id.player);
        scoreView = itemView.findViewById(R.id.score);
    }
}
