package main;

import entity.Entity;
import object.SuperObject;
import tile.TileManager;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A CollisionChecker osztály felelős a játékban történő ütközések detektálásáért
 * és kezeléséért. Vizsgálja az entitások, objektumok és a pálya elemeinek ütközéseit.
 */
public class CollisionChecker {

    Engine eng;

    public CollisionChecker(Engine eng) {
        this.eng = eng;
    }

    /**
     * Ellenőrzi egy entitás ütközését a pálya elemeivel.
     * Az entitás irányának megfelelően vizsgálja a következő pozíciót.
     *
     * @param entity a vizsgálandó entitás
     */
    public void checkTile(Entity entity){
        int entityLeftWorldX = entity.getWorldX() + entity.solidArea.x;
        int entityRightWorldX = entity.getWorldX() + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.getWorldY() + entity.solidArea.y;
        int entityBottomWorldY = entity.getWorldY() + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/ eng.getTileSize();
        int entityRightCol = entityRightWorldX/ eng.getTileSize();
        int entityTopRow = entityTopWorldY/ eng.getTileSize();
        int entityBottomRow = entityBottomWorldY/ eng.getTileSize();

        int tileNum1, tileNum2;

        switch(entity.direction){
            case UP:
                entityTopRow = (entityTopWorldY-entity.getSpeed())/ eng.getTileSize();
                tileNum1 = TileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = TileManager.mapTileNum[entityRightCol][entityTopRow];
                if(eng.tileman.tile[tileNum1].collision|| eng.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case DOWN:
                entityBottomRow = (entityBottomWorldY+entity.getSpeed())/ eng.getTileSize();
                tileNum1 = TileManager.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = TileManager.mapTileNum[entityRightCol][entityBottomRow];
                if(eng.tileman.tile[tileNum1].collision|| eng.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case LEFT:
                entityLeftCol = (entityLeftWorldX-entity.getSpeed())/ eng.getTileSize();
                tileNum1 = TileManager.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = TileManager.mapTileNum[entityLeftCol][entityBottomRow];
                if(eng.tileman.tile[tileNum1].collision|| eng.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case RIGHT:
                entityRightCol = (entityRightWorldX+entity.getSpeed())/ eng.getTileSize();
                tileNum1 = TileManager.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = TileManager.mapTileNum[entityRightCol][entityBottomRow];
                if(eng.tileman.tile[tileNum1].collision|| eng.tileman.tile[tileNum2].collision)
                    entity.collisionOn=true;
                break;
            case SHOOT:
                break;
        }
    }

    /**
     * Ellenőrzi egy entitás ütközését a pálya elemeivel.
     * Az entitás irányának megfelelően vizsgálja a következő pozíciót.
     *
     * @param entity a vizsgálandó entitás
     */
    public int checkObject(Entity entity, boolean isPlayer) {
        int index = 999;
        int it = 0;

        for(SuperObject obj : eng.aSetter.list) {
            if (obj != null) {
                entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
                entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;

                obj.solidArea.x = obj.worldX + obj.solidArea.x;
                obj.solidArea.y = obj.worldY + obj.solidArea.y;

                switch (entity.direction) {
                    case UP -> {
                        entity.solidArea.y -= entity.getSpeed();
                        if (entity.solidArea.intersects(obj.solidArea)) {
                            if (obj.collision) {
                                entity.collisionOn = true;
                            }
                            if (isPlayer) {
                                index = it;
                            }
                        }
                    }
                    case DOWN -> {
                        entity.solidArea.y += entity.getSpeed();
                        if (entity.solidArea.intersects(obj.solidArea)) {
                            if (obj.collision) {
                                entity.collisionOn = true;
                            }
                            if (isPlayer) {
                                index = it;
                            }
                        }
                    }
                    case LEFT -> {
                        entity.solidArea.x -= entity.getSpeed();
                        if (entity.solidArea.intersects(obj.solidArea)) {
                            if (obj.collision) {
                                entity.collisionOn = true;
                            }
                            if (isPlayer) {
                                index = it;
                            }
                        }
                    }
                    case RIGHT -> {
                        entity.solidArea.x += entity.getSpeed();
                        if (entity.solidArea.intersects(obj.solidArea)) {
                            if (obj.collision) {
                                entity.collisionOn = true;
                            }
                            if (isPlayer) {
                                index = it;
                            }
                        }
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
            it++;
        }
        return index;
    }

    /**
     * Ellenőrzi egy entitás ütközését más entitásokkal.
     *
     * @param entity a vizsgálandó entitás
     * @param second az entitások listája, amelyekkel az ütközést vizsgáljuk
     * @return az ütközött entitás indexe vagy 999 ha nincs ütközés
     */
    public int checkEntity(Entity entity, CopyOnWriteArrayList<Entity> second){
        int index=999;
        int it = 0;
        for(Entity target : second) {
            if (target != null) {
                entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
                entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;

                target.solidArea.x = target.getWorldX() + target.solidArea.x;
                target.solidArea.y = target.getWorldY() + target.solidArea.y;

                switch (entity.direction) {
                    case UP -> {
                        entity.solidArea.y -= entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                    }
                    case DOWN -> {
                        entity.solidArea.y += entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                    }
                    case LEFT -> {
                        entity.solidArea.x -= entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                    }
                    case RIGHT -> {
                        entity.solidArea.x += entity.getSpeed();
                        if (entity.solidArea.intersects(target.solidArea)) {
                            entity.collisionOn = true;
                            index = it;
                        }
                    }
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

    /**
     * Ellenőrzi egy entitás ütközését a játékossal.
     *
     * @param entity a vizsgálandó entitás
     */
    public void checkPlayer(Entity entity){
        if (entity != null) {
            entity.solidArea.x = entity.getWorldX() + entity.solidArea.x;
            entity.solidArea.y = entity.getWorldY() + entity.solidArea.y;
            eng.player.solidArea.x = eng.player.getWorldX() + eng.player.solidArea.x;
            eng.player.solidArea.y = eng.player.getWorldY() + eng.player.solidArea.y;

            switch (entity.direction) {
                case UP:
                    entity.solidArea.y -= entity.getSpeed();
                    if (entity.solidArea.intersects(eng.player.solidArea))
                        entity.collisionOn = true;
                    break;
                case DOWN:
                    entity.solidArea.y += entity.getSpeed();
                    if (entity.solidArea.intersects(eng.player.solidArea))
                        entity.collisionOn = true;
                    break;
                case LEFT:
                    entity.solidArea.x -= entity.getSpeed();
                    if (entity.solidArea.intersects(eng.player.solidArea))
                        entity.collisionOn = true;
                    break;
                case RIGHT:
                    entity.solidArea.x += entity.getSpeed();
                    if (entity.solidArea.intersects(eng.player.solidArea))
                        entity.collisionOn = true;
                    break;
            }
            entity.solidArea.x = entity.solidAreaDefaultX;
            entity.solidArea.y = entity.solidAreaDefaultY;
            eng.player.solidArea.x = eng.player.solidAreaDefaultX;
            eng.player.solidArea.y = eng.player.solidAreaDefaultY;
        }
    }
}
