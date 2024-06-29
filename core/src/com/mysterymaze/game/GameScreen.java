package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.*;

public class GameScreen implements Screen {
    private final MysteryMaze game;
    private SpriteBatch batch;
    private Texture wallTexture1, wallTexture2, floorTexture;
    private Texture characterTexture, enemyTexture;
    private Maze maze;
    private Hero character;
    private static final int CELL_SIZE = 20;
    private int offsetX;
    private int offsetY;
    private java.util.List<Enemy> enemies;
    private boolean paused;
    private boolean justPaused;

    public GameScreen(final MysteryMaze game) {
        this.game = game;
        batch = new SpriteBatch();
        wallTexture1 = new Texture(Gdx.files.internal("V01_Tile1.png"));
        wallTexture2 = new Texture(Gdx.files.internal("V01_Tile2.png"));
        floorTexture = new Texture(Gdx.files.internal("V01_Tile3.png"));
        enemyTexture = new Texture(Gdx.files.internal("V01_Enemy.png"));
        characterTexture = new Texture(Gdx.files.internal("V01_MainCharacter.png"));
        maze = new Maze(21, 21);  // Create a 21x21 maze
        character = new Hero(1, 1);
        enemies = new java.util.ArrayList<>();

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Calculate maze dimensions in pixels
        int mazeWidthInPixels = maze.getWidth() * CELL_SIZE;
        int mazeHeightInPixels = maze.getHeight() * CELL_SIZE;

        // Calculate offset to center the maze on screen
        offsetX = (screenWidth - mazeWidthInPixels) / 2;
        offsetY = (screenHeight - mazeHeightInPixels) / 2;

        placeEnemies(5);

        Gdx.app.log("MysteryMaze", "Maze created");
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
        handlePauseInput();
        // Draw game elements
        if (!paused) {
            batch.begin();
            maze.drawMaze(batch, offsetX, offsetY, CELL_SIZE, wallTexture1, wallTexture2, floorTexture);
            drawCharacter();
            updateAndDrawEnemies();
            batch.end();

            // Handle game logic
            handleInput();
            checkCollisions();
        } else {
            // Draw paused message or menu
            batch.begin();
            // Example: Draw "Game Paused" message
            game.font.draw(batch, "Game Paused", Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
            batch.end();
        }


        // Handle game logic
        handleInput();
        checkCollisions();
    }

    private void placeEnemies(int numberOfEnemies) {
        // Place enemies as before
        Random random = new Random();
        int[][] mazeArray = maze.getMaze();
        int mazeWidth = maze.getWidth();
        int mazeHeight = maze.getHeight();
        Set<int[]> occupiedPositions = new HashSet<>();

        for (int i = 0; i < numberOfEnemies; i++) {
            int x, y;
            do {
                x = random.nextInt(mazeWidth);
                y = random.nextInt(mazeHeight);
            } while (mazeArray[x][y] == 0 || (x == character.getX() && y == character.getY()) || isTooCloseToOthers(x, y, occupiedPositions)); // Ensure the enemy is not placed on a wall, the character, or too close to others

            occupiedPositions.add(new int[]{x, y});
            List<int[]> patrolPath = generatePatrolPath(x, y, mazeArray);
            enemies.add(new Enemy(x, y, patrolPath));
        }
    }

    private boolean isTooCloseToOthers(int x, int y, Set<int[]> occupiedPositions) {
        for (int[] pos : occupiedPositions) {
            int dx = pos[0] - x;
            int dy = pos[1] - y;
            if (dx * dx + dy * dy < 25) { // Adjust distance threshold as needed
                return true;
            }
        }
        return false;
    }

    private List<int[]> generatePatrolPath(int startX, int startY, int[][] mazeArray) {
        List<int[]> path = new ArrayList<>();
        path.add(new int[]{startX, startY});
        Random random = new Random();
        int direction = random.nextInt(4); // 0: up, 1: down, 2: left, 3: right

        int x = startX;
        int y = startY;

        for (int i = 0; i < 5; i++) { // Adjust the length of the patrol path as needed
            switch (direction) {
                case 0: // up
                    if (y < mazeArray[0].length - 1 && mazeArray[x][y + 1] == 1) {
                        y++;
                    } else {
                        direction = random.nextInt(4);
                        continue;
                    }
                    break;
                case 1: // down
                    if (y > 0 && mazeArray[x][y - 1] == 1) {
                        y--;
                    } else {
                        direction = random.nextInt(4);
                        continue;
                    }
                    break;
                case 2: // left
                    if (x > 0 && mazeArray[x - 1][y] == 1) {
                        x--;
                    } else {
                        direction = random.nextInt(4);
                        continue;
                    }
                    break;
                case 3: // right
                    if (x < mazeArray.length - 1 && mazeArray[x + 1][y] == 1) {
                        x++;
                    } else {
                        direction = random.nextInt(4);
                        continue;
                    }
                    break;
            }
            path.add(new int[]{x, y});
        }

        return path;
    }

    private void handleInput() {
        boolean moved = false;
        moved = character.handleInput(maze, moved);
        if (moved) {
            checkCollisions();
        }
    }

    private void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (character.getX() == enemy.getX() && character.getY() == enemy.getY()) {
                onCharacterDeath();
                break;
            }
        }
    }

    private void onCharacterDeath() {
        // For simplicity, let's reset the character's position to the starting point
        character.setPosition(1, 1); // Assuming the start position is (1, 1)

        // Alternatively, you could implement more complex logic, like ending the game or reducing lives
        Gdx.app.log("MysteryMaze", "Hero Died!");
    }

    private void drawCharacter() {
        batch.draw(characterTexture, offsetX + character.getX() * CELL_SIZE, offsetY + (maze.getMaze().length - character.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private void updateAndDrawEnemies() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
            batch.draw(enemyTexture, offsetX + enemy.getX() * CELL_SIZE, offsetY + (maze.getHeight() - enemy.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private void handlePauseInput() {
        // Toggle pause state on key press, e.g., P key
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && !justPaused) {
            paused = !paused;
            justPaused = true;
        } else if (!Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            justPaused = false;
        }
    }

    @Override
    public void resize(int width, int height) {
        // Adjust screen size
        batch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
    }

    public void pause() {
        // Called when the application is paused
        paused = true;
    }

    @Override
    public void resume() {
        // Called when the application resumes from a paused state
        paused = false;
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen for a Game
    }

    @Override
    public void dispose() {
        // Dispose resources
        batch.dispose();
        wallTexture1.dispose();
        wallTexture2.dispose();
        floorTexture.dispose();
        characterTexture.dispose();
        enemyTexture.dispose();
    }
}