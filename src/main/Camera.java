package main;

import entity.Player;

public class Camera {

    private int x;
    private int y;

    private final int worldWidth;
    private final int worldHeight;
    private final int screenWidth;
    private final int screenHeight;

    public Camera(int worldWidth, int worldHeight, int screenWidth, int screenHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void update(Player player) {
        // ideal camera position (player in centre)
        x = player.getWorldX() - player.getScreenX();
        y = player.getWorldY() - player.getScreenY();

        // clamp for world border
        x = Math.max(0, Math.min(x, worldWidth - screenWidth));
        y = Math.max(0, Math.min(y, worldHeight - screenHeight));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
