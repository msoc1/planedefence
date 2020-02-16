package com.fixed4fun.planedefence;

import android.content.Context;

public class StrongerEnemy extends Enemy {
    public StrongerEnemy(int health, int enemyID, Context context) {
        super(health*2 ,enemyID, context);
        this.enemyImage = R.drawable.enemy2;
        this.enemyImageView.setImageResource(enemyImage);
    }

    @Override
    public int attack() {
        return -10;
    }
}
