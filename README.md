# Classic Tetris

A compact Classic Tetris implementation written in Java Swing. Designed for teaching, demonstration, and lightweight play.

What's changed (recent updates)
- NES-style gravity: gravity uses a frames-per-cell lookup table to reproduce original timing.
- Fixed ARE (entry delay): 10 frames after a piece locks (or after line-clear flash) before the next piece spawns.
- NES-style soft drop: fixed 2 frames per cell (~33 ms) while holding `↓`.
- Soft-drop transition safety: when soft-drop begins, the fall accumulator is reset to avoid large instant drops.
- Counterclockwise rotation: `Z` rotates pieces CCW.
- CTWC-style white flash on line clear (about 10 frames).
- High score is persisted to `highscore.txt` when a new best is reached.
- Background music replaced with a local Tetris theme in the `Sound/` folder.

Features
- All 7 tetrominoes with 4 rotation states
- 10×20 playfield, collision, hard drop, soft drop, and ghost piece
- Grid lines and CTWC-style line-clear flash
- Pause (`P`), restart (`R`), and menu (`ESC`) controls
- High-score persistence

Controls
- Left / Right: move piece
- Down: soft drop (fixed 2 frames ≈ 33 ms per cell)
- Up or X: rotate clockwise
- Z: rotate counterclockwise
- Space: hard drop
- P: pause / resume
- R: restart (on Game Over)
- ESC: return to main menu

Design notes
- `TetrominoFactory`: Factory pattern for piece creation
- `ScoreListener`: Observer pattern for HUD updates
- `ScoringStrategy` / `GuidelineScoring`: Strategy pattern for scoring rules

Project layout
```
Classic/
├── classic/    # Java sources (package `classic`)
└── Sound/      # .wav assets used for BGM and SFX
```

Requirements
- Java 11 or newer (Java 17+ recommended)

Build & Run
Run from the `Classic` directory so the `Sound/` assets are available:

```bash
javac classic/*.java
java classic.Main
```

Or run `classic.Main` from your IDE (for example, the Java extension in VS Code).

Notes
- `highscore.txt` is created/updated automatically when you beat the current best score. Delete it to reset the saved high score.
- Timing and feel parameters (NES gravity table, `ARE_FRAMES`) are defined in `classic/GameManager.java` if you want to tweak them.

If you'd like, I can add screenshots, a short changelog, or platform-specific run instructions for Windows.