package com.obstacleavoid.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.obstacleavoid.config.GameConfig;

/**
 * Created by goran on 23/08/2016.
 */
public class Player extends GameObjectBase{

    private static final float BOUNDS_RADIUS = 0.4f; // world units
    private static final float SIZE = 2 * BOUNDS_RADIUS;



    public Player() {
        super(BOUNDS_RADIUS);
    }



    public void update(){
        float xspeed=0;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            xspeed = -GameConfig.MAX_PLAYER_X_SPEED;
        }else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            xspeed = GameConfig.MAX_PLAYER_X_SPEED;
        }
        setX(getX() + xspeed);
    }



    public float getWidth(){
        return SIZE;
    }
    public float getHeight(){
        return SIZE;
    }
}
