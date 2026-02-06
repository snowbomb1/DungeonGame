package com.game;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

record ExperienceGainOutput(int experienceGained, int cumulativeDamageToEnemy) {}

public class CombatSystem {
    
    public ExperienceGainOutput calculateExperienceGain(int damageDealt, boolean bossFight, Boss boss, Enemy enemy, int gameLevel, int cumulativeDamageToEnemy) {
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
        
        return new ExperienceGainOutput(xpThisHit, cumulativeDamageToEnemy);
    }

    private Timeline createGlowEffect(Label label, Color color, int totalTime) {
        Glow glow = new Glow(1.0);
        DropShadow shadow = new DropShadow();
            shadow.setColor(color);
            shadow.setRadius(0);
            shadow.setSpread(0.0);
        shadow.setInput(glow);
        label.setEffect(shadow);
        Timeline t = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(shadow.radiusProperty(), 0.0),
                new KeyValue(glow.levelProperty(), 0.0)
            ),
            new KeyFrame(Duration.millis(totalTime / 2),
                new KeyValue(shadow.radiusProperty(), 10.0),
                new KeyValue(glow.levelProperty(), 0.8)
            ),
            new KeyFrame(Duration.millis(totalTime),
                new KeyValue(shadow.radiusProperty(), 0.0),
                new KeyValue(glow.levelProperty(), 0.0)
            )
        );
    
        return t;
    }

    public Timeline createShakeEffect(Label label) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), label);
        shake.setByX(10);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        return new Timeline(new KeyFrame(Duration.ZERO, e -> shake.play()));
    }

    public void animateProgressBar(javafx.scene.control.ProgressBar progressBar, double to, Runnable onComplete) {
        double currentProgress = progressBar.getProgress();
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), currentProgress)),
            new KeyFrame(Duration.millis(500), new KeyValue(progressBar.progressProperty(), to))
        );
        timeline.setOnFinished(event -> onComplete.run());
        timeline.play();
    }

    public void animatePlayerAttack(javafx.scene.control.Label playerChar, javafx.scene.control.Label enemyChar, Runnable onComplete) {
        // Player attack animation
        TranslateTransition playerAttack = new TranslateTransition(Duration.millis(200), playerChar);
        playerAttack.setByX(150);
        playerAttack.setAutoReverse(true);
        playerAttack.setCycleCount(2);

        // Create damage effect on enemy character
        Timeline effect = createGlowEffect(enemyChar, Color.RED, 500);

        playerAttack.play();

        // Call onComplete after animations finish
        ParallelTransition attackAnimation = new ParallelTransition(playerAttack, effect);
        attackAnimation.setOnFinished(event -> onComplete.run());
        attackAnimation.play();
    }

    public void animateEnemyAttack(javafx.scene.control.Label playerChar, javafx.scene.control.Label enemyChar, Runnable onComplete) {
        // Enemy attack animation
        TranslateTransition enemyAttack = new TranslateTransition(Duration.millis(200), enemyChar);
        enemyAttack.setByX(-150);
        enemyAttack.setAutoReverse(true);
        enemyAttack.setCycleCount(2);

        // Create damage effect on player character
        Timeline effect = createGlowEffect(playerChar, Color.RED, 500);

        enemyAttack.play();

        // Call onComplete after animations finish
        ParallelTransition attackAnimation = new ParallelTransition(enemyAttack, effect);
        attackAnimation.setOnFinished(event -> onComplete.run());
        attackAnimation.play();
    }

    public void animateEnemyDefeat(javafx.scene.control.Label enemyChar, Runnable onComplete) {
        // Enemy defeat animation (fade out)
        TranslateTransition moveOut = new TranslateTransition(Duration.millis(500), enemyChar);
        moveOut.setFromX(0);
        moveOut.setToX(200);
        moveOut.setOnFinished(event -> onComplete.run());
        moveOut.play();
    }

    public void animateBossDefeat(javafx.scene.control.Label bossChar, Runnable onComplete) {
        // Boss defeat animation (fade out)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), bossChar);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> onComplete.run());
        fadeOut.play();
    }

    public void animateNewEnemy(javafx.scene.control.Label enemyChar, Runnable onComplete) {
        TranslateTransition moveIn = new TranslateTransition(Duration.millis(500), enemyChar);
        moveIn.setFromX(200);
        moveIn.setToX(0);
        moveIn.setOnFinished(event -> onComplete.run());
        moveIn.play();
    }

    public void animateNewBoss(javafx.scene.control.Label bossChar, Runnable onComplete) {
        // New boss animation (fade in)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), bossChar);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(event -> onComplete.run());
        fadeIn.play();
    }

    public void animatePlayerLevelUp(javafx.scene.control.Label playerChar, Runnable onComplete) {
        // Player level up animation (scale up and back)
        ScaleTransition levelUp = new ScaleTransition(Duration.millis(300), playerChar);
        levelUp.setFromX(1.0);
        levelUp.setFromY(1.0);
        levelUp.setToX(1.5);
        levelUp.setToY(1.5);
        levelUp.setAutoReverse(true);
        levelUp.setCycleCount(2);
        levelUp.setOnFinished(event -> onComplete.run());
        levelUp.play();
    }

    public void animateGameLevelUp(javafx.scene.control.Label playerChar, javafx.scene.control.Label enemyChar, javafx.scene.control.Label bossChar, Runnable onComplete) {
        // Game level up animation (fade out and in)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), playerChar);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), playerChar);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(event -> {
            // Reset enemy and boss visibility
            enemyChar.setOpacity(0.0);
            bossChar.setOpacity(0.0);
            fadeIn.play();
        });

        fadeIn.setOnFinished(event -> onComplete.run());

        fadeOut.play();
    }

    public void animateDamageIndicator(Label damageLabel, int damage) {
        damageLabel.setText("-" + damage);
        damageLabel.setOpacity(1.0);
        damageLabel.setVisible(true);
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(800), damageLabel);
        moveUp.setByY(-50);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), damageLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        Timeline glow = createGlowEffect(damageLabel, Color.RED, 800);

        ParallelTransition indicatorAnimation = new ParallelTransition(moveUp, fadeOut, glow);
        indicatorAnimation.play();
        indicatorAnimation.setOnFinished(event -> {
            damageLabel.setVisible(false);
            damageLabel.setTranslateY(0); // Reset position for next time
        });
    }

    public void animatePlayerHeal(javafx.scene.control.Label playerChar, Runnable onComplete) {
        // Player heal animation (glow effect)
        Timeline effect = createGlowEffect(playerChar, Color.GREENYELLOW, 750);
        effect.setOnFinished(event -> onComplete.run());
        effect.play();
    }
}
