package com.fixed4fun.planedefence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    ImageView plane;
    ImageView enemy;
    TextView currentHealth;
    TextView maxHealth;
    TextView enemyHealth;
    Timer gameTimer;
    Timer baseEnemyShootingTimer;
    Timer strongerEnemyShootingTimer;
    static int enH = 10;
    static int curH = 1000;
    ConstraintLayout main;
    static int width;
    TextView healers;
    int healersNumber = 20;
    TextView shooters;
    int shootersNumber = 20;
    Timer shootersTimer;
    Timer healersTimer;
    int enemiesKilled = 0;
    TextView enemiesKilledTV;
    int enemiesThisRound = 60;


    Button start;
    Button stop;
    static int i = 0;
    static int activeEnemy = 0;
    ArrayList<Enemy> enemies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enemy = findViewById(R.id.enemy);
        plane = findViewById(R.id.plane);
        currentHealth = findViewById(R.id.current_health);
        maxHealth = findViewById(R.id.max_health);
        enemyHealth = findViewById(R.id.enemy_health);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        shooters = findViewById(R.id.shooters_number);
        healers = findViewById(R.id.healers_number);
        main = findViewById(R.id.main);
        enemiesKilledTV = findViewById(R.id.killed_enemies);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        enemyHealth.setVisibility(View.INVISIBLE);
        enemy.setVisibility(View.INVISIBLE);

        shooters.setText(String.valueOf(shootersNumber));
        healers.setText(String.valueOf(healersNumber));

        currentHealth.setText(String.valueOf(curH));

        stop.setOnClickListener(v -> {
            gameTimer.purge();
            gameTimer.cancel();
            for (Enemy e : enemies) {
                e.thisEnemyText.setVisibility(View.GONE);
                e.enemyImageView.setVisibility(View.GONE);
                e.thisEnemyText = null;
                e.enemyImageView = null;
            }
            enemies.clear();

        });


        start.setOnClickListener(v -> {
            prepareEnemies(enemiesThisRound);

            enH = 10;
            enemyHealth.setText(String.valueOf(enH));

            int position = ThreadLocalRandom.current().nextInt(20, 70);
//            enemy.setX((float) position / 100 * width);
//            enemy.setY(ThreadLocalRandom.current().nextInt(100, 200));

//            enemy.setVisibility(View.INVISIBLE);
            enemyHealth.setVisibility(View.VISIBLE);

            gameTimer = new Timer();
            gameTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                    enemy.setY(enemy.getY() + 2);
//                    enemyHealth.setY(enemy.getY() - enemy.getHeight());
//                    enemyHealth.setX(enemy.getX());


                    for (Enemy en : enemies) {
                        if (en.health <= 0) {
                            runOnUiThread(() -> {
                                en.enemyImageView.setVisibility(View.INVISIBLE);
                                en.thisEnemyText.setVisibility(View.INVISIBLE);
                            });
                        } else {
                            en.enemyImageView.setY(en.enemyImageView.getY() + 3);
                            en.thisEnemyText.setY(en.enemyImageView.getY() - 50);
                        }
                    }


                    runOnUiThread(() -> currentHealth.setText(String.valueOf(curH)));

                    if (enemiesKilled == enemiesThisRound) {
                        gameTimer.purge();
                        gameTimer.cancel();


                    }
                }
            }, 0, 20);

            baseEnemyShootingTimer = new Timer();
            baseEnemyShootingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    for (Enemy e : enemies) {
                        if (e.health > 0 && e.enemyImageView.getY() > 50 && e.getClass().equals(Enemy.class)) {
                            curH += e.attack();
                        }
                    }
                }
            }, 0, 2000);

            strongerEnemyShootingTimer = new Timer();
            strongerEnemyShootingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    for (Enemy e : enemies) {
                        if (e.health > 0 && e.enemyImageView.getY() > 50 && e.getClass().equals(StrongerEnemy.class)) {
                            curH += e.attack();
                        }
                    }
                }
            }, 0, 3500);

            healersTimer = new Timer();
            healersTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (curH + 1 <= Integer.valueOf(maxHealth.getText().toString()))
                        curH += 3;


                }
            }, 0, 1000 / healersNumber);

            shootersTimer = new Timer();
            shootersTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    for (int i = 0; i < enemies.size(); i++) {
                        if (enemies.get(i).health > 0) {
                            activeEnemy = i;
                            break;
                        }
                    }


                    if (activeEnemy < enemiesThisRound) {
                        if (enemies.get(activeEnemy).enemyImageView.getY() > 300) {
                            if (!enemies.get(activeEnemy).enemyImageView.isShown()) {
                                activeEnemy++;
                            }
                            if (enemies.get(activeEnemy).health > 0) {
                                enemies.get(activeEnemy).health--;
                                if (enemies.get(activeEnemy).health == 0) {
                                    enemiesKilled++;
                                    Log.d("123456", "run: " + enemiesKilled);
                                    runOnUiThread(() -> enemiesKilledTV.setText(String.valueOf(enemiesKilled)));
                                    activeEnemy++;
//                                    break;
                                } else {
                                    runOnUiThread(() -> {
                                        try {
                                            enemies.get(activeEnemy).thisEnemyText.setText(String.valueOf(enemies.get(activeEnemy).health));
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            }
                        }
                    }


                }
            }, 5000, 2000 / shootersNumber);

        });


        enemy.setOnClickListener(v -> {
            enH--;
            enemyHealth.setText(String.valueOf(enH));

        });
    }

    private void prepareEnemies(int enemiesNumber) {
        for (i = 0; i < enemiesNumber; i++) {
            if (i % 2 == 1) {
                enemies.add(new Enemy(ThreadLocalRandom.current().nextInt(2, 4), i, getApplicationContext()));
            } else {
                enemies.add(new StrongerEnemy(ThreadLocalRandom.current().nextInt(2, 4), i, getApplicationContext()));

            }
            main.addView(enemies.get(i).enemyImageView);
            main.addView(enemies.get(i).thisEnemyText);
            enemies.get(i).enemyImageView.setY(-i * 50);
            int pox = ThreadLocalRandom.current().nextInt(10, 95);
            enemies.get(i).enemyImageView.setX((float) pox / 100 * width);
            enemies.get(i).thisEnemyText.setY(enemies.get(i).enemyImageView.getY() - 50);
            enemies.get(i).thisEnemyText.setX(enemies.get(i).enemyImageView.getX());
        }

        for (Enemy e : enemies) {
            e.enemyImageView.setOnClickListener(v -> {
                e.health = e.health - 10;
                if (e.health <= 0) {
                    enemiesKilled++;
                    enemiesKilledTV.setText(String.valueOf(enemiesKilled));
                }
                e.thisEnemyText.setText(String.valueOf(e.health));
            });
        }
    }


}
