package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen implements Screen {
    private final MysteryMaze game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private Texture playButtonActive;
    private Texture playButtonInactive;
    private Rectangle playButtonBounds;
    private Vector3 touchPoint;

    public MainMenuScreen(final MysteryMaze game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // Adjust resolution as needed
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("V01_Tile3.png")); // Replace with your background image
        playButtonActive = new Texture(Gdx.files.internal("V01_MainCharacter.png")); // Replace with your play button active image
        playButtonInactive = new Texture(Gdx.files.internal("V01_Enemy.png")); // Replace with your play button inactive image
        playButtonBounds = new Rectangle(50, 50, playButtonActive.getWidth(), playButtonActive.getHeight()); // Adjust button position
        touchPoint = new Vector3();
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen for a Game
    }

    @Override
    public void render(float delta) {
        // Clear screen with white color
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set camera projection matrix
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Begin batch and draw background
        game.batch.begin();
        game.batch.draw(background, 0, 0, 800, 480); // Adjust background size

        // Draw play button based on touch state
        if (playButtonBounds.contains(touchPoint.x, touchPoint.y)) {
            game.batch.draw(playButtonActive, 100, 100); // Adjust button position
        } else {
            game.batch.draw(playButtonInactive, 100, 100); // Adjust button position
        }
        game.batch.end();

        // Check for touch input
        if (Gdx.input.isTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                dispose(); // Dispose resources before switching screen
                game.setScreen(new GameScreen(game)); // Switch to GameScreen
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Adjust screen size
        batch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
    }

    @Override
    public void pause() {
        // Called when the application is paused
    }

    @Override
    public void resume() {
        // Called when the application resumes from a paused state
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen for a Game
    }

    @Override
    public void dispose() {
        // Dispose resources
        batch.dispose();
        background.dispose();
        playButtonActive.dispose();
        playButtonInactive.dispose();
    }
}