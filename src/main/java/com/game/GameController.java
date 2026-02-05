package com.game;

record ExperienceGainResult(int experienceGained, int cumulativeDamageToEnemy) {}

public class GameController {
    private CombatSystem combatSystem = new CombatSystem();
    private Player player;
    private Enemy enemy;
    private Boss boss;
    private int gameLevel = 1;
    private boolean isBossFight = false;
    private int goblinsDefeated = 0;
    private int cumulativeDamageToEnemy = 0;  // Track total damage to current enemy

    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
    public Boss getBoss() { return boss; }
    public int getGameLevel() { return gameLevel; }
    public boolean isBossFight() { return isBossFight; }
    public int getGoblinsDefeated() { return goblinsDefeated; }

    public GameController() {
        player = new Player();
        enemy = new Enemy(15, 15, 15, 5, 20);
        boss = new Boss();
    }

    public void handlePlayerAttack() {
        int damage = player.attack();
        if (isBossFight) {
            boss.takeDamage(damage);
            if (!boss.isAlive()) {
                isBossFight = false;
                // Boss defeated logic
            }
        } else {
            enemy.takeDamage(damage);
            if (!enemy.isAlive()) {
                goblinsDefeated++;
                if (goblinsDefeated >= 3) {
                    isBossFight = true;
                    goblinsDefeated = 0;
                }
            }
        }
        ExperienceGainOutput result = combatSystem.calculateExperienceGain(damage, isBossFight, boss, enemy, gameLevel, cumulativeDamageToEnemy);
        player.gainExperience(result.experienceGained());
        cumulativeDamageToEnemy = result.cumulativeDamageToEnemy();
    }

    public void handleEnemyAttack() {
        int enemyDamage = isBossFight ? boss.attack() : enemy.attack();
        player.takeDamage(enemyDamage);
    }

    public void increaseGameLevel() {
        gameLevel++;
        isBossFight = false;
        player.increaseGameLevel(gameLevel);
        enemy.increaseGameLevel(gameLevel);
        boss.increaseGameLevel(gameLevel);
    }

    public void startBossFight() {
        isBossFight = true;
        cumulativeDamageToEnemy = 0;
    }
    
    public void handlePotion() {
        player.heal();
    }

    public void createNewEnemy() {
        enemy.heal();
        cumulativeDamageToEnemy = 0;
        player.health += 10; // Small health boost between fights
        if (player.health > player.maxHealth) {
            player.health = player.maxHealth;
        }
    }
}
