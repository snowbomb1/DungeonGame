public class Player {
    private final int POTION_HEAL = 20;
    private final int EXPERIENCE_REQUIRED = 100;
    private final int ATTACK_INCREASE_PER_LEVEL = 5;
    private final int DEFAULT_HEALTH = 30;
    private final int HEALTH_INCREASE_PER_LEVEL = 20;
    private final int DEFAULT_MAX_ATTACK_DAMAGE = 15;
    private final int MIN_ATTACK_DAMAGE = 5;

    private int health = DEFAULT_HEALTH;
    private int maxHealth = DEFAULT_HEALTH;
    private int maxAttackDamage = DEFAULT_MAX_ATTACK_DAMAGE;
    private int experience = 0;
    private int level = 1;
    private int potions = 3;

    public void increaseGameLevel(int gameLevel) {
        potions = 3 + (gameLevel - 1);
        health = maxHealth;
    }

    public String getStatus() {
        return "Player - Level: " + level + ", Health: " + health + "/" + maxHealth + ", Experience: " + experience + "/" + EXPERIENCE_REQUIRED + ", Potions: " + potions;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int attack() {
        return (int) (Math.random() * (maxAttackDamage - MIN_ATTACK_DAMAGE + 1)) + MIN_ATTACK_DAMAGE;
    }

    public void heal() {
        if (potions <= 0) {
            System.out.println("No potions left!");
            return;
        }
        potions--;
        health += POTION_HEAL;
        if (health > maxHealth) {
            health = maxHealth;
        }
        System.out.println("You used a potion and healed " + POTION_HEAL + " health.");
    }

    public void levelUp() {
        maxHealth += HEALTH_INCREASE_PER_LEVEL;
        health = maxHealth;
        maxAttackDamage += ATTACK_INCREASE_PER_LEVEL;
        level++;
    }

    public void gainExperience(int exp) {
        experience += exp;
        if (experience >= EXPERIENCE_REQUIRED) {
            experience -= EXPERIENCE_REQUIRED;
            levelUp();
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void reset() {
        health = DEFAULT_HEALTH;
        maxHealth = DEFAULT_HEALTH;
        maxAttackDamage = DEFAULT_MAX_ATTACK_DAMAGE;
        experience = 0;
        level = 1;
    }
}
