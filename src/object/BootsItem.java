package object;

import entity.Player;
import main.Engine;

public class BootsItem implements Item {
    private final Components.Durability durability;
    private final Engine eng;

    public BootsItem(Engine eng) {
        this.eng = eng;
        this.durability = new Components.Durability(60 * eng.getFPS(), 1);
    }

    @Override
    public void use(Player player) {
        // Original logic: boots degrade when attacking? Preserving logic.
        if (Player.isAttacking) {
            durability.reduce();
        }
        if (durability.isBroken()) {
            player.getInventory().objectExpired(this);
        }
    }

    @Override
    public String getName() {
        return "Boots";
    }

    public Components.Durability getDurability() {
        return durability;
    }
}