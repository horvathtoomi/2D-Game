package object;

import entity.Player;

public class KeyItem implements Item {
    @Override
    public void use(Player player) {
        /* Keys are passive/contextual */
    }

    @Override
    public String getName() {
        return "Key";
    }
}