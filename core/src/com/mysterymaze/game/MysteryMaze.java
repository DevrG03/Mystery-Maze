package com.mysterymaze.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MysteryMaze extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture wallTexture1, wallTexture2;
	private Texture floorTexture;
	private Texture characterTexture;
	private Maze maze;
	private Hero character;
	private static final int CELL_SIZE = 20;
	private int offsetX;
	private int offsetY;
	private float timeSinceLastMove = 0f;

	@Override
	public void create() {
		Gdx.app.log("MysteryMaze", "Creating application");
		batch = new SpriteBatch();
		wallTexture1 = new Texture(Gdx.files.internal("V01_Tile1.png"));
		wallTexture2 = new Texture(Gdx.files.internal("V01_Tile2.png"));
		floorTexture = new Texture(Gdx.files.internal("V01_Tile3.png"));
		characterTexture = new Texture(Gdx.files.internal("V01_MainCharacter.png"));
		maze = new Maze(21, 21);  // Create a 21x21 maze
		character = new Hero(1, 1);

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		// Calculate maze dimensions in pixels
		int mazeWidthInPixels = maze.getWidth() * CELL_SIZE;
		int mazeHeightInPixels = maze.getHeight() * CELL_SIZE;

		// Calculate offset to center the maze on screen
		offsetX = (screenWidth - mazeWidthInPixels) / 2;
		offsetY = (screenHeight - mazeHeightInPixels) / 2;
		Gdx.app.log("MysteryMaze", "Maze created");
	}

	@Override
	public void render() {
		handleInput(Gdx.graphics.getDeltaTime());
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		drawMaze();
		drawCharacter();
		batch.end();
	}

	private void handleInput(float deltaTime) {
		timeSinceLastMove += deltaTime;
		// Movement delay in seconds
		float movementCooldown = 0.2f;
		if (timeSinceLastMove >= movementCooldown) {
			if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
				if (maze.getMaze()[character.getX()][character.getY() - 1] == 1) {
					character.moveDown();
					timeSinceLastMove = 0f;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
				if (maze.getMaze()[character.getX()][character.getY() + 1] == 1) {
					character.moveUp();
					timeSinceLastMove = 0f;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
				if (maze.getMaze()[character.getX() - 1][character.getY()] == 1) {
					character.moveLeft();
					timeSinceLastMove = 0f;
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
				if (maze.getMaze()[character.getX() + 1][character.getY()] == 1) {
					character.moveRight();
					timeSinceLastMove = 0f;
				}
			}
		}
	}

	private void drawMaze() {
		int[][] mazeArray = maze.getMaze();
		for (int y = 0; y < mazeArray.length; y++) {
			for (int x = 0; x < mazeArray[y].length; x++) {
				if (mazeArray[x][y] == 0) {  // Draw wall
					if (maze.hasFloorBelow(x, y)) {
						batch.draw(wallTexture2, offsetX +x * CELL_SIZE, offsetY +(mazeArray.length - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
					} else {
						batch.draw(wallTexture1, offsetX + x * CELL_SIZE, offsetY + (mazeArray.length - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
						// Draw a different wall texture here for no wall below
						// Example: batch.draw(differentWallTexture, x * CELL_SIZE, (mazeArray.length - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
					}
				} else {  // Draw path
					batch.draw(floorTexture, offsetX + x * CELL_SIZE, offsetY + (mazeArray.length - y - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				}
			}
		}
	}

	private void drawCharacter() {
		batch.draw(characterTexture, offsetX + character.getX() * CELL_SIZE, offsetY + (maze.getMaze().length - character.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
	}

	@Override
	public void dispose() {
		Gdx.app.log("MysteryMaze", "Disposing resources");
		batch.dispose();
		wallTexture1.dispose();
		wallTexture1.dispose();
		floorTexture.dispose();
		characterTexture.dispose();
	}
}