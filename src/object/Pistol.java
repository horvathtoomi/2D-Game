package object;

import main.Engine;

import static java.lang.Integer.MAX_VALUE;

public class Pistol extends Shooter{

    public Pistol(Engine eng, int x, int y, String name, String imageName, int damage) {
        super(eng,x,y,name,imageName,damage,MAX_VALUE);
    }
}
