package object;

import main.logger.GameLogger;

public class Components {
    // 1. Durability Component (for wearables and melee weapons)
    public static class Durability {
        private final int max;
        private int current;
        private final int usageDamage;

        public Durability(int max, int usageDamage) {
            this.max = max;
            this.current = max;
            this.usageDamage = usageDamage;
        }

        public void reduce() {
            current = Math.max(0, current - usageDamage);
        }

        public boolean isBroken() {
            return current <= 0;
        }

        public int getCurrent() {
            return current;
        }

        public int getMax() {
            return max;
        }
    }

    // 2. Magazine Component (Ammo logic)
    public static class Magazine {
        private final int maxMagSize;
        private int currentMagSize;
        private int remainingAmmo;

        public Magazine(int maxMagSize, int remainingAmmo) {
            this.maxMagSize = maxMagSize;
            this.currentMagSize = maxMagSize;
            this.remainingAmmo = remainingAmmo;
        }

        public Magazine(int maxMagSize, int currentMag, int remainingAmmo) {
            this.maxMagSize = maxMagSize;
            this.currentMagSize = currentMag;
            this.remainingAmmo = remainingAmmo;
        }

        public boolean canShoot() {
            return currentMagSize > 0;
        }

        public void shoot() {
            if (canShoot()) {
                currentMagSize--;
            } else {
                GameLogger.warn("[MAGAZINE]", "Cannot shoot: empty mag");
            }
        }

        public void reload() {
            int needed = maxMagSize - currentMagSize;
            int toReload = Math.min(needed, remainingAmmo);
            currentMagSize += toReload;
            remainingAmmo -= toReload;
            GameLogger.info("[MAGAZINE]", "Reloaded: " + toReload + " bullets");
        }

        public int getCurrentMag() {
            return currentMagSize;
        }

        public int getReserve() {
            return remainingAmmo;
        }

        public int getMaxMagSize() {
            return maxMagSize;
        }
    }

    // 3. Damage record (simple value holder)
    public record Damage(int value) {
    }
}