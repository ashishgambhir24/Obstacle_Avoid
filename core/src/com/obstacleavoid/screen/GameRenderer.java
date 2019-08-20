package com.obstacleavoid.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.obstacleavoid.assets.AssetsPath;
import com.obstacleavoid.config.GameConfig;
import com.obstacleavoid.entity.Background;
import com.obstacleavoid.entity.Obstacle;
import com.obstacleavoid.entity.Player;
import com.obstacleavoid.util.GdxUtils;
import com.obstacleavoid.util.ViewportUtils;
import com.obstacleavoid.util.debug.DebugCameraController;

public class GameRenderer implements Disposable {

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;

    private OrthographicCamera hudcamera;
    private Viewport hudviewport;

    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont smallfont;
    private final GlyphLayout layout = new GlyphLayout();
    private final GlyphLayout layout_small = new GlyphLayout();

    private Texture playerTexture;
    private Texture obstacleTexture;
    private Texture backgroundTexture;


    private DebugCameraController debugCameraController;

    private final GameController controller;


    public GameRenderer(GameController controller) {
        this.controller = controller;
        init();
    }

    private void init(){
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        hudcamera = new OrthographicCamera();
        hudviewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudcamera);
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal(AssetsPath.UI_FONT));
        smallfont = new BitmapFont(Gdx.files.internal(AssetsPath.UI_FONT_SMALL));

        playerTexture = new Texture(Gdx.files.internal(AssetsPath.PLAYER_TEXTURE));
        obstacleTexture = new Texture(Gdx.files.internal(AssetsPath.OBSTACLE_TEXTURE));
        backgroundTexture = new Texture(Gdx.files.internal(AssetsPath.BACKGROUND));

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(GameConfig.WORLD_CENTER_X,GameConfig.WORLD_CENTER_Y);

    }

    public void render(float delta){
        debugCameraController.handleDebugInput(delta);
        debugCameraController.applyTo(camera);



        GdxUtils.clearScreen();

        renderGamePlay();

        renderUI();

        renderDebug();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudviewport.update(width,height,true);
        ViewportUtils.debugPixelPerUnit(viewport);
    }


    private void renderGamePlay(){
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Background background = controller.getBackground();
        batch.draw(backgroundTexture , 0,0,background.getWidth(),background.getHeight());

        Player player = controller.getPlayer();
        batch.draw(playerTexture, player.getX() - player.getWidth()/2f, player.getY() - player.getHeight()/2f , player.getWidth(), player.getHeight());

        for(Obstacle obstacle : controller.getObstacles()){
            batch.draw(obstacleTexture,obstacle.getX() - obstacle.getWidth()/2f,obstacle.getY() - obstacle.getHeight()/2f,obstacle.getWidth(),obstacle.getHeight());
        }

        batch.end();
    }

    private void renderUI(){
        hudviewport.apply();
        batch.setProjectionMatrix(hudcamera.combined);
        batch.begin();

        if(controller.isGameOver()){
            String gameOverText = "GAME OVER!!";
            layout.setText(font,gameOverText);
            font.draw(batch, gameOverText,(GameConfig.HUD_WIDTH - layout.width)/2f , (GameConfig.HUD_HEIGHT + layout.height)/2f);

            String gameOverScore = "SCORE: " + controller.getDisplayScore();
            smallfont.draw(batch,gameOverScore , (GameConfig.HUD_WIDTH - layout.width)/2f , (GameConfig.HUD_HEIGHT - layout.height)/2f - 10);

            String highScoreText = "HIGH SCORE: " + controller.getHighScore();
            layout_small.setText(smallfont , highScoreText);
            smallfont.draw(batch , highScoreText , (GameConfig.HUD_WIDTH + layout.width)/2f - layout_small.width,  (GameConfig.HUD_HEIGHT - layout.height)/2f - 10);

        }
        String livesTest = "LIVES: " + controller.getLives();
        layout.setText(font,livesTest);
        font.draw(batch, livesTest, 20, GameConfig.HUD_HEIGHT - layout.height);

        String scoreText = "SCORE: " + controller.getDisplayScore();
        layout.setText(font,scoreText);
        font.draw(batch,scoreText , GameConfig.HUD_WIDTH - layout.width - 20, GameConfig.HUD_HEIGHT - layout.height);

        batch.end();
    }

    private void renderDebug() {

        viewport.apply();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();

        ViewportUtils.drawGrid(viewport, renderer);
    }

    private void drawDebug() {
        renderer.setColor(Color.RED);
        Player player = controller.getPlayer();
        player.drawDebug(renderer);

        renderer.setColor(Color.YELLOW);

        Array<Obstacle> obstacles = controller.getObstacles();
        for (Obstacle obstacle: obstacles){
            obstacle.drawDebug(renderer);
        }
    }



    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
        font.dispose();
        smallfont.dispose();
        playerTexture.dispose();
        obstacleTexture.dispose();
        backgroundTexture.dispose();
    }
}
