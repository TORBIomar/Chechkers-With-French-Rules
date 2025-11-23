# Jeu de Dames - French/Moroccan Checkers

A beautiful, fully-featured checkers game implementing French/Moroccan rules with a modern JavaFX interface.

![Java](https://img.shields.io/badge/Java-17+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-20-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.5-green)

## Features

- ✅ **Full French/Moroccan Rules Implementation**
  - Mandatory captures (must capture if possible)
  - Multiple captures in one turn (capture chains)
  - Forward-only movement for regular pieces
  - Kings can move and capture in any direction
  - Automatic promotion to king when reaching opposite end

- 🎨 **Modern UI**
  - Beautiful dark theme with gradient backgrounds
  - Smooth piece animations
  - Visual move indicators (green for moves, red for captures)
  - Highlighted possible moves when selecting a piece
  - Responsive design - window is fully resizable

- 🎮 **Game Features**
  - Turn-based gameplay
  - Visual feedback for selected pieces
  - Game over detection
 

## Requirements

- Java 17 or higher
- Gradle (or use the included wrapper)

## How to Run

### Using Gradle Wrapper (Recommended)

```bash
# On Windows
.\gradlew.bat run

# On Linux/Mac
./gradlew run
```

### Using Gradle Directly

```bash
gradle run
```

### Build JAR

```bash
gradle build
java -jar build/libs/dames.jar
```

### Using IDE

1. Open the project in IntelliJ IDEA, Eclipse, or VS Code
2. Import as a Gradle project
3. Run the `Main` class

## Game Rules

### Basic Movement
- **Regular Pieces**: Can only move forward diagonally (white moves up, black moves down)
- **Kings**: Can move any number of squares diagonally in any direction
- Pieces can only move to empty squares

### Captures
- **Mandatory**: If a capture is possible, you must take it
- **Multiple Captures**: Continue capturing in the same turn if possible
- **Regular Pieces**: Can only capture forward
- **Kings**: Can capture in any direction and land anywhere after the captured piece

### Promotion
- Pieces become kings when reaching the opposite end of the board
- White pieces promote on row 0
- Black pieces promote on row 7

## Controls

- **Click** on a piece to select it
- **Green circles** indicate possible regular moves
- **Red/orange circles** indicate possible captures
- **Click** on a highlighted square to move
- Continue clicking to chain multiple captures

## Project Structure

```
src/main/java/com/example/dames/
├── Main.java                    # Application entry point
├── controller/
│   └── BoardController.java     # Game logic and UI controller
└── model/
    ├── Piece.java               # Piece representation
    ├── PieceType.java           # Piece type enum (MAN, KING)
    ├── Tile.java                # Board tile representation
    └── Move.java                # Move representation

src/main/resources/com/example/dames/
├── board.fxml                   # UI layout
└── styles.css                   # Styling
```

## Technologies

- **JavaFX 20** - Modern UI framework
- **Gradle** - Build automation
- **Java 17** - Programming language

## License

This project is open source and available for educational purposes.

## Contributing

Feel free to fork this project and submit pull requests for any improvements!

---

Enjoy playing! 🎮
