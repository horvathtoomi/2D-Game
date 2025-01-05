package object;

import main.Engine;
import main.logger.GameLogger;

public abstract class Shooter extends Weapon {
    protected int remainingAmmo;
    protected int maxMagSize;
    protected int currentMagSize;
    protected int fireRate;
    protected int coolDown;
    private static final String LOG_CONTEXT = "[SHOOTER]";

    public int getRemainingAmmo() {
        return remainingAmmo;
    }
    public int getCurrentMagSize() {
        return currentMagSize;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getFireRate() {
        return fireRate;
    }

    public Shooter(Engine eng, int x, int y, String name, String imageName, int damage) {
        super(eng, x, y, name, imageName, damage, Integer.MAX_VALUE, 30);
        setDurability(100);
    }

    @Override
    public void use() {
        if (currentMagSize > 0) {
            currentMagSize--;
        } else {
            GameLogger.warn(LOG_CONTEXT, "MAG IS EMPTY, RELOAD!");
        }
    }

    public void reload() {
        int neededAmmo = maxMagSize - currentMagSize;
        if (neededAmmo == 0) {
            return;
        } else if (neededAmmo >= remainingAmmo) {
            currentMagSize += remainingAmmo;
            remainingAmmo = 0;
        } else {
            currentMagSize += neededAmmo;
            remainingAmmo -= neededAmmo;
        }
    }

    public boolean canShoot(){
        return currentMagSize > 0;
    }

}