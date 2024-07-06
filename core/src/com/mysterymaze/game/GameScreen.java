package com.mysterymaze.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;

/**
 * Represents the main game screen where the maze, character, and enemies are displayed and controlled.
 */
public class GameScreen implements Screen {
    private final MysteryMaze game;
    private final SpriteBatch batch;
    private final Texture wallTexture1, wallTexture2, floorTexture;
    private final Texture characterTexture, enemyTexture;
    private final Texture doorTexture, keyTexture;
    private final Maze maze;
    private final Hero character;
    private static final int CELL_SIZE = 20;
    private final java.util.List<Enemy> enemies;
    private boolean paused;
    private boolean justPaused;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Texture bombTexture, flashTexture, coinTexture, thornTexture;
    List<Bomb> bombs;
    private boolean gameFinished = false;
    private float finishTimer = 0f;
    private  int bombScore = 0;
    // HUD variables
    private float timer;
    private int points;
    private int lives;
    private boolean gameOver;

    /**
     * Constructs the game screen with necessary textures, maze, character, and enemies.
     *
     * @param game The main game instance
     */
    public GameScreen(final MysteryMaze game) {
        this.game = game;
        doorTexture = new Texture(Gdx.files.internal("V01_Door.png"));
        keyTexture = new Texture(Gdx.files.internal("V01_Key.png"));
        batch = new SpriteBatch();
        wallTexture1 = new Texture(Gdx.files.internal("V01_Tile1.png"));
        wallTexture2 = new Texture(Gdx.files.internal("V01_Tile2.png"));
        floorTexture = new Texture(Gdx.files.internal("V01_Tile3.png"));
        enemyTexture = new Texture(Gdx.files.internal("V01_Enemy.png"));
        characterTexture = new Texture(Gdx.files.internal("V01_MainCharacter.png"));
        bombTexture = new Texture(Gdx.files.internal("Bomb.png"));
        flashTexture = new Texture(Gdx.files.internal("V01_Bomb_Flash.png"));
        coinTexture = new Texture(Gdx.files.internal("V01_Coin.png"));
        thornTexture = new Texture(Gdx.files.internal("V01_Obstacle.png"));
        maze = new Maze(21, 21);  // Create a 21x21 maze
        character = new Hero(1, 1);
        enemies = new ArrayList<>();
        bombs = new ArrayList<>();

        // Initialize the camera and viewport with logical size
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();

        // Center the camera
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        placeEnemies(5);

        // Initialize HUD variables
        timer = 0;
        points = 0;
        lives = 3;  // For example, the player starts with 3 lives
        gameOver = false;

        Gdx.app.log("MysteryMaze", "Maze created");
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen for a Game
    }

    @Override
    public void render(float delta) {
        // Clear screen with black color
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handlePauseInput();
        handleRestartInput();

        // Update the camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        viewport.apply();  // Apply the viewport

        // Draw game elements
        if (gameFinished) {
            // Display "Game Finished" message
            batch.begin();
            finishTimer += delta;
            if (finishTimer >= 1f) {
                // Add logic to finish the game
                System.out.println("Game Finished!");
                game.font2.draw(batch, "Game Finished", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                // You can call a method to show the game finished screen or perform any other actions
            }
            batch.end();
        }
        else if (!paused && !gameOver) {
            timer += delta;
            batch.begin();
            // Center the maze drawing
            float offsetX = (viewport.getWorldWidth() - maze.getWidth() * CELL_SIZE) / 2;
            float offsetY = (viewport.getWorldHeight() - maze.getHeight() * CELL_SIZE) / 2;
            maze.drawMaze(batch, (int) offsetX, (int) offsetY, CELL_SIZE, wallTexture1, wallTexture2, floorTexture, doorTexture, keyTexture, coinTexture, thornTexture);
            drawCharacter(offsetX, offsetY);
            updateAndDrawEnemies(offsetX, offsetY);
            // Handle game logic
            handleInput();
            checkCollisions();
            // Update and draw bombs
            drawBomb(offsetX,offsetY,delta);
            batch.end();
        } else {
            // Draw paused or game over message
            batch.begin();
            String message = gameOver ? "Game Over" : "Game Paused";
            game.font.draw(batch, message, (float) Gdx.graphics.getWidth() / 2 - 100, (float) Gdx.graphics.getHeight() / 2);
            batch.end();
        }



        // Draw HUD elements
        batch.begin();
        game.font2.draw(batch, "Time: " + formatTime(timer), 10, viewport.getWorldHeight() - 10);
        game.font2.draw(batch, "Points: " + character.getScore() + bombScore, 10, viewport.getWorldHeight() - 30);
        game.font2.draw(batch, "Lives: " + lives, 10, viewport.getWorldHeight() - 50);
        batch.end();
    }

    /**
     * Places a specified number of enemies in valid positions within the maze.
     *
     * @param numberOfEnemies The number of enemies to place in the maze
     */
    private void placeEnemies(int numberOfEnemies) {
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
            } while (mazeArray[x][y] == 0 || (x == character.getX() && y == character.getY()) || isTooCloseToOthers(x, y, occupiedPositions));

            occupiedPositions.add(new int[]{x, y});
            List<int[]> patrolPath = generatePatrolPath(x, y, mazeArray);
            enemies.add(new Enemy(x, y, patrolPath, character, maze));
        }
    }

    /**
     * Checks if the specified position is too close to any occupied positions.
     *
     * @param x                The x coordinate to check
     * @param y                The y coordinate to check
     * @param occupiedPositions Set of occupied positions to compare against
     * @return true if the position is too close to others, false otherwise
     */
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

    /**
     * Generates a patrol path for an enemy starting from the specified position.
     *
     * @param startX   The starting x coordinate for the enemy
     * @param startY   The starting y coordinate for the enemy
     * @param mazeArray The maze represented as a 2D array
     * @return A list of coordinates representing the enemy's patrol path
     */
    private List<int[]> generatePatrolPath(int startX, int startY, int[][] mazeArray) {
        List<int[]> path = new ArrayList<>();
        path.add(new int[]{startX, startY});
        Random random = new Random();

        int x = startX;
        int y = startY;
        int pathLength = 1; // Start with the initial position

        while (pathLength < 5) { // Ensure the path is at least 5 blocks long
            int direction = random.nextInt(4); // 0: up, 1: down, 2: left, 3: right
            switch (direction) {
                case 0: // up
                    if (y < mazeArray[0].length - 1 && mazeArray[x][y + 1] == 1) {
                        y++;
                    }
                    break;
                case 1: // down
                    if (y > 0 && mazeArray[x][y - 1] == 1) {
                        y--;
                    }
                    break;
                case 2: // left
                    if (x > 0 && mazeArray[x - 1][y] == 1) {
                        x--;
                    }
                    break;
                case 3: // right
                    if (x < mazeArray.length - 1 && mazeArray[x + 1][y] == 1) {
                        x++;
                    }
                    break;
            }
            path.add(new int[]{x, y});
            pathLength++;
        }

        return path;
    }

    /**
     * Handles user input for character movement and other actions.
     */
    private void handleInput() {
        boolean moved = false;
        moved = character.handleInput(maze, moved, this);
        if (moved) {
            checkCollisions();
        }
    }

    /**
     * Checks for collisions between the character and enemies.
     */
    private void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (character.getX() == enemy.getX() && character.getY() == enemy.getY()) {
                onCharacterDeath();
                break;
            }
        }
    }

    /**
     * Action to perform when the character dies.
     */
    private void onCharacterDeath() {
        lives--;
        if (lives <= 0) {
            gameOver = true;
            paused = true; // Pause the game on game over
        } else {
            character.setPosition(1, 1); // Reset character position
        }
        Gdx.app.log("MysteryMaze", "Hero Died!");
    }

    /**
     * Draws the main character on the screen.
     */
    private void drawCharacter(float offsetX, float offsetY) {
        batch.draw(characterTexture, offsetX + character.getX() * CELL_SIZE, offsetY + (maze.getHeight() - character.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private int drawBomb(float offsetX, float offsetY, float delta){
        int score = 0;
        for (Iterator<Bomb> iterator = character.getBombs().iterator(); iterator.hasNext();) {
            Bomb bomb = iterator.next();
            bombScore += bomb.update(delta, character, enemies, maze); // Update bomb timer
            bomb.draw(batch, bombTexture, flashTexture,offsetX, offsetY ,CELL_SIZE, maze); // Draw bomb
            if (bomb.hasExploded()) {
                iterator.remove();
                if(character.isDead()){
                    onCharacterDeath();
                }
                // Remove dead enemy from the list
                enemies.removeIf(Enemy::isDead);
            }
        }
        return score;
    }

    /**
     * Updates and draws all enemies on the screen.
     */
    private void updateAndDrawEnemies(float offsetX, float offsetY) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime, maze);
            batch.draw(enemyTexture, offsetX + enemy.getX() * CELL_SIZE, offsetY + (maze.getHeight() - enemy.getY() - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    /**
     * Handles input for pausing the game.
     */
    private void handlePauseInput() {
        // Toggle pause state on key press, e.g., P key
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && !justPaused) {
            paused = !paused;
            justPaused = true;
        } else if (!Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            justPaused = false;
        }
    }

    /**
     * Handles input for restarting the game.
     */
    private void handleRestartInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && gameOver) {
            restartGame();
        }
    }

    /**
     * Restarts the game by reinitializing necessary variables.
     */
    private void restartGame() {
        timer = 0;
        points = 0;
        lives = 3;
        gameOver = false;
        paused = false;
        character.setPosition(1, 1);
        enemies.clear();
        placeEnemies(5);
    }

    private String formatTime(float timeInSeconds) {
        int minutes = (int) timeInSeconds / 60;
        int seconds = (int) timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private int getBombScore(){
        int bombScore = 0;
        for (Bomb bomb : bombs){
            bombScore += bomb.getBombScore();
        }
        return  bombScore;
    }

    // Method to call when the game is finished
    public void finishGame() {
        gameFinished = true;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
    }

    @Override
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

