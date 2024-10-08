package main;

import entity.*;
import object.SuperObject;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp=gp;

    }

    public void checkTile(Entity entity){
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        int tileNum1, tileNum2;

        switch(entity.direction){
            case "up":
                entityTopRow = (entityTopWorldY-entity.speed)/gp.tileSize;
                tileNum1 = gp.tileman.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileman.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY+entity.speed)/gp.tileSize;
                tileNum1 = gp.tileman.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileman.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX-entity.speed)/gp.tileSize;
                tileNum1 = gp.tileman.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileman.mapTileNum[entityLeftCol][entityBottomRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "right":
                entityRightCol = (entityRightWorldX+entity.speed)/gp.tileSize;
                tileNum1 = gp.tileman.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileman.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
        }
    }

    public int checkObject(Entity entity, boolean is_a_player){
        int index=999;
        int it = 0;
        for(SuperObject aut : gp.aSetter.lista) {
            if (aut != null) {
                //Get entity's solid area
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                //Get the object's solid area position
                aut.solidArea.x = aut.worldX + aut.solidArea.x;
                aut.solidArea.y = aut.worldY + aut.solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                            break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                            break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                            break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                aut.solidArea.x = aut.solidAreaDefaultX;
                aut.solidArea.y = aut.solidAreaDefaultY;
            }
            it++;
        }
        return index;
    }
}
