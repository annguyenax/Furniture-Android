package com.example.demococaro;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int winner = -1;
    int startGame = 0;
    Button btPlayAgain, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9;
    TextView txtShowresult;
    int activePlayer = 1; // 1 : player 1 (X), 2 : player 2 (O)
    ArrayList<Integer> player1 = new ArrayList<>();
    ArrayList<Integer> player2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
        btPlayAgain.setOnClickListener(v -> {
            if (startGame == 0) {
                startGame = 1;
                btPlayAgain.setText("Chơi lại");
            } else {
                PlayAgain();
            }
        });
    }

    void PlayAgain() {
        player1.clear();
        player2.clear();
        winner = -1;
        activePlayer = 1;
        
        Button[] buttons = {bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9};
        for (Button bt : buttons) {
            bt.setText("");
            bt.setBackgroundColor(Color.rgb(188, 185, 185));
        }
        
        txtShowresult.setVisibility(View.INVISIBLE);
    }

    void AnhXa() {
        btPlayAgain = findViewById(R.id.btPlayAgain);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);
        bt5 = findViewById(R.id.bt5);
        bt6 = findViewById(R.id.bt6);
        bt7 = findViewById(R.id.bt7);
        bt8 = findViewById(R.id.bt8);
        bt9 = findViewById(R.id.bt9);
        txtShowresult = findViewById(R.id.txtShowresult);
    }

    public void btClick(View view) {
        if (startGame == 0 || winner != -1) return;

        Button btSelected = (Button) view;
        if (!btSelected.getText().toString().isEmpty()) return;

        int cellID = 0;
        int id = btSelected.getId();
        if (id == R.id.bt1) cellID = 1;
        else if (id == R.id.bt2) cellID = 2;
        else if (id == R.id.bt3) cellID = 3;
        else if (id == R.id.bt4) cellID = 4;
        else if (id == R.id.bt5) cellID = 5;
        else if (id == R.id.bt6) cellID = 6;
        else if (id == R.id.bt7) cellID = 7;
        else if (id == R.id.bt8) cellID = 8;
        else if (id == R.id.bt9) cellID = 9;

        if (cellID != 0) {
            PlayGame(cellID, btSelected);
        }
    }

    void PlayGame(int cellID, Button btselected) {
        if (activePlayer == 1) {
            btselected.setText("X");
            btselected.setBackgroundColor(Color.GREEN);
            btselected.setTextColor(Color.RED);
            player1.add(cellID);
            activePlayer = 2;
        } else {
            btselected.setText("O");
            btselected.setTextColor(Color.WHITE);
            btselected.setBackgroundColor(Color.BLUE);
            player2.add(cellID);
            activePlayer = 1;
        }
        CheckWinner();
        if (winner != -1) {
            txtShowresult.setVisibility(View.VISIBLE);
            if (winner == 1) {
                txtShowresult.setText("Player 1 thắng !");
            } else if (winner == 2) {
                txtShowresult.setText("Player 2 thắng !");
            } else if (winner == 0) {
                txtShowresult.setText("Hòa !");
            }
        }
    }

    void CheckWinner() {
        int[][] winCombinations = {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, // Rows
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9}, // Columns
            {1, 5, 9}, {3, 5, 7}             // Diagonals
        };

        for (int[] combo : winCombinations) {
            if (player1.contains(combo[0]) && player1.contains(combo[1]) && player1.contains(combo[2])) {
                winner = 1;
                return;
            }
            if (player2.contains(combo[0]) && player2.contains(combo[1]) && player2.contains(combo[2])) {
                winner = 2;
                return;
            }
        }

        if (player1.size() + player2.size() == 9) {
            winner = 0;
        }
    }
}
