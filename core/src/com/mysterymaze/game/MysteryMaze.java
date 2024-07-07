package com.mysterymaze.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Main game class for Mystery Maze game.
 * Initializes resources and manages screen transitions.
 */
public class MysteryMaze extends Game {
	public SpriteBatch batch;   // SpriteBatch for rendering
	public BitmapFont font,font2,font3;// BitmapFont for displaying text

	/**
	 * Called when the application is first created.
	 * Initializes SpriteBatch, loads font from a TrueType (TTF) or OpenType (OTF) file,
	 * and sets up the initial screen to MainMenuScreen.
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();

		// Load font from file and set its size
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Crackman Front.otf")); // Replace with your OTF file
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 36; // Set font size
		font = generator.generateFont(parameter);
		parameter.size = 14;
		font2 = generator.generateFont(parameter);
		parameter.size = 20;
		font3 = generator.generateFont(parameter);

		setScreen(new MainMenuScreen(this)); // Set initial screen to MainMenuScreen
	}

	/**
	 * Renders the current screen.
	 */
	@Override
	public void render() {
		super.render(); // Delegate rendering to the current screen
	}

	/**
	 * Called when the screen size changes, adjusts the projection matrix of the SpriteBatch.
	 *
	 * @param width  New width of the screen
	 * @param height New height of the screen
	 */
	public void resize(int width, int height) {
		// Adjust screen size with an orthographic projection
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	/**
	 * Called when the application is destroyed, disposes resources.
	 */
	@Override
	public void dispose() {
		super.dispose(); // Dispose of resources allocated by Game class
	}
}