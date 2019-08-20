package com.obstacleavoid.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Background;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;

public class GameController {
    private static final Logger log = new Logger(GameController.class.getName(), Logger.DEBUG);

    private Player player;
    private final float startPlayerX = GameConfig.WORLD_WIDTH / 2f;

    private final float startPlayerY = 1;

    private Background background;

    private Array<Obstacle> obstacles = new Array<>();
    private float obstacleTimer;
    private float scoreTimer;

    private int lives = GameConfig.LIVES_START;
    private int score = 0;
    private int displayScore;
    private int highScore=0;

    private Pool<Obstacle> obstaclePool;

    public GameController(){
        init();
    }

    private void init(){
        // create player
        player = new Player();

        // calculate position


        // position player
        player.setPosition(startPlayerX, startPlayerY);

        // obstacle pool
        obstaclePool = Pools.get(Obstacle.class, 40);

        background = new Background();
        background.setPosition(0,0);
        background.setSize(GameConfig.WORLD_WIDTH , GameConfig.WORLD_HEIGHT);
    }

    public void update (float delta){

        if(isGameOver()){
            playAgain();
        }else {
            updatePlayer();
            updateObstacles(delta);
            updateScore(delta);
            updateDisplayScore(delta);

            if (isPlayerCollidingWithObstacle()) {
                log.debug("Collision Detected:");
                lives--;

                if (isGameOver()) {
                    log.debug("GAME OVER!!");
                } else {
                    restart();
                }
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getLives() {
        return lives;
    }

    public int getDisplayScore() {
        return displayScore;
    }

    public Background getBackground(){
        return background;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isGameOver(){
        return lives<=0;
    }

    private void updatePlayer(){
//        log.debug("playerX = " + player.getX() + " player Y = " + player.getY());
        player.update();
        blockPlayerFromLeaving();
    }

    private void blockPlayerFromLeaving(){
        float playerX = MathUtils.clamp(player.getX(), player.getWidth()/2f, GameConfig.WORLD_WIDTH - player.getWidth()/2f);

        player.setPosition(playerX, player.getY());
    }

    private void updateObstacles(float delta){
        for(Obstacle obstacle : obstacles){
            obstacle.update();
        }
        createNewObstacle(delta);
        removePassedObstacle();
    }

    private void createNewObstacle(float delta){
        obstacleTimer +=delta;
        if(obstacleTimer>=GameConfig.OBSTACLE_SPAWN_TIME){

            float min = Obstacle.SIZE/2f;
            float max = GameConfig.WORLD_WIDTH - Obstacle.SIZE/2f;
            float obstacleX = MathUtils.random(min,max);
            float obstacleY = GameConfig.WORLD_HEIGHT;

            Obstacle obstacle = obstaclePool.obtain();


            obstacle.setPosition(obstacleX,obstacleY);
            obstacles.add(obstacle);
            obstacleTimer=0;
        }
    }

    private void removePassedObstacle(){
        if(obstacles.size>0){
            Obstacle first = obstacles.first();

            float minObstacleY = -Obstacle.SIZE/2f;

            if(first.getY() < minObstacleY){
                obstacles.removeValue(first, true);
                obstaclePool.free(first);
            }
        }
    }

    private boolean isPlayerCollidingWithObstacle(){
        for(Obstacle obstacle : obstacles){
            if(obstacle.isNotHit() &&  obstacle.isPlayerColliding(player)){
                return true;
            }
        }

        return false;
    }

    private void updateScore(float delta){
        scoreTimer += delta;

        if(scoreTimer>=GameConfig.SCORE_MAX_TIME){
            score += MathUtils.random(1,5);
            scoreTimer = 0.0f;
        }
    }

    private void updateDisplayScore(float delta){
        if(displayScore<score){
            displayScore = Math.min(score, displayScore + (int) (60 * delta));
        }
        if(highScore<displayScore){
            highScore = displayScore;
        }
    }

    private void restart(){
        obstaclePool.freeAll(obstacles);
        obstacles.clear();
        player.setPosition(startPlayerX , startPlayerY);
    }

    private void playAgain(){
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            lives = GameConfig.LIVES_START;
            score = 0;
            displayScore=0;
            restart();
        }
    }
}
