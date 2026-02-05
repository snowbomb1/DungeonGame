package com.game;

import java.util.Scanner;

public class Game {
    private Scanner scanner = new Scanner(System.in);
    private Player player = new Player();
    private Enemy enemy = new Enemy(15, 15, 15, 5, 20);
    private Boss boss = new Boss();

    private final int MAX_LEVEL = 5;
    private final int MAX_GOBLINS = 3;
    private boolean bossFight = false;
    private int gameLevel = 1;
    private int goblinsDefeated = 0;
    private int cumulativeDamageToEnemy = 0;  // Track total damage to current enemy

    private final String[] actions = {"Attack", "Run", "Use Potion"};

    private void clearScreen() {
        System.out.println("\n".repeat(50));
    }

    private void waitForContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void increaseGameLevel() {
        if (gameLevel >= MAX_LEVEL) {
            gameLevel++; // Increment to indicate game completion
            return;
        }
        gameLevel++;
        bossFight = false;
        player.increaseGameLevel(gameLevel);
        enemy.increaseGameLevel(gameLevel);
        boss.increaseGameLevel(gameLevel);
        System.out.println("You have advanced to game level " + gameLevel + "!");
    }


    public int calculateExperienceGain(int damageDealt) {
        int maxEnemyHealth = bossFight ? boss.getMaxHealth() : enemy.getMaxHealth();
        int xpValue = bossFight ?
            boss.getExperienceValue(gameLevel) :
            enemy.getExperienceValue(gameLevel);
        
        // Total damage before this hit and after this hit
        int totalDamageBefore = cumulativeDamageToEnemy;
        int totalDamageAfter = cumulativeDamageToEnemy + damageDealt;
        if (totalDamageAfter > maxEnemyHealth) totalDamageAfter = maxEnemyHealth;
        
        // Calculate XP earned for damage dealt, capped at max enemy XP
        int totalXpBefore = (int)(((double) totalDamageBefore / maxEnemyHealth) * xpValue);
        if (totalXpBefore > xpValue) totalXpBefore = xpValue;
        
        int totalXpAfter = (int)(((double) totalDamageAfter / maxEnemyHealth) * xpValue);
        if (totalXpAfter > xpValue) totalXpAfter = xpValue;
        
        int xpThisHit = totalXpAfter - totalXpBefore;
        if (xpThisHit < 0) xpThisHit = 0;
        
        // Update cumulative damage for next hit
        cumulativeDamageToEnemy = totalDamageAfter;
        
        return xpThisHit;
    }

    public void playerAttack() {
        int damage = player.attack();
        System.out.println("You deal " + damage + " damage to the goblin.");
        if (!bossFight) {
            enemy.takeDamage(damage);
        } else {
            boss.takeDamage(damage);
        }
        int experienceGained = calculateExperienceGain(damage);
        player.gainExperience(experienceGained);
        System.out.println("You gained " + experienceGained + " experience points.");
    }

    public void enemyAttack() {
        int damage = enemy.attack();
        player.takeDamage(damage);
        System.out.println("The goblin attacked you for " + damage + " damage.");
    }
    public boolean isGameOver() {
        return !player.isAlive();
    }
    public boolean isGameComplete() {
        return gameLevel >= MAX_LEVEL;
    }
    public void runAway() {
        enemy.heal();
    }
    public void resetGame() {
        player.reset();
        enemy.reset();
        boss.reset();
        gameLevel = 1;
        bossFight = false;
        cumulativeDamageToEnemy = 0;
        startGame();
    }
    public void displayStatus() {
        System.out.println("----- Game Status -----");
        System.out.println("Game Level: " + gameLevel);
        System.out.println("----- Player & Enemy Status -----");
        System.out.println(player.getStatus());
        if (!bossFight) System.out.println(enemy.getStatus(goblinsDefeated, MAX_GOBLINS));
        else System.out.println(boss.getStatus(0, 1));
        System.out.println("-----------------------");
    }

    public void displayActions() {
        System.out.println("Choose an action:");
        for (int i = 0; i < actions.length; i++) {
            System.out.println((i + 1) + ". " + actions[i]);
        }
    }

    public void bossAttack() {
        int damage = boss.attack();
        player.takeDamage(damage);
        System.out.println("The Goblin King attacked you for " + damage + " damage.");
    }

    public String getUserAction(String input) {
        System.out.print("Enter your action: ");
        String action = scanner.nextLine();
        return action;
    }

    public void startGame() {
        System.out.println("Welcome to the Goblin Battle Game!");
        while (!isGameOver() && !isGameComplete()) {
            clearScreen();
            displayStatus();
            System.out.println("Goblins defeated: " + goblinsDefeated + "/" + MAX_GOBLINS);
            displayActions();
            String action = getUserAction(null);
            switch (action) {
                case "1":
                    playerAttack();
                    int enemyHealth = bossFight ? boss.getHealth() : enemy.getHealth();
                    if (enemyHealth > 0) {
                        if (bossFight) bossAttack();
                        else enemyAttack();
                    } else if (bossFight) {
                        System.out.println("You have defeated the Goblin King!");
                        increaseGameLevel();
                    } else {
                        System.out.println("You have defeated the goblin!");
                        if (goblinsDefeated < MAX_GOBLINS - 1) {
                            goblinsDefeated++;
                            enemy.heal();
                            cumulativeDamageToEnemy = 0;  // Reset for new enemy
                            System.out.println("A new goblin appears!");
                        } else {
                            System.out.println("A Goblin King appears!");
                            bossFight = true;
                            goblinsDefeated = 0;
                            cumulativeDamageToEnemy = 0;  // Reset for boss
                        }
                    }
                    waitForContinue();
                    break;
                case "2":
                    runAway();
                    cumulativeDamageToEnemy = 0;  // Reset when running away
                    System.out.println("You ran away!");
                    System.out.println("You have happened upon a new goblin!");
                    waitForContinue();
                    break;
                case "3":
                    player.heal();
                    if (bossFight) bossAttack();
                    else enemyAttack();
                    waitForContinue();
                    break;
                default:
                    System.out.println("Invalid action. Please choose again.");
                    waitForContinue();
                    break;
            }
        }
        if (isGameOver()) {
            System.out.println("Game Over! The goblin has defeated you. Play Again?");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("yes")) {
                resetGame();
            } else {
                System.out.println("Thank you for playing!");
            }
        } else if (isGameComplete()) {
            System.out.println("Congratulations! You have defeated all the goblins and completed the game!");
            System.out.println("Play Again?");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("yes")) {
                resetGame();
            } else {
                System.out.println("Thank you for playing!");
            }
        }
    }
}
