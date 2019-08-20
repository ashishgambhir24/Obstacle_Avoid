package com.obstacleavoid.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obstacleavoid.assets.AssetsPath;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;
import com.obstacleavoid.util.GdxUtils;
import com.obstacleavoid.util.ViewportUtils;
import com.obstacleavoid.util.debug.DebugCameraController;

/**...*/
@Deprecated
public class gameScreenOld implements Screen {


    private static final Logger log = new Logger(gameScreenOld.class.getName(), Logger.DEBUG);

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private OrthographicCamera hudcamera;
    private Viewport hudviewport;

    private SpriteBatch batch;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    private Player player;

    private Array<Obstacle> obstacles = new Array<>();
    private float obstacleTimer;
    private float scoreTimer;

    private int lives = GameConfig.LIVES_START;
    private int score = 0;
    private int displayScore;

    private DebugCameraController debugCameraController;



    @Override
    public void show () {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        hudcamera = new OrthographicCamera();
        hudviewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudcamera);
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal(AssetsPath.UI_FONT));

        // create player
        player = new Player();

        // calculate position
        float startPlayerX = GameConfig.WORLD_WIDTH / 2f;

        float startPlayerY = 1;

        // position player
        player.setPosition(startPlayerX, startPlayerY);

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X,GameConfig.WORLD_CENTER_Y);

    }

    @Override
    public void render (float delta) {
//        to move/zoom camera
        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);

        update(delta);


        GdxUtils.clearScreen();

        renderUI();

        renderDebug();

    }

    private void update (float delta){

         if(isGameOver()){
             log.debug("GAME OVER!!");
             return ;
         }
         updatePlayer();
         updateObstacles(delta);
         updateScore(delta);
         updateDisplayScore(delta);

         if(isPlayerCollidingWithObstacle()){
             log.debug("Collision Detected:");
             lives--;
         }
    }

    private boolean isGameOver(){
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
    }

    private void createNewObstacle(float delta){
        obstacleTimer +=delta;
        if(obstacleTimer>=GameConfig.OBSTACLE_SPAWN_TIME){
            float min = 0f;
            float max = GameConfig.WORLD_WIDTH;
            float obstacleX = MathUtils.random(min,max);
            float obstacleY = GameConfig.WORLD_HEIGHT;

            Obstacle obstacle = new Obstacle();
            obstacle.setPosition(obstacleX,obstacleY);
            obstacles.add(obstacle);
            obstacleTimer=0;
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

    private void renderUI(){
        batch.setProjectionMatrix(hudcamera.combined);
        batch.begin();

        String livesTest = "LIVES: " + lives;
        layout.setText(font,livesTest);
        font.draw(batch, livesTest, 20, GameConfig.HUD_HEIGHT - layout.height);

        String scoreText = "SCORE: " + displayScore;
        layout.setText(font,scoreText);
        font.draw(batch,scoreText , GameConfig.HUD_WIDTH - layout.width - 20, GameConfig.HUD_HEIGHT - layout.height);

        batch.end();
    }


    private void renderDebug() {

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();

        ViewportUtils.drawGrid(viewport, renderer);
    }

    private void drawDebug() {
        renderer.setColor(Color.RED);
        player.drawDebug(renderer);

        renderer.setColor(Color.YELLOW);

        for (Obstacle obstacle:obstacles){
            obstacle.drawDebug(renderer);
        }
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
    }

    @Override
    public void dispose () {
        renderer.dispose();
        batch.dispose();
        font.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudviewport.update(width,height,true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }
}
