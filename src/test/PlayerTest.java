package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.Engine;
import entity.Player;
import object.OBJ_Sword;
import tile.TileManager;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        Engine engine = new Engine();
        player = engine.player;
    }

    @Test
    void testPlayerInitialization() {
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getMaxHealth());
        assertEquals("down", player.direction);

        assertNotNull(player.solidArea);
    }

    @Test
    void testPlayerHealthManagement() {
        player.setHealth(50);
        assertEquals(50, player.getHealth());

        player.setPlayerHealth(150);
        assertEquals(100, player.getHealth());

        player.dealDamage(10);
        assertEquals(90, player.getHealth());
    }

    @Test
    void testPlayerSpeed() {
        player.setSpeed(3);
        assertEquals(3, player.getSpeed());

        player.setSpeed(5);
        assertEquals(5, player.getSpeed());
    }
}

class WeaponTest {
    private OBJ_Sword sword;

    @BeforeEach
    void setUp() {
        Engine engine = new Engine();
        sword = new OBJ_Sword(engine, 100, 100, 50);
    }

    @Test
    void testSwordInitialization() {
        assertNotNull(sword);
        assertEquals(200, sword.getMaxDurability());
        assertEquals(200, sword.getDurability());
        assertEquals("sword", sword.name);
    }

    @Test
    void testSwordDurability() {
        int initialDurability = sword.getDurability();

        Player.isAttacking = false;
        sword.use();
        assertEquals(sword.getDurability(), initialDurability);

        Player.isAttacking = true;
        sword.use();
        assertTrue(sword.getDurability() < initialDurability);

        for(int i = 0; i < 200; i++) {
            sword.use();
        }

        assertEquals(sword.getDurability(), 0);
    }

    @Test
    void testSwordPosition() {
        assertEquals(100, sword.worldX);
        assertEquals(100, sword.worldY);
    }
}

class TileManagerTest {
    private Engine engine;
    private TileManager tileManager;

    @BeforeEach
    void setUp() {
        engine = new Engine();
        tileManager = engine.tileman;
    }

    @Test
    void testTileSetup() {
        // Test tile array initialization
        assertNotNull(tileManager.tile);
        assertEquals(12, tileManager.tile.length);

        // Test specific tiles
        assertTrue(tileManager.tile[0].collision); // Wall
        assertFalse(tileManager.tile[1].collision); // Grass
        assertTrue(tileManager.tile[4].collision); // Water
        assertTrue(tileManager.tile[9].collision); // Tree
    }

    @Test
    void testMapTileNumInitialization() {
        assertNotNull(TileManager.mapTileNum);
        assertEquals(engine.getMaxWorldCol(), TileManager.mapTileNum.length);
        assertEquals(engine.getMaxWorldRow(), TileManager.mapTileNum[0].length);
    }

    @Test
    void testGetTileValidation() {
        assertNotNull(tileManager.getTile(0));
        assertNotNull(tileManager.getTile(11));

        assertEquals(tileManager.getTile(0), tileManager.getTile(-1));
        assertEquals(tileManager.getTile(0), tileManager.getTile(12));
    }
}

class InventoryTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testInventoryInitialization() {
        assertNotNull(engine.player.getInventory());
        assertTrue(engine.player.getInventory().getItems().isEmpty());
    }

    @Test
    void testInventoryCapacity() {
        OBJ_Sword sword1 = new OBJ_Sword(engine, 0, 0, 50);
        OBJ_Sword sword2 = new OBJ_Sword(engine, 0, 0, 50);
        OBJ_Sword sword3 = new OBJ_Sword(engine, 0, 0, 50);
        OBJ_Sword sword4 = new OBJ_Sword(engine, 0, 0, 50);

        engine.player.getInventory().addItem(sword1);
        engine.player.getInventory().addItem(sword2);
        engine.player.getInventory().addItem(sword3);

        assertEquals(3, engine.player.getInventory().getItems().size());

        engine.player.getInventory().addItem(sword4);
        assertEquals(3, engine.player.getInventory().getItems().size());
    }

    @Test
    void testInventoryItemRetrieval() {
        OBJ_Sword sword = new OBJ_Sword(engine, 0, 0, 50);
        engine.player.getInventory().addItem(sword);

        assertEquals(sword, engine.player.getInventory().getCurrent());
    }
}