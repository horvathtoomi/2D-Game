package test;

import static org.junit.jupiter.api.Assertions.*;

import object.WeaponRarity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.Engine;
import main.CollisionChecker;
import map.TileColor;
import map.ColorAnalyzer;
import entity.Entity;
import entity.npc.NPC_Wayfarer;
import java.awt.Color;

class CollisionCheckerTest {
    private Engine engine;
    private CollisionChecker collisionChecker;

    @BeforeEach
    void setUp() {
        engine = new Engine();
        collisionChecker = new CollisionChecker(engine);
    }

    @Test
    void testEntityTileCollision() {
        Entity entity = new NPC_Wayfarer(engine);
        entity.setWorldX(0);
        entity.setWorldY(0);

        collisionChecker.checkTile(entity);
        // Edge of map should have collision
        assertTrue(entity.collisionOn);

        // Move entity to non-collision tile
        entity.setWorldX(engine.getTileSize() * 2);
        entity.setWorldY(engine.getTileSize() * 2);
        entity.collisionOn = false;

        collisionChecker.checkTile(entity);
        assertTrue(entity.collisionOn);
    }

    @Test
    void testPlayerCollision() {
        Entity entity = new NPC_Wayfarer(engine);
        entity.setWorldX(engine.player.getWorldX());
        entity.setWorldY(engine.player.getWorldY());

        collisionChecker.checkPlayer(entity);
        assertTrue(entity.collisionOn);
    }
}

class ColorSystemTest {

    @Test
    void testTileColorInitialization() {
        TileColor tileColor = new TileColor(255, 0, 0, 1);
        assertEquals(255, tileColor.r);
        assertEquals(0, tileColor.g);
        assertEquals(0, tileColor.b);
        assertEquals(1, tileColor.tileNumber);
    }

    @Test
    void testColorDistance() {
        Color color1 = new Color(255, 0, 0);
        TileColor color2 = new TileColor(255, 0, 0, 1);

        double distance = ColorAnalyzer.calculateColorDistance(color1, color2);
        assertEquals(0.0, distance, 0.001);

        Color color3 = new Color(0, 255, 0);
        double greenDistance = ColorAnalyzer.calculateColorDistance(color3, color2);
        assertTrue(greenDistance > 0);
    }

    @Test
    void testGetClosestTile() {
        Color grassColor = new Color(37, 166, 22); // Light green
        int tileNumber = ColorAnalyzer.getClosestTile(grassColor);
        assertEquals(1, tileNumber); // Assuming 1 is grass tile

        Color waterColor = new Color(80, 119, 219); // Blue
        tileNumber = ColorAnalyzer.getClosestTile(waterColor);
        assertEquals(4, tileNumber); // Assuming 4 is water tile
    }
}

class NPCTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testWayfarerInitialization() {
        NPC_Wayfarer wayfarer = new NPC_Wayfarer(engine);
        assertEquals("NPC_Wayfarer", wayfarer.getName());
        assertEquals("down", wayfarer.direction);
        assertEquals(1, wayfarer.getSpeed());
    }

    @Test
    void testWayfarerPositioning() {
        NPC_Wayfarer wayfarer = new NPC_Wayfarer(engine, 100, 200);
        assertEquals(100, wayfarer.getWorldX());
        assertEquals(200, wayfarer.getWorldY());
    }

    @Test
    void testWayfarerMovement() {
        NPC_Wayfarer wayfarer = new NPC_Wayfarer(engine);

        // Force action update
        wayfarer.actionLockCounter = 120;
        wayfarer.setAction();

        // Direction should change after action
        assertTrue(wayfarer.direction.equals("up") ||
                wayfarer.direction.equals("down") ||
                wayfarer.direction.equals("left") ||
                wayfarer.direction.equals("right"));
    }
}

class EntityTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Test
    void testEntityScreenPosition() {
        Entity entity = new NPC_Wayfarer(engine);
        entity.setWorldX(100);
        entity.setWorldY(100);

        // Test screen position calculations
        entity.setScreenX(entity.getWorldX() - engine.player.getWorldX() + engine.player.getScreenX());
        entity.setScreenY(entity.getWorldY() - engine.player.getWorldY() + engine.player.getScreenY());

        assertTrue(entity.getScreenX() >= -engine.getTileSize());
        assertTrue(entity.getScreenY() >= -engine.getTileSize());
    }

    @Test
    void testEntityBoundaries() {
        Entity entity = new NPC_Wayfarer(engine);

        // Test world boundaries
        entity.setWorldX(-100);
        entity.setWorldY(-100);

        assertTrue(entity.getWorldX() >= 0);
        assertTrue(entity.getWorldY() >= 0);
    }
}

class WeaponRarityTest {

    @Test
    void testCommonWeaponStats() {
        assertEquals(1.0f, WeaponRarity.COMMON.damageMultiplier);
        assertEquals(Color.GRAY, WeaponRarity.COMMON.color);
    }

    @Test
    void testUncommonWeaponStats() {
        assertEquals(1.2f, WeaponRarity.UNCOMMON.damageMultiplier);
        assertEquals(Color.GREEN, WeaponRarity.UNCOMMON.color);
    }

    @Test
    void testRareWeaponStats() {
        assertEquals(1.5f, WeaponRarity.RARE.damageMultiplier);
        assertEquals(Color.BLUE, WeaponRarity.RARE.color);
    }

    @Test
    void testLegendaryWeaponStats() {
        assertEquals(2.0f, WeaponRarity.LEGENDARY.damageMultiplier);
        assertEquals(Color.ORANGE, WeaponRarity.LEGENDARY.color);
    }

    @Test
    void testWeaponDamageCalculation() {
        float baseDamage = 100;

        assertEquals(100, baseDamage * WeaponRarity.COMMON.damageMultiplier);

        assertEquals(150, baseDamage * WeaponRarity.RARE.damageMultiplier);

        assertEquals(200, baseDamage * WeaponRarity.LEGENDARY.damageMultiplier);
    }
}