package com.obstacleavoid.config;

public class GameConfig {

    public static final float WIDTH= 480.0f;
    public static final float HEIGHT= 800.0f;

    public static final float HUD_WIDTH = 480.0f;
    public static final float HUD_HEIGHT = 800.0f;

    public static final float WORLD_WIDTH= 6.0f;
    public static final float WORLD_HEIGHT= 10.0f;

    public static final float WORLD_CENTER_X= WORLD_WIDTH/2f;
    public static final float WORLD_CENTER_Y= WORLD_HEIGHT/2f;

    public static final float OBSTACLE_SPAWN_TIME = 0.25f;

    public static final float MAX_PLAYER_X_SPEED = 0.1f;
    public static final float OBSTACLE_Y_SPEED = 0.1f;
    public static final float SCORE_MAX_TIME = 1.0f;

    public static final int LIVES_START = 3;

    private GameConfig() {}
}
