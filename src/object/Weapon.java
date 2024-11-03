package object;

import main.GamePanel;

public abstract class Weapon extends SuperObject {
    protected int damage;
    protected int range;  // Range in tiles
    protected int attackSpeed; // Frames between attacks
    protected int currentCooldown = 0;
    protected int attackDuration = 30; // Duration of attack animation in frames
    protected int attackCooldown = 30;
    protected int currentAttackFrame = 0;
    private final int ATTACK_COOLDOWN_TIME = 30;
    protected boolean isAttacking = false;

    public Weapon(GamePanel gp, int x, int y, String name, String imageName, int damage, int range, int attackSpeed) {
        super(gp, x, y, name, imageName);
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
    }

    public int getDamage() {return damage;}
    public int getAttackSpeed() {return attackSpeed;}
    public int getRange() {return range;}


    public void update() {
        if(isAttacking) {
            attackCooldown--;
        }
        if(attackCooldown==0 && isAttacking) {
            attackCooldown = ATTACK_COOLDOWN_TIME;
            attackDuration--;
        }
    }
}