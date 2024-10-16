package main;

import entity.*;
import object.SuperObject;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp=gp;

    }

    public void checkTile(Entity entity){
        int entityLeftWorldX = entity.getWorldX() + entity.solidArea.x;
        int entityRightWorldX = entity.getWorldX() + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.getWorldY() + entity.solidArea.y;
        int entityBottomWorldY = entity.getWorldY() + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/gp.getTileSize();
        int entityRightCol = entityRightWorldX/gp.getTileSize();
        int entityTopRow = entityTopWorldY/gp.getTileSize();
        int entityBottomRow = entityBottomWorldY/gp.getTileSize();

        int tileNum1, tileNum2;

        switch(entity.direction){
            case "up":
                entityTopRow = (entityTopWorldY-entity.getSpeed())/gp.getTileSize();
                tileNum1 = gp.tileman.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileman.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY+entity.getSpeed())/gp.getTileSize();
                tileNum1 = gp.tileman.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileman.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX-entity.getSpeed())/gp.getTileSize();
                tileNum1 = gp.tileman.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileman.mapTileNum[entityLeftCol][entityBottomRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "right":
                entityRightCol = (entityRightWorldX+entity.getSpeed())/gp.getTileSize();
                tileNum1 = gp.tileman.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileman.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileman.tile[tileNum1].collision||gp.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case "shoot":

                break;
        }
    }

    public int checkObject(Entity entity, boolean is_a_player){
        int index=999;
        int it = 0;
        for(SuperObject aut : gp.aSetter.list) {
            if (aut != null) {
                //Get entity's solid area
                entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
                entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
                //Get the object's solid area position
                aut.solidArea.x = aut.worldX + aut.solidArea.x;
                aut.solidArea.y = aut.worldY + aut.solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.getSpeed();
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.getSpeed();
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                            break;
                    case "left":
                        entity.solidArea.x -= entity.getSpeed();
                        if (entity.solidArea.intersects(aut.solidArea)) {
                            if (aut.collision)
                                entity.collisionOn = true;
                            if (is_a_player)
                                index = it;
                        }
                            break;
                    case "right":
                        entity.solidArea.x += entity.getSpeed();
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

    //Check npc/monster collision
    public int checkEntity(Entity entity, CopyOnWriteArrayList<Entity> second){
        int index=999;
        int it = 0;
        for(Entity target : second) {
            if (target != null) {
                //Get entity's solid area
                entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
                entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
                //Get the object's solid area position
                target.solidArea.x = target.getWorldX() + target.solidArea.x;
                target.solidArea.y = target.getWorldY() + target.solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target.solidArea.x = target.solidAreaDefaultX;
                target.solidArea.y = target.solidAreaDefaultY;
            }
            it++;
        }
        return index;
    }

    public void checkPlayer(Entity entity){
        if (entity != null) {
            //Get entity's solid area
            entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
            entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
            //Get the object's solid area position
            gp.player.solidArea.x = gp.player.getWorldX() + gp.player.solidArea.x;
            gp.player.solidArea.y = gp.player.getWorldY() + gp.player.solidArea.y;

            switch (entity.direction) {
                case "up":
                    entity.solidArea.y -= entity.getSpeed();
                    if (entity.solidArea.intersects(gp.player.solidArea))
                        entity.collisionOn = true;
                    break;
                case "down":
                    entity.solidArea.y += entity.getSpeed();
                    if (entity.solidArea.intersects(gp.player.solidArea))
                        entity.collisionOn = true;
                    break;
                case "left":
                    entity.solidArea.x -= entity.getSpeed();
                    if (entity.solidArea.intersects(gp.player.solidArea))
                        entity.collisionOn = true;
                    break;
                case "right":
                    entity.solidArea.x += entity.getSpeed();
                    if (entity.solidArea.intersects(gp.player.solidArea))
                        entity.collisionOn = true;
                    break;
            }
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;
            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
        }
    }
}
