package com.mysterymaze.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MysteryMaze extends Game {
	public SpriteBatch batch;
	public BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	public void resize(int width, int height) {
		// Adjust screen size
		batch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}