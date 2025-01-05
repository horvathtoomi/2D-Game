package object;

import main.Engine;

public class Pistol extends Shooter {

    public Pistol(Engine eng, int x, int y) {
        super(eng, x, y, "pistol", "pistol", 5);
        setPistolValues();
    }

    public Pistol(Engine eng, int x, int y, int leftOverAmmo, int magAmmo){
        super(eng, x, y, "pistol", "pistol", 5);
        setPistolValues();
        currentMagSize = magAmmo;
        remainingAmmo = leftOverAmmo;
    }

    private void setPistolValues(){
        maxMagSize = 12;
        currentMagSize = maxMagSize;
        remainingAmmo = 24;
    }
}