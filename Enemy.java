public class Enemy {
    private int ATTACK_INCREASE_PER_LEVEL = 5;
    private int DEFAULT_HEALTH = 15;
    private int HEALTH_INCREASE_PER_LEVEL = 15;
    private int DEFAULT_MAX_ATTACK_DAMAGE = 15;
    private int EXPERIENCE_PER_LEVEL = 20;
    private final int MIN_ATTACK_DAMAGE = 5;

    public Enemy(int defaultHealth, int defaultMaxAttackDamage, int healthIncreasePerLevel, int attackIncreasePerLevel, int experiencePerLevel) {
        this.DEFAULT_HEALTH = defaultHealth;
        this.HEALTH_INCREASE_PER_LEVEL = healthIncreasePerLevel;
        this.ATTACK_INCREASE_PER_LEVEL = attackIncreasePerLevel;
        this.DEFAULT_MAX_ATTACK_DAMAGE = defaultMaxAttackDamage;
        this.EXPERIENCE_PER_LEVEL = experiencePerLevel;
    }

    private int health = DEFAULT_HEALTH;
    private int maxHealth = DEFAULT_HEALTH;
    private int maxAttackDamage = DEFAULT_MAX_ATTACK_DAMAGE;

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getExperienceValue(int gameLevel) {
        return EXPERIENCE_PER_LEVEL * gameLevel;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getStatus() {
        return "Enemy - Health: " + health + "/" + maxHealth;
    }

    public void heal() {
        health = DEFAULT_HEALTH;
    }

    public void increaseGameLevel(int gameLevel) {
        health = DEFAULT_HEALTH + (HEALTH_INCREASE_PER_LEVEL * (gameLevel - 1));
        maxHealth = health;
        maxAttackDamage = DEFAULT_MAX_ATTACK_DAMAGE + (ATTACK_INCREASE_PER_LEVEL * (gameLevel - 1));
    }

    public int attack() {
        return (int) (Math.random() * (maxAttackDamage - MIN_ATTACK_DAMAGE + 1)) + MIN_ATTACK_DAMAGE;
    }

    public void levelUp() {
        health += HEALTH_INCREASE_PER_LEVEL;
        maxAttackDamage += ATTACK_INCREASE_PER_LEVEL;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void reset() {
        health = DEFAULT_HEALTH;
        maxAttackDamage = DEFAULT_MAX_ATTACK_DAMAGE;
    }
}
