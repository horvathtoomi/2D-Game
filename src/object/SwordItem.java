package object;

import entity.Player;
import main.Engine;

// --- SWORD ITEM ---
public class SwordItem implements Item {
    private final Components.Damage damage;
    private final Components.Durability durability;
    private final WeaponRarity rarity;
    private final Engine eng;

    public SwordItem(Engine eng, int baseDamage, WeaponRarity rarity) {
        this.eng = eng;
        this.rarity = rarity;
        this.damage = new Components.Damage((int) (baseDamage * rarity.damageMultiplier));
        this.durability = new Components.Durability(200, 2);
    }

    @Override
    public void use(Player player) {
        if (!Player.isAttacking)
            return;

        durability.reduce();
        // Assuming Player has an attack method that takes damage
        // player.attack(damage.value());

        if (durability.isBroken()) {
            player.getInventory().objectExpired(this);
        }
    }

    @Override
    public String getName() {
        return "Sword";
    }

    public WeaponRarity getRarity() {
        return rarity;
    }

    public int getDamage() {
        return damage.value();
    }

    public Components.Durability getDurability() {
        return durability;
    }
}