package object;

import main.Engine;

public class Rifle extends Shooter {

    public Rifle(Engine eng, int x, int y) {
        super(eng, x, y, "rifle", "rifle", 25);
        setRifleValues();
    }

    private void setRifleValues(){
        maxMagSize = 30;
        currentMagSize = maxMagSize;
        remainingAmmo = 60;
    }
}