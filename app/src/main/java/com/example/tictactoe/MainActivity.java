package com.example.tictactoe;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playAgainstAI = false;
    private boolean playerXTurn = true;
    private int movesCount = 0;
    private TextView gameStatus;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup modeSelector = findViewById(R.id.modeSelector);
        modeSelector.setOnCheckedChangeListener((group, checkedId) -> {
            playAgainstAI = (checkedId == R.id.aiMode);
            restartGame();
        });

        gameStatus = findViewById(R.id.gameStatus);
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        Button restartButton = findViewById(R.id.restartButton);
        Button historyButton = findViewById(R.id.historyButton);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                String buttonID = "button" + row + col;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[row][col] = findViewById(resID);
                buttons[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGridButtonClick((Button) v);
                    }
                });
            }
        }

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistory(); // показати історію
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void onGridButtonClick(Button button) {
        if (gameOver || !button.getText().toString().isEmpty()) {
            return;
        }

        button.setText(playerXTurn ? "X" : "O");
        movesCount++;

        if (checkForWin()) {
            String result = (playerXTurn ? "Хрестики" : "Нулики") + " виграли!";
            gameStatus.setText(result);
            saveGameResult(result);
            gameOver = true;
            return;
        } else if (movesCount == 9) {
            String result = "Нічия!";
            gameStatus.setText(result);
            saveGameResult(result);
            gameOver = true;
            return;
        }

        playerXTurn = !playerXTurn;

        if (playAgainstAI && !playerXTurn && !gameOver) {
            gameStatus.setText("Хід ШІ...");
            aiMove();
        } else {
            gameStatus.setText(playerXTurn ? "Хрестики, ваш хід!" : "Нулики, ваш хід!");
        }
    }

    private void aiMove() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().toString().isEmpty()) {
                    buttons[row][col].setText("O");
                    movesCount++;

                    if (checkForWin()) {
                        String result = "Нулики виграли!";
                        gameStatus.setText(result);
                        saveGameResult(result);
                        gameOver = true;
                        return;
                    } else if (movesCount == 9) {
                        String result = "Нічия!";
                        gameStatus.setText(result);
                        saveGameResult(result);
                        gameOver = true;
                        return;
                    }

                    playerXTurn = true;
                    gameStatus.setText("Хрестики, ваш хід!");
                    return;
                }
            }
        }
    }


    private boolean checkForWin() {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                    buttons[i][0].getText().equals(buttons[i][2].getText()) &&
                    !buttons[i][0].getText().toString().isEmpty()) {
                return true;
            }
            if (buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                    buttons[0][i].getText().equals(buttons[2][i].getText()) &&
                    !buttons[0][i].getText().toString().isEmpty()) {
                return true;
            }
        }

        if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[0][0].getText().equals(buttons[2][2].getText()) &&
                !buttons[0][0].getText().toString().isEmpty()) {
            return true;
        }
        if (buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[0][2].getText().equals(buttons[2][0].getText()) &&
                !buttons[0][2].getText().toString().isEmpty()) {
            return true;
        }

        return false;
    }

    private void restartGame() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }

        playerXTurn = true;
        movesCount = 0;
        gameStatus.setText("Гра почалася!");
        gameOver = false;
    }

    private void saveGameResult(String result) {
        SharedPreferences prefs = getSharedPreferences("game_history", MODE_PRIVATE);
        String history = prefs.getString("history", "");
        history += result + "\n";
        prefs.edit().putString("history", history).apply();
    }

    private void showHistory() {
        SharedPreferences prefs = getSharedPreferences("game_history", MODE_PRIVATE);
        String history = prefs.getString("history", "Немає історії");

        new android.app.AlertDialog.Builder(this)
                .setTitle("Історія ігор")
                .setMessage(history)
                .setPositiveButton("OK", null)
                .setNegativeButton("Очистити", (dialog, which) -> {
                    prefs.edit().remove("history").apply();
                })
                .show();
    }
}
