package com.obstacleavoid.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Pool;
import com.obstacleavoid.config.GameConfig;

public class Obstacle extends GameObjectBase implements Pool.Poolable {

    private static final float BOUNDS_RADIUS = 0.2f; // world units
    public static final float SIZE = 2 * BOUNDS_RADIUS;

    private boolean hit;

    public Obstacle() {
        super(BOUNDS_RADIUS);
    }

    public void update(){
        setPosition(getX(),getY()- GameConfig.OBSTACLE_Y_SPEED);
    }

    public boolean isPlayerColliding(Player player) {
        Circle playerBounds = player.getBounds();
        boolean overlaps =  Intersector.overlaps(playerBounds, getBounds());
        hit = overlaps;

        return overlaps;
    }

    public boolean isNotHit() {return !hit;}

    @Override
    public void reset() {
        hit = false;
    }

    public float getWidth(){ return SIZE; }

    public float getHeight(){ return SIZE; }
}
