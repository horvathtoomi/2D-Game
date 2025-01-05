package object;

import main.Engine;

public class Rifle extends Shooter {

    public Rifle(Engine eng, int x, int y) {
        super(eng, x, y, "rifle", "rifle", 25);
        setRifleValues();
    }

    public Rifle(Engine eng, int x, int y, int leftOverAmmo, int inMagAmmo){
        super(eng, x, y, "rifle", "rifle", 25);
        setRifleValues();
        currentMagSize = inMagAmmo;
        remainingAmmo = leftOverAmmo;
    }

    private void setRifleValues(){
        maxMagSize = 30;
        currentMagSize = maxMagSize;
        remainingAmmo = 60;
        fireRate = 5;
        coolDown = 5;
    }

}