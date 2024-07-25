# Mystery Maze Game

## Introduction
Welcome to the Mystery Maze game repository! This game is developed using LibGDX and features a maze-based adventure where the player navigates through a maze while avoiding enemies.

## Demo Link: 
- 🔗 [Demo](https://drive.google.com/drive/folders/1w4GlfvgHvNymEj8fu7lgiaPjORSS7CDF?usp=sharing)

## Classes Overview

### MysteryMaze
- **Purpose**: Entry point of the game.
- **Responsibilities**: Manages game initialization, sets up the main menu screen, and handles screen transitions.
- **Key Features**: Utilizes LibGDX for graphics and input handling.

### MainMenuScreen
- **Purpose**: Represents the main menu screen.
- **Responsibilities**: Renders the main menu interface, including play and exit buttons.
- **Key Features**: Uses LibGDX's sprite batch for rendering textures and manages user input for menu navigation.

### GameScreen
- **Purpose**: Manages the main gameplay screen.
- **Responsibilities**: Renders the maze, player character, enemies, and handles user input during gameplay.
- **Key Features**: Implements maze generation, character movement, enemy AI, and collision detection.

### Maze
- **Purpose**: Generates and manages the maze environment.
- **Responsibilities**: Uses Depth-First Search (DFS) algorithm for maze generation and provides methods to check paths, walls, and floor tiles.
- **Key Features**: Supports dynamic maze generation and provides methods for rendering maze textures.

### Hero
- **Purpose**: Represents the player character.
- **Responsibilities**: Handles player input for movement and manages the character's position in the maze.
- **Key Features**: Implements movement restrictions based on maze layout and cooldowns for smooth player input handling.

### Enemy
- **Purpose**: Represents enemy entities.
- **Responsibilities**: Manages enemy AI for patrolling and pathfinding towards the player character.
- **Key Features**: Uses pathfinding algorithms to navigate the maze and avoids obstacles and dead-ends.

### Pathfinding
- **Purpose**: Implements pathfinding algorithms.
- **Responsibilities**: Finds the shortest path between points in the maze for enemies.
- **Key Features**: Uses A* algorithm with Manhattan distance heuristic for optimal pathfinding.

### Bresenham
- **Purpose**: Provides line drawing algorithms.
- **Responsibilities**: Used for rendering walls and floors efficiently in the maze.
- **Key Features**: Optimized algorithms for drawing lines in 2D grid-based environments.

### Bomb
- **Purpose**: Provides all the algorithms for the bomb.
- **Responsibilities**: Used for explosions to kill enemy and open paths through thorns.
- **Key Features**: Explosion algorithm optimized to affect in certain range and kills hero as well as enemy in range.


## Credits
Made with ❤️ and Java by Devrajsinh Gohil
