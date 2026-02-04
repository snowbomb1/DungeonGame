# DungeonGame

A text-based dungeon crawler game written in Java. Battle goblins across multiple levels, defeat the boss, and master the art of combat.

## Features

- **Progressive Difficulty**: 5 game levels with scaling enemy stats
- **Combat System**: Attack, run away, or use healing potions in turn-based combat
- **Experience & Leveling**: Gain XP from defeating goblins and level up with stat increases
- **Health Potions**: Limited potions that heal the player for 20 health points
- **Boss Battle**: Final challenge at level 5 with significantly stronger stats
- **Enemy Respawning**: Goblins reset health when you run away

## Project Structure

```
├── Main.java        # Entry point for the game
├── Game.java        # Core game logic and progression system
├── Player.java      # Player character with health, attack, and XP tracking
├── Enemy.java       # Goblin enemy class with configurable stats
└── Boss.java        # Boss enemy (extends Enemy) with higher difficulty
```

## Getting Started

### Prerequisites
- Java 8 or higher
- A terminal or IDE to compile and run

### Running the Game

1. Compile all Java files:
   ```bash
   javac *.java
   ```

2. Run the game:
   ```bash
   java Main
   ```

## How to Play

- Choose to **Attack**, **Run**, or **Use Potion** each turn
- Defeat goblins to gain experience and level up
- Progress through 5 game levels of increasing difficulty
- Use potions wisely—you have limited supplies
- Defeat the boss on level 5 to complete the game

## Game Mechanics

- **Attack**: Deals random damage between 5 and your max attack damage
- **Potions**: Heal 20 health each; more potions available at higher levels
- **Experience**: Earned proportionally to damage dealt, required 100 XP per level
- **Leveling**: Increases max health and attack damage

## Author

Nathaniel McIlvenna
