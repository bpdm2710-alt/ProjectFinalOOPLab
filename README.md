# 🟦 Tetris — Java Edition

> A feature-complete Tetris implementation in Java (AWT/Swing), inspired by the modern gameplay feel of [tetr.io](https://tetr.io).

---

## 📋 Table of Contents

- [Features](#features)
- [Controls](#controls)
- [Game Modes](#game-modes)
- [Handling Configuration](#handling-configuration)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Scoring System](#scoring-system)
- [How to Build & Run](#how-to-build--run)
- [Requirements](#requirements)

---

## ✨ Features

- **Modern Handling System** — DAS, ARR, SDF fully configurable in-game
- **Extended Placement (Lock Delay Reset)** — up to 15 slide/spin resets per piece, Tetris Guideline compliant
- **SRS (Super Rotation System)** — full wall kick table for J, L, S, T, Z, I pieces
- **180° Rotation** — direct state flip without double-kick artifacts
- **Ghost Piece** — shows landing position at all times
- **Hold Queue** — hold a piece and swap it in later (once per piece)
- **Next Queue** — shows the next 5 upcoming pieces (tetr.io style)
- **7-bag Randomizer** — fair piece distribution, no droughts
- **Guideline Speed Curve** — gravity formula `(0.8 − (level−1)×0.007)^(level−1)` sec/row
- **Guideline Scoring** — Single ×1, Double ×3, Triple ×5, Tetris ×8 (multiplied by level)
- **Line Clear Animation** — yellow flash effect on cleared rows
- **Sound Effects** — BGM (Tetris 99 theme), rotation, line clear, floor touch, game over
- **Practice Mode** — gravity and level progression locked at base speed
- **Pause / Resume** — press `P` in-game
- **Hard Drop** — instant lock with 2 pts/cell bonus score
- **Soft Drop** — SDF-accelerated fall with 1 pt/cell bonus score
- **Return to Menu** — hold `ESC` for 0.5 s during gameplay
- **Restart** — press `R` on the Game Over screen

---

## 🎮 Controls

| Key | Action |
|-----|--------|
| `←` / `→` | Move left / right |
| `↓` | Soft drop (SDF-accelerated) |
| `↑` or `X` | Rotate clockwise |
| `Z` | Rotate counter-clockwise |
| `A` | Rotate 180° |
| `Space` | Hard drop |
| `C` | Hold piece |
| `P` | Pause / Resume |
| `R` | Restart *(Game Over screen only)* |
| Hold `ESC` | Return to main menu |

---

## 🕹️ Game Modes

### Marathon
Standard Tetris experience. Gravity increases every 5 lines cleared, following the official Tetris Guideline speed curve. Survive as long as possible and aim for a high score.

### Practice
Gravity is locked at the starting speed (level 1) for the entire session. Ideal for practicing stacking, T-spins, and Perfect Clear setups without time pressure.

---

## ⚙️ Handling Configuration

Open **CONFIG** from the main menu to adjust:

| Setting | Description | Default |
|---------|-------------|---------|
| **DAS** (Delayed Auto Shift) | Time (ms) before auto-repeat starts when holding ←/→ | `160 ms` |
| **ARR** (Auto Repeat Rate) | Interval (ms) between each repeated shift. `0` = instant-to-wall | `32 ms` |
| **SDF** (Soft Drop Factor) | Maximum cells dropped per frame when holding ↓ | `20×` |

Values are entered in milliseconds and automatically converted to frame counts at 60 FPS.

---

## 🏗️ Architecture Overview

The project follows a clean, decoupled design:

```
Main
 └─ CardLayout (JPanel)
     ├─ MenuPanel     — Custom Graphics2D UI, no Swing button defaults
     └─ GamePanel     — Runnable game loop at 60 FPS
          └─ GameManager   — All game state & logic
               ├─ MinoFactory   — Creates Mino instances (Dependency Injection)
               ├─ GameRenderer  — All drawing separated from game logic
               ├─ KeyHandler    — Thread-safe AtomicBoolean input
               └─ Sound         — Audio playback with try-with-resources safety
```

**Key design decisions:**
- **No Singleton pattern** — `GameManager` is a plain instance passed via constructor (DI)
- **No static mutable game state** — all fields live on the `GameManager` instance
- **Thread safety** — `effectY` uses `CopyOnWriteArrayList`; input flags use `AtomicBoolean`
- **Strategy pattern** — `ScoringStrategy` / `GuidelineScoring` for clean scoring extension
- **Factory pattern** — `MinoFactory.createByType(gm, type)` centralises piece creation

---

## 📁 Project Structure

```
ProjectFinalOOPLab/
├── MainMethods/
│   ├── Main.java              Entry point; wires CardLayout
│   ├── MenuPanel.java         Custom-rendered main menu
│   ├── GamePanel.java         60 FPS game loop (Runnable)
│   ├── GameManager.java       Core game state & update logic
│   ├── GameRenderer.java      All Graphics2D rendering
│   ├── KeyHandler.java        KeyListener + AtomicBoolean flags
│   ├── MinoFactory.java       Piece factory (Dependency Injection)
│   ├── Sound.java             WAV audio playback
│   ├── GameState.java         Enum: PLAYING, PAUSED, GAME_OVER
│   ├── ScoringStrategy.java   Scoring interface
│   └── GuidelineScoring.java  Tetris Guideline scoring implementation
├── mino/
│   ├── Block.java             Single coloured tile
│   ├── Mino.java              Base tetromino (DAS/ARR/SDF/Lock logic)
│   ├── Mino_I/J/L/O/S/T/Z.java  Piece-specific shapes & rotations
│   └── SRSKickTable.java      Wall kick offsets for SRS
└── Sound/
    ├── Tetris 99 - Main Theme - SoundHub.wav
    ├── delete line.wav
    ├── gameover.wav
    ├── rotation.wav
    └── touch floor.wav
```

---

## 🏆 Scoring System

Based on the **Tetris Guideline** scoring formula:

| Lines Cleared | Base Points | ×Level |
|---------------|-------------|--------|
| 1 (Single)    | 100         | ✓ |
| 2 (Double)    | 300         | ✓ |
| 3 (Triple)    | 500         | ✓ |
| 4 (Tetris)    | 800         | ✓ |
| Soft Drop     | 1 per cell  | — |
| Hard Drop     | 2 per cell  | — |

---

## 🚀 How to Build & Run

### Compile

```bash
javac MainMethods/*.java mino/*.java
```

### Run

```bash
java MainMethods.Main
```

> ⚠️ Make sure the working directory is the **project root** (where `Sound/` is located), otherwise audio files will not be found.

### Run from VS Code / IntelliJ

Set the working directory to the project root folder in your run configuration. The Java extension for VS Code handles this automatically if you open the folder directly.

---

## 📌 Requirements

| Requirement | Version |
|-------------|---------|
| **Java** | 17 or higher (21 recommended) |
| **OS** | Windows / macOS / Linux |
| **Audio** | Requires `javax.sound.sampled` (bundled with standard JDK) |

---

## 🎨 Credits & Inspiration

- Gameplay mechanics and visual design inspired by **[tetr.io](https://tetr.io)**
- Background music: *Tetris 99 Main Theme* (Nintendo)
- Rotation system: **Super Rotation System (SRS)** as defined in the Tetris Guideline