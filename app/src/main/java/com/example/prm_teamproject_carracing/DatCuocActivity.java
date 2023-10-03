package com.example.prm_teamproject_carracing;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class DatCuocActivity extends Activity{
    InMemoryData data;
    private Button btnBetDone;
    private TextView textViewMoneyUser;
    private Button btnUp1;
    private Button btnUp2;
    private Button btnUp3;
    private Button btnDown1;
    private Button btnDown2;
    private Button btnDown3;
    private Button btnBack;
    private TextView textViewBetMoney1;
    private TextView textViewBetMoney2;
    private TextView textViewBetMoney3;
    private User loginUser;
    private int valueBetMoney = 10; // Coin Radio
    private int totalBetMoney = 0; // Tổng tiền cược
    public int getValueBet1 = 0;
    public int getValueBet2 = 0;
    public int getValueBet3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datcuoc_layout);

        data = InMemoryData.getInstance();

        MediaPlayer background = MediaPlayer.create(DatCuocActivity.this, R.raw.background);
        background.start();

        // Get login user
        Intent intent = getIntent();
        loginUser = (User) intent.getSerializableExtra("user");

        textViewMoneyUser = findViewById(R.id.textViewMoneyUser);
        textViewMoneyUser.setText("Money: " + loginUser.getMoney() + "$");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Login Successfully
                Toast.makeText(DatCuocActivity.this, "Chào mừng " + loginUser.getUsername(), Toast.LENGTH_SHORT).show();
            }
        });

        btnUp1 = findViewById(R.id.buttonUp1);
        btnUp2 = findViewById(R.id.buttonUp2);
        btnUp3 = findViewById(R.id.buttonUp3);

        btnDown1 = findViewById(R.id.buttonDown1);
        btnDown2 = findViewById(R.id.buttonDown2);
        btnDown3 = findViewById(R.id.buttonDown3);
        btnBack = findViewById(R.id.buttonBack);

        textViewBetMoney1 = findViewById(R.id.textViewMoney1);
        textViewBetMoney2 = findViewById(R.id.textViewMoney2);
        textViewBetMoney3 = findViewById(R.id.textViewMoney3);

        getValueBet1 = Integer.parseInt(textViewBetMoney1.getText().toString().replace("$", ""));
        getValueBet2 = Integer.parseInt(textViewBetMoney2.getText().toString().replace("$", ""));
        getValueBet3 = Integer.parseInt(textViewBetMoney3.getText().toString().replace("$", ""));

        String[] parts = textViewMoneyUser.getText().toString().split("\\s|\\$");

        // Sau khi đăng nhập thành công
        int currentUserMoney = Integer.parseInt(parts[1]);
        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate: khi Bet thêm -> 0 đc quá với Current Money
                updateTotalBetMoney();
                if ((totalBetMoney + valueBetMoney) <= currentUserMoney) {
                    getValueBet1 += valueBetMoney;
                    textViewBetMoney1.setText(getValueBet1 + "$");

                    Log.d("Horse", String.valueOf(totalBetMoney + valueBetMoney));
                } else {
                    Toast.makeText(DatCuocActivity.this, "Không đủ tiền cược!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate: khi Bet thêm -> 0 đc quá với Current Money
                updateTotalBetMoney();
                if ((totalBetMoney + valueBetMoney) <= currentUserMoney) {
                    getValueBet2 += valueBetMoney;
                    textViewBetMoney2.setText(getValueBet2 + "$");
//                    Log.d("Horse", String.valueOf(totalBetMoney + valueBetMoney));
                } else {
                    Toast.makeText(DatCuocActivity.this, "Không đủ tiền cược!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate: khi Bet thêm -> 0 đc quá với Current Money
                updateTotalBetMoney();
                if ((totalBetMoney + valueBetMoney) <= currentUserMoney) {
                    getValueBet3 += valueBetMoney;
                    textViewBetMoney3.setText(getValueBet3 + "$");
//                    Log.d("Horse", String.valueOf(totalBetMoney + valueBetMoney));
                } else {
                    Toast.makeText(DatCuocActivity.this, "Không đủ tiền cược!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDown1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int getMoney1 = (getValueBet1 -= valueBetMoney);
                if (getMoney1 >= 0) {
                    textViewBetMoney1.setText(getMoney1 + "$");
                }
            }
        });

        btnDown2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int getMoney2 = (getValueBet2 -= valueBetMoney);
                if (getMoney2 >= 0) {
                    textViewBetMoney2.setText(getMoney2 + "$");
                }
            }
        });

        btnDown3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int getMoney3 = (getValueBet3 -= valueBetMoney);
                if (getMoney3 >= 0) {
                    textViewBetMoney3.setText(getMoney3 + "$");
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                background.stop();
                data.setMoney(loginUser.getMoney());
                finish();
            }
        });

        btnBetDone = findViewById(R.id.buttonBetDone);
        btnBetDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDC = new Intent(DatCuocActivity.this, MainActivity.class);
                intentDC.putExtra("betMoney1", textViewBetMoney1.getText().toString());
                intentDC.putExtra("betMoney2", textViewBetMoney2.getText().toString());
                intentDC.putExtra("betMoney3", textViewBetMoney3.getText().toString());
                intentDC.putExtra("user", loginUser);
                startActivity(intentDC);
                background.stop();
                finish();
            }
        });
    }

    private void updateTotalBetMoney() {
        int betMoney1 = Integer.parseInt(textViewBetMoney1.getText().toString().replace("$", ""));
        int betMoney2 = Integer.parseInt(textViewBetMoney2.getText().toString().replace("$", ""));
        int betMoney3 = Integer.parseInt(textViewBetMoney3.getText().toString().replace("$", ""));
        totalBetMoney = betMoney1 + betMoney2 + betMoney3;
    }
}
