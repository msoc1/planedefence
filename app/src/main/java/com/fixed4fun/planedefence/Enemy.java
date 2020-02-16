package com.fixed4fun.planedefence;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class Enemy {

    int health;
    int enemyImage;
    int enemyID;
    ImageView enemyImageView;
    TextView thisEnemyText;

    public Enemy(int health, int enemyID, Context context) {
        this.enemyImage = R.drawable.enemy_image;
        this.health = health;
        this.enemyID = enemyID;
        this.enemyImageView = new ImageView(context);
        this.enemyImageView.setId(enemyID);
        this.enemyImageView.setImageResource(enemyImage);
        thisEnemyText = new TextView(context);
        thisEnemyText.setText(String.valueOf(health));
    }

    public int attack(){
        return -5;
    }


}
