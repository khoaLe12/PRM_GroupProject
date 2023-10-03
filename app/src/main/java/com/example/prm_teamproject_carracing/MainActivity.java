package com.example.prm_teamproject_carracing;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer carSound1;
    private int completeCount = 0;
    private ExecutorService executorService;
    private AnimationDrawable thumbAnimation1;
    private AnimationDrawable thumbAnimation2;
    private AnimationDrawable thumbAnimation3;
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private TextView textViewBetMoney1;
    private TextView textViewBetMoney2;
    private TextView textViewBetMoney3;
    private TextView textViewMoneyUser;
    public boolean isStarted = true; // Biến để kiểm tra xem đã bấm "Start" chưa
    private boolean hasWinner = false;
    public final int increment = 2;
    private Button startButton;
    private Button backToBet;
    private User loginUser;
    public int getUserMoney = 0;
    public int getValueBet1 = 0;
    public int getValueBet2 = 0;
    public int getValueBet3 = 0;
    private List<String> winningHorses = new ArrayList<>();
    private int profit = 0;
    private int turn = 1;
    final AtomicInteger completedCount = new AtomicInteger(0); // Biến đếm công việc hoàn thành

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStarted = true;
        hasWinner = false;

        thumbAnimation1 = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_car1);
        thumbAnimation2 = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_car2);
        thumbAnimation3 = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_car3);

        // Gắn AnimationDrawable làm Thumb cho SeekBar
        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);
        seekBar1.setThumb(thumbAnimation1);
        seekBar2.setThumb(thumbAnimation2);
        seekBar3.setThumb(thumbAnimation3);

        seekBar1.setEnabled(false);
        seekBar2.setEnabled(false);
        seekBar3.setEnabled(false);

        textViewBetMoney1 = findViewById(R.id.textViewBetMoney1);
        textViewBetMoney2 = findViewById(R.id.textViewBetMoney2);
        textViewBetMoney3 = findViewById(R.id.textViewBetMoney3);
        textViewMoneyUser = findViewById(R.id.textViewMoney);

        if (getIntent() != null) {
            Intent intent = getIntent();
            String betMoney1 = intent.getStringExtra("betMoney1");
            String betMoney2 = intent.getStringExtra("betMoney2");
            String betMoney3 = intent.getStringExtra("betMoney3");
            loginUser = (User) intent.getSerializableExtra("user");
            getUserMoney = loginUser.getMoney();
            textViewBetMoney1.setText(betMoney1);
            textViewBetMoney2.setText(betMoney2);
            textViewBetMoney3.setText(betMoney3);
            textViewMoneyUser.setText("Money: " + loginUser.getMoney() + "$");
            getValueBet1 = Integer.parseInt(textViewBetMoney1.getText().toString().replace("$", ""));
            getValueBet2 = Integer.parseInt(textViewBetMoney2.getText().toString().replace("$", ""));
            getValueBet3 = Integer.parseInt(textViewBetMoney3.getText().toString().replace("$", ""));
        }

        startRacing(); // Running

        startButton = findViewById(R.id.buttonPlayAgain_Start);
        startButton.setEnabled(false);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đặt biến hasWinner về false khi bấm "Reset"
                hasWinner = false;
                // Đặt biến isStarted thành true khi bấm "Start"
                isStarted = true;
                startButton.setEnabled(false);

                // Đặt giá trị Progress của SeekBar về 0 (hoặc giá trị mặc định)
                seekBar1.setProgress(0);
                seekBar2.setProgress(0);
                seekBar3.setProgress(0);

                // Dừng executorService nếu đang chạy
                if (executorService != null && !executorService.isShutdown()) {
                    executorService.shutdownNow();
                }

                int validateBet = getUserMoney - getValueBet1 - getValueBet2 - getValueBet3;
                if (validateBet < 0) {
                    startButton.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Số dư không đủ để cược", Toast.LENGTH_SHORT).show();
                    textViewBetMoney1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    textViewBetMoney2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    textViewBetMoney3.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    // Chuyển Layout
                } else {
                    // Bắt đầu hoạt hình Thumb
                    thumbAnimation1.start();
                    thumbAnimation2.start();
                    thumbAnimation3.start();
                    // Reset Màu
                    textViewBetMoney1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    textViewBetMoney2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    textViewBetMoney3.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    startRacing(); // Running
                }
            }
        });

        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đặt biến hasWinner về false khi bấm "Reset"
                hasWinner = false;
                isStarted = false;

                // Đặt giá trị Progress của SeekBar về 0 (hoặc giá trị mặc định)
                seekBar1.setProgress(0);
                seekBar2.setProgress(0);
                seekBar3.setProgress(0);

                // Bắt đầu hoạt hình Thumb lại từ đầu (nếu cần)
                thumbAnimation1.stop();
                thumbAnimation2.stop();
                thumbAnimation3.stop();

                startButton.setEnabled(true);

                // Dừng executorService nếu đang chạy
                if (executorService != null && !executorService.isShutdown()) {
                    executorService.shutdownNow();
                }
            }
        });

        backToBet = findViewById(R.id.buttonBackToBet);
        backToBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DatCuocActivity.class);
                intent.putExtra("user", loginUser);
                startActivity(intent);
                carSound1.stop();
                finish();
            }
        });
    }

    private void startRacing() {
        carSound1 = MediaPlayer.create(MainActivity.this, R.raw.car_sound);
        carSound1.setLooping(true);
        carSound1.start();

        hasWinner = false;
        isStarted = true;
        // Khởi tạo hoặc reset executorService (ExecutorService)
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

        executorService = Executors.newFixedThreadPool(3);

        // Bắt đầu thực hiện công việc khi bấm "Start"
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                runFunction1();
            }
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                runFunction2();
            }
        };

        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                runFunction3();
            }
        };

        executorService.submit(runnable1);
        executorService.submit(runnable2);
        executorService.submit(runnable3);
    }

    public void checkCompletion() {
        completeCount += 1;
        if(completeCount == 3){
            carSound1.stop();
            completeCount = 0;
        }
        // Lấy số Tiền
        String[] parts = textViewMoneyUser.getText().toString().split("\\s|\\$");
        Log.d("Horse", "a: " + parts[1]);
        if (completedCount.incrementAndGet() == 2) { // Change to 2
            completedCount.set(0); // Đặt lại biến đếm về 0 để sử dụng cho lần sau
            // Nếu tất cả công việc đã hoàn thành, hiển thị lại nút "Start"
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startButton.setEnabled(true);
                }
            });
        }

        // Dừng executorService nếu đang chạy
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (Integer.parseInt(parts[1]) <= 0) {
            // Sử dụng runOnUiThread để chuyển đổi layout sau một đợi
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Tạo một đợi (delay) 2 giây trước khi chuyển layout
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                            // Pass Data to Result_Activity:
                            intent.putStringArrayListExtra("winningHorses", new ArrayList<>(winningHorses));
                            startActivity(intent);
                        }
                    }, 1000); // 1s
                }
            });
        }


    }

    private void runFunction1() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int maxProgress = seekBar1.getMax();
                while (isStarted) {
                    int currentProgress = seekBar1.getProgress();
                    if (currentProgress >= maxProgress) {
                        // SeekBar đã thắng
                        thumbAnimation1.stop();
                        checkCompletion();
                        if (!hasWinner) {
                            hasWinner = true;
                            getUserMoney = getUserMoney + getValueBet1 - getValueBet2 - getValueBet3;
                            loginUser.setMoney(getUserMoney);
                            textViewMoneyUser.setText("Money: " + getUserMoney + "$");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hiển thị thông báo SeekBar đã thắng
//                                    Toast.makeText(MainActivity.this, "Ngựa ở vị trí số 1 đã thắng!", Toast.LENGTH_SHORT).show();
                                    textViewBetMoney1.setBackgroundColor(getColor(R.color.green));
                                    profit = getValueBet1 - getValueBet2 - getValueBet3;
                                    winningHorses.add("Turn " + turn + " [H1]: " + profit + "$");
                                    turn++;
                                    Log.d("Horse", "Turn " + turn + " [H1]: " + profit + "$");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewBetMoney1.setBackgroundColor(getColor(R.color.red));
                                }
                            });
                        }
                        break;
                    } else {
                        int randomIncrement = 10;
                        // Tăng giá trị Progress
                        seekBar1.setProgress(currentProgress + randomIncrement);
                    }

                    try {
                        Thread.sleep(100); // Delay 100 milliseconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void runFunction2() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int maxProgress = seekBar2.getMax();
                while (isStarted) {
                    int currentProgress = seekBar2.getProgress();
                    if (currentProgress >= maxProgress) {
                        // SeekBar đã thắng
                        thumbAnimation2.stop();
                        checkCompletion();
                        if (!hasWinner) {
                            hasWinner = true;
                            getUserMoney = getUserMoney + getValueBet2 - getValueBet1 - getValueBet3;
                            loginUser.setMoney(getUserMoney);
                            textViewMoneyUser.setText("Money: " + getUserMoney + "$");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hiển thị thông báo SeekBar đã thắng
//                                    Toast.makeText(MainActivity.this, "Ngựa ở vị trí số 2 đã thắng!", Toast.LENGTH_SHORT).show();
                                    textViewBetMoney2.setBackgroundColor(getColor(R.color.green));
                                    profit = getValueBet2 - getValueBet1 - getValueBet3;
                                    winningHorses.add("Turn " + turn + " [H2]: " + profit + "$");
                                    turn++;
                                    Log.d("Horse", "Turn " + turn + " [H2]: " + profit + "$");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewBetMoney2.setBackgroundColor(getColor(R.color.red));
                                }
                            });
                        }
                        break;
                    } else {
                        int randomIncrement = 5;
                        // Tăng giá trị Progress
                        seekBar2.setProgress(currentProgress + randomIncrement);
                    }

                    try {
                        Thread.sleep(100); // Delay 100 milliseconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void runFunction3() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int maxProgress = seekBar3.getMax();
                while (isStarted) {
                    int currentProgress = seekBar3.getProgress();
                    if (currentProgress >= maxProgress) {
                        // SeekBar đã thắng
                        thumbAnimation3.stop();
                        checkCompletion();
                        if (!hasWinner) {
                            hasWinner = true;
                            getUserMoney = getUserMoney + getValueBet3 - getValueBet1 - getValueBet2;
                            loginUser.setMoney(getUserMoney);
                            textViewMoneyUser.setText("Money: " + getUserMoney + "$");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hiển thị thông báo SeekBar đã thắng
//                                    Toast.makeText(MainActivity.this, "Ngựa ở vị trí số 3 đã thắng!", Toast.LENGTH_SHORT).show();
                                    textViewBetMoney3.setBackgroundColor(getColor(R.color.green));
                                    profit = getValueBet3 - getValueBet1 - getValueBet2;
                                    winningHorses.add("Turn " + turn + " [H3]: " + profit + "$");
                                    turn++;
                                    Log.d("Horse", "Turn " + turn + " [H3]: " + profit + "$");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewBetMoney3.setBackgroundColor(getColor(R.color.red));
                                }
                            });
                        }
                        break;
                    } else {
                        int randomIncrement = 5;
                        // Tăng giá trị Progress
                        seekBar3.setProgress(currentProgress + randomIncrement);
                    }

                    try {
                        Thread.sleep(100); // Delay 100 milliseconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    // Methods
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng ExecutorService khi không cần sử dụng nữa
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
