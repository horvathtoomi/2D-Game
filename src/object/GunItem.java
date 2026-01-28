package object;

import entity.Player;
import main.Engine;
import main.logger.GameLogger;

/**
 * Abstract base class for gun items.
 * Uses Magazine component for ammo management.
 */
public abstract class GunItem implements Item {
    protected final Components.Magazine magazine;
    protected final Engine eng;
    protected int cooldownTimer = 0;
    protected final int cooldownMax;

    public GunItem(Engine eng, int maxMag, int reserve, int cooldownMax) {
        this.eng = eng;
        this.magazine = new Components.Magazine(maxMag, reserve);
        this.cooldownMax = cooldownMax;
    }

    public GunItem(Engine eng, int maxMag, int currentMag, int reserve, int cooldownMax) {
        this.eng = eng;
        this.magazine = new Components.Magazine(maxMag, currentMag, reserve);
        this.cooldownMax = cooldownMax;
    }

    @Override
    public void use(Player player) {
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return;
        }

        if (magazine.canShoot()) {
            shootLogic(player);
            magazine.shoot();
            cooldownTimer = cooldownMax;
        } else {
            GameLogger.warn("[GUN]", "Click! Empty mag.");
            // Optional: Auto reload
            // magazine.reload();
        }
    }

    protected abstract void shootLogic(Player player);

    public void reload() {
        magazine.reload();
    }

    public Components.Magazine getMagazine() {
        return magazine;
    }
}