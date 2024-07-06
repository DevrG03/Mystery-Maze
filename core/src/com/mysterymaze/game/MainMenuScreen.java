package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Represents the main menu screen of the game.
 */
public class MainMenuScreen implements Screen {
    private final MysteryMaze game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final Texture background;
    private final Texture playButtonActive, playButtonInactive, exitButtonActive, exitButtonInactive;
    private final Rectangle playButtonBounds, exitButtonBounds;
    private final Vector3 touchPoint;
    private final BitmapFont titleFont;

    /**
     * Constructs the main menu screen with necessary resources and elements.
     *
     * @param game The main game instance
     */
    public MainMenuScreen(final MysteryMaze game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // Adjust resolution as needed
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("V01_Tile3.png")); // Replace with your background image
        playButtonActive = new Texture(Gdx.files.internal("flatDark15.png")); // Replace with your play button active image
        playButtonInactive = new Texture(Gdx.files.internal("flatDark15.png")); // Replace with your play button inactive image
        exitButtonActive = new Texture(Gdx.files.internal("flatDark23.png")); // Replace with your exit button active image
        exitButtonInactive = new Texture(Gdx.files.internal("flatDark23.png")); // Replace with your exit button inactive image
        playButtonBounds = new Rectangle(350, 200, playButtonActive.getWidth(), playButtonActive.getHeight()); // Adjust button position
        exitButtonBounds = new Rectangle(350, 100, exitButtonActive.getWidth(), exitButtonActive.getHeight()); // Adjust button position
        touchPoint = new Vector3();

        // Load the TTF font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Crackman Front.otf")); // Replace with your OTF file
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36; // Set font size
        titleFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose to free up resources
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
        batch.setProjectionMatrix(camera.combined);

        // Begin batch and draw background
        batch.begin();
        batch.draw(background, 0, 0, 800, 480); // Adjust background size
        titleFont.draw(batch, "Mystery Maze", 300, 400); // Adjust title position

        // Draw play button based on touch state
        if (playButtonBounds.contains(touchPoint.x, touchPoint.y)) {
            batch.draw(playButtonActive, playButtonBounds.x, playButtonBounds.y);
        } else {
            batch.draw(playButtonInactive, playButtonBounds.x, playButtonBounds.y);
        }

        // Draw exit button based on touch state
        if (exitButtonBounds.contains(touchPoint.x, touchPoint.y)) {
            batch.draw(exitButtonActive, exitButtonBounds.x, exitButtonBounds.y);
        } else {
            batch.draw(exitButtonInactive, exitButtonBounds.x, exitButtonBounds.y);
        }
        batch.end();

        // Check for touch input
        if (Gdx.input.isTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                dispose(); // Dispose resources before switching screen
                game.setScreen(new GameScreen(game)); // Switch to GameScreen
            }
            if (exitButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.exit(); // Exit the game
            }
        }


    }

    @Override
    public void resize(int width, int height) {
        // Adjust screen size
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
        exitButtonActive.dispose();
        exitButtonInactive.dispose();
        titleFont.dispose();
    }
}