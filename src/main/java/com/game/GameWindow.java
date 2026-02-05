package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.PauseTransition;

enum PlayerAction {
    ATTACK, USE_POTION, RUN
}
enum ProgressBarType {
    HEALTH, EXPERIENCE
}
enum CharacterType {
    PLAYER, ENEMY, BOSS
}

public class GameWindow extends Application {
    private GameController controller = new GameController();
    private CombatSystem combatSystem = new CombatSystem();
    private PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(500));

    private Label playerChar = new Label("🗡️");
    private Label enemyChar = new Label("👹");
    private Label bossChar = new Label("👺");
    private Label playerHealthLabel;
    private Label playerExpLabel;
    private Label enemyHealthLabel;
    private ProgressBar playerHealthBar;
    private ProgressBar playerExpBar;
    private ProgressBar enemyHealthBar;

    // Buttons
    private Button attackButton = new Button("⚔️ ATTACK");
    private Button usePotionButton = new Button("🧪 USE POTION");
    private Button runButton = new Button("🏃 RUN");

    private String getStyle(int health, int maxHealth) {
        double healthPercentage = (double) health / maxHealth;
        if (healthPercentage <= 0.25) return "-fx-accent: red";
        else if (healthPercentage <= 0.50) return "-fx-accent: orange";
        else if (healthPercentage <= 0.75) return "-fx-accent: yellow";
        else return "-fx-accent: green";
    }

    private void updateProgressBar(javafx.scene.control.ProgressBar progressBar, ProgressBarType type, CharacterType character) {
        int current = 0;
        int max = 1;
        switch (character) {
            case PLAYER -> {
                if (type == ProgressBarType.HEALTH) {
                    current = controller.getPlayer().health;
                    max = controller.getPlayer().maxHealth;
                } else if (type == ProgressBarType.EXPERIENCE) {
                    current = controller.getPlayer().experience;
                    max = 100;
                }
            }
            case ENEMY -> {
                if (type == ProgressBarType.HEALTH) {
                    current = controller.getEnemy().health;
                    max = controller.getEnemy().maxHealth;
                }
            }
            case BOSS -> {
                if (type == ProgressBarType.HEALTH) {
                    current = controller.getBoss().health;
                    max = controller.getBoss().maxHealth;
                }
            }
        }
        double progress = (double) current / max;
        
        combatSystem.animateProgressBar(progressBar, progress, () -> {
            switch (type) {
                case HEALTH -> {
                    if (character == CharacterType.PLAYER) {
                        playerHealthLabel.setText("Player Health: " + controller.getPlayer().health + "/" + controller.getPlayer().maxHealth);
                        progressBar.setProgress((double) controller.getPlayer().health / controller.getPlayer().maxHealth);
                        progressBar.setStyle(getStyle(controller.getPlayer().health, controller.getPlayer().maxHealth));

                    } else if (character == CharacterType.ENEMY) {
                        enemyHealthLabel.setText("Enemy Health: " + controller.getEnemy().health + "/" + controller.getEnemy().maxHealth);
                        progressBar.setProgress((double) controller.getEnemy().health / controller.getEnemy().maxHealth);
                        progressBar.setStyle(getStyle(controller.getEnemy().health, controller.getEnemy().maxHealth));
                    } else if (character == CharacterType.BOSS) {
                        enemyHealthLabel.setText("Boss Health: " + controller.getBoss().health + "/" + controller.getBoss().maxHealth);
                        progressBar.setProgress((double) controller.getBoss().health / controller.getBoss().maxHealth);
                        progressBar.setStyle(getStyle(controller.getBoss().health, controller.getBoss().maxHealth));
                    }
                }
                case EXPERIENCE -> {
                    playerExpLabel.setText("Player EXP: " + controller.getPlayer().experience + "/100");
                    playerExpBar.setProgress((double) controller.getPlayer().experience / 100);
                }
            }
        });
    }

    private void updateEnemyInfo() {
        if (controller.isBossFight()) {
            updateProgressBar(enemyHealthBar, ProgressBarType.HEALTH, CharacterType.BOSS);
        } else {
            updateProgressBar(enemyHealthBar, ProgressBarType.HEALTH, CharacterType.ENEMY);
        }
    }

    private void handleButtons(boolean disable) {
        attackButton.setDisable(disable);
        usePotionButton.setDisable(disable);
        runButton.setDisable(disable);
    }

    private void handleNewEnemy() {
        controller.createNewEnemy();
        updateEnemyInfo();
    }

    @Override
    public void start(Stage primaryStage) {
        // Game Info
        Label gameLevelLabel = new Label("Game Level: " + controller.getGameLevel());
        Label goblinsDefeatedLabel = new Label("Goblins Defeated: " + controller.getGoblinsDefeated() + "/3");
        Label playerLabel = new Label("Player Level: " + controller.getPlayer().level);

        VBox gameInfoBox = new VBox(50, gameLevelLabel, goblinsDefeatedLabel, playerLabel);
        gameInfoBox.setPadding(new Insets(10));
        gameInfoBox.setAlignment(Pos.CENTER);

        // Charcaters
        playerChar.setFont(new Font(80));
        enemyChar.setFont(new Font(80));
        bossChar.setFont(new Font(80));

        // Player Info
        playerHealthLabel = new Label("Player Health: " + controller.getPlayer().health + "/" + controller.getPlayer().maxHealth);
        playerExpLabel = new Label("Player EXP: " + controller.getPlayer().experience + "/100");
        playerHealthBar = new ProgressBar((double) controller.getPlayer().health / controller.getPlayer().maxHealth);
        playerExpBar = new ProgressBar((double) controller.getPlayer().experience / 100);
        playerHealthBar.setStyle("-fx-accent: green");
        playerHealthBar.setPrefWidth(200);
        playerExpBar.setPrefWidth(200);

        VBox playerStatus = new VBox(5, playerHealthLabel, playerHealthBar, playerExpLabel, playerExpBar);
        VBox playerSection = new VBox(10, playerChar, playerStatus);
        playerSection.setAlignment(Pos.CENTER);
        playerSection.setPadding(new Insets(10));

        // Enemy Info
        enemyHealthLabel = new Label("Enemy Health: " + controller.getEnemy().health + "/" + controller.getEnemy().maxHealth);
        enemyHealthBar = new ProgressBar((double) controller.getEnemy().health / controller.getEnemy().maxHealth);
        enemyHealthBar.setStyle("-fx-accent: green");
        enemyHealthBar.setPrefWidth(200);

        VBox enemyStatus = new VBox(5, enemyHealthLabel, enemyHealthBar);
        VBox enemySection = new VBox(10, controller.isBossFight() ? bossChar : enemyChar, enemyStatus);
        enemySection.setAlignment(Pos.CENTER);
        enemySection.setPadding(new Insets(10));

        // Battle Arena
        HBox battleArena = new HBox(100, playerSection, enemySection);
        battleArena.setPadding(new Insets(30));
        battleArena.setAlignment(Pos.CENTER);
        battleArena.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: black; -fx-border-width: 2;");

        // Button Style
        String buttonStyle = "-fx-font-size: 16px; -fx-padding: 15 30; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;";
        attackButton.setStyle(buttonStyle);
        usePotionButton.setStyle(buttonStyle);
        runButton.setStyle(buttonStyle.replace("#4CAF50", "#2196F3"));

        // Button Actions
        attackButton.setOnAction(event -> {
            handleButtons(true);
            // Player attack animation and logic
            combatSystem.animatePlayerAttack(playerChar, enemyChar, () -> {
                controller.handlePlayerAttack();
                updateProgressBar(playerHealthBar, ProgressBarType.HEALTH, CharacterType.PLAYER);
                updateProgressBar(playerExpBar, ProgressBarType.EXPERIENCE, CharacterType.PLAYER);
                updateEnemyInfo();
                pause.setOnFinished(null);
                if (controller.isBossFight()) {
                    if (!controller.getBoss().isAlive()) {
                        controller.increaseGameLevel();
                        handleNewEnemy();
                        goblinsDefeatedLabel.setText("Goblins Defeated: " + controller.getGoblinsDefeated() + "/3");
                        gameLevelLabel.setText("Game Level: " + controller.getGameLevel());
                        // Re-enable after handling boss defeat/level up
                        handleButtons(false);
                    } else {
                        // Enemy attack animation and logic
                        combatSystem.animateEnemyAttack(playerChar, bossChar, () -> {
                            controller.handleEnemyAttack();
                            updateProgressBar(playerHealthBar, ProgressBarType.HEALTH, CharacterType.PLAYER);
                            updateEnemyInfo();
                            handleButtons(false);
                        });
                    }
                } else {
                    if (!controller.getEnemy().isAlive()) {
                        // Chain defeat then spawn/animate new enemy so animations don't overlap
                        combatSystem.animateEnemyDefeat(enemyChar, () -> {
                            if (controller.getGoblinsDefeated() >= 3) {
                                combatSystem.animateNewBoss(enemyChar, () -> {
                                    controller.startBossFight();
                                    updateEnemyInfo();
                                    goblinsDefeatedLabel.setText("Goblins Defeated: " + controller.getGoblinsDefeated() + "/3");
                                    handleButtons(false);
                                });
                            } else {
                                combatSystem.animateNewEnemy(enemyChar, () -> {
                                    controller.createNewEnemy();
                                    updateEnemyInfo();
                                    goblinsDefeatedLabel.setText("Goblins Defeated: " + controller.getGoblinsDefeated() + "/3");
                                    handleButtons(false);
                                });
                            }
                        });
                    } else {
                        // Enemy attack animation and logic
                        combatSystem.animateEnemyAttack(playerChar, enemyChar, () -> {
                            controller.handleEnemyAttack();
                            updateProgressBar(playerHealthBar, ProgressBarType.HEALTH, CharacterType.PLAYER);
                            updateProgressBar(playerExpBar, ProgressBarType.EXPERIENCE, CharacterType.PLAYER);
                            updateEnemyInfo();
                            handleButtons(false);
                        });
                    }
                }
            });
        });
        usePotionButton.setOnAction(event -> {
            handleButtons(false);
            controller.handlePotion();
            updateProgressBar(playerHealthBar, ProgressBarType.HEALTH, CharacterType.PLAYER);
            handleButtons(false);
        });

        HBox buttonBox = new HBox(10, attackButton, runButton, usePotionButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        // Layout
        VBox root = new VBox(20, gameInfoBox, battleArena, buttonBox);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 600, 800);

        primaryStage.setTitle("Goblin Battle Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
