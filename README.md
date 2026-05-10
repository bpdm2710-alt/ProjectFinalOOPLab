# ✨ Classic Tetris — Classic (OOP Lab Final)

A compact, educational Classic Tetris implementation written in Java (Swing/AWT). Built as a university OOP final project focused on clear design, documented patterns, and faithful NES-like timing.

## 🎮 Features

### Core Gameplay
- 7 tetrominoes with 4 rotation states
- 10×20 playfield with collision detection and locking
- Hard drop and soft drop
- Ghost piece preview showing landing position
- Grid overlay on the playfield

### Extra Features
- Menu screen with Marathon and Practice modes (CardLayout navigation)
- Practice mode: gravity locked at level 1 speed
- NES-accurate gravity table (frames per grid cell)
- ARE (Entry Delay): 10-frame spawn delay after lock / after clear flash
- NES-style line clear animation: rows flash white for 10 frames
- NES-style soft drop: fixed 2 frames per cell (~33 ms)
- High score persistence via `highscore.txt` I/O
- Pause / resume with an overlay (`P`)
- Counterclockwise rotation bound to `Z`
- Background music & sound effects (WAV playback via `javax.sound.sampled`)

## 🏗️ Design Patterns

- **Strategy** — `ScoringStrategy` interface with `GuidelineScoring` implementation. This decouples scoring rules from game flow and allows swapping scoring policies for testing or extensions.

- **Factory** — `TetrominoFactory.createRandom()` centralizes tetromino creation and seeding. The factory isolates piece-generation details and simplifies testing and future piece selection policies.

- **Observer** — `ScoreListener` interface; `GamePanel` implements it and `GameManager` notifies listeners on score/level/lines changes. This keeps HUD updates separated from game logic and enables multiple listeners.

Each pattern is implemented to demonstrate separation of concerns and to support clear, testable code for academic evaluation.

## ⌨️ Controls

| Key | Action |
|---:|:---|
| ← / → | Move left / right |
| ↓ | Soft drop (2 frames per cell ≈ 33 ms) |
| ↑ or X | Rotate clockwise |
| Z | Rotate counter-clockwise |
| Space | Hard drop |
| P | Pause / Resume |
| R | Restart (on Game Over) |
| ESC | Return to menu |

## 📁 Project Structure

```
classic/
├── Main.java              — Entry point, JFrame + CardLayout setup
├── MenuPanel.java         — Main menu UI (Marathon, Practice, Quit)
├── GamePanel.java         — 60 FPS game loop (Runnable), implements `ScoreListener`
├── GameManager.java       — Board logic, gravity, collision, line clear, timing, rendering helpers
├── Tetromino.java         — 7 piece shapes as `int[][][][]` with 4 rotations each
├── TetrominoFactory.java  — Factory pattern: random piece creation
├── KeyHandler.java        — Thread-safe `AtomicBoolean` input flags
├── Sound.java             — WAV playback via `javax.sound.sampled` (try-with-resources for SFX/BGM)
├── ScoreListener.java     — Observer pattern interface
├── ScoringStrategy.java   — Strategy pattern interface for scoring
└── GuidelineScoring.java  — Tetris Guideline scoring implementation
```

## 🔢 Scoring System

| Lines Cleared | Base Points | Notes |
|---:|---:|:---|
| Single (1) | 100 | × level |
| Double (2) | 300 | × level |
| Triple (3) | 500 | × level |
| Tetris (4) | 800 | × level |

Final points = Base Points × Current Level (level increases per 10 lines cleared).

## 🛠️ How to Build & Run

Run from the project root so the `Sound/` folder is available to the runtime.

```bash
javac -d bin classic/*.java
java -cp bin classic.Main
```

Working directory must be the project root (where `Sound/` is located).

## ✅ Requirements

| Item | Requirement |
|:--|:--|
| Language | Java 21 (code compatible with Java 17+) |
| Libraries | None (pure Swing/AWT) |
| OS | Windows / macOS / Linux (JRE required) |

## 🧾 Academic Context & Grading

This repository is prepared for a university OOP final project. Grading rubric example:

- Game implementation: 50 pts
- Report & class diagrams: 10 pts
- Demonstration: 10 pts
- Git usage: 10 pts
- GUI & polish: 10 pts
- Bonus: +5 pts per design pattern implemented, +2 pts per extra feature

Highlight the implemented patterns and features in your report and demo.

## 🎖️ Credits

- Inspired by NES Tetris and CTWC (CTWC-style line-clear flash and timing feel)
- Project code written for educational purposes; all assets included under `Sound/` are local WAV files.

---

