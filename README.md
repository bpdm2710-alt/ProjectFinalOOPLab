# Classic Tetris Java

A clean, single-package Classic Tetris implementation built with Java Swing for academic presentation and simple gameplay demos.

## Features

- 7 tetrominoes with 4 rotation states each
- 10×20 board with gravity, collision, hard drop, and soft drop
- Ghost piece and grid rendering
- CTWC-style line clear flash effect
- Pause and resume with `P`
- Marathon and Practice modes
- High score saved to `highscore.txt`
- Background music and sound effects from the local `Sound/` folder

## Controls

| Key | Action |
|-----|--------|
| `←` / `→` | Move left / right |
| `↓` | Soft drop |
| `↑` or `X` | Rotate clockwise |
| `Space` | Hard drop |
| `P` | Pause / resume |
| `R` | Restart on the Game Over screen |
| `ESC` | Return to the main menu |

## Game Modes

### Marathon
Standard endless play. Gravity increases as the score grows through line clears.

### Practice
Gravity stays at the starting speed, so you can train stacking and rotation without pressure.

## Project Structure

```text
Classic/
├── classic/
│   ├── Main.java
│   ├── MenuPanel.java
│   ├── GamePanel.java
│   ├── GameManager.java
│   ├── KeyHandler.java
│   ├── Sound.java
│   ├── Tetromino.java
│   ├── TetrominoFactory.java
│   ├── ScoringStrategy.java
│   ├── GuidelineScoring.java
│   └── ScoreListener.java
└── Sound/
    ├── Tetris 99 - Main Theme - SoundHub.wav
    ├── delete line.wav
    └── gameover.wav
```

## Build and Run

Run from the project root so the game can find the `Sound/` assets.

```bash
javac classic/*.java
java classic.Main
```

If you are using VS Code, open the `Classic` folder directly and run `classic.Main` from the Java extension.

## Notes

- `highscore.txt` is generated automatically when the best score changes.
- Delete `highscore.txt` if you want to reset the saved best score.
- Java 17 or later is recommended.