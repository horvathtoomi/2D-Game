package test;

import entity.Entity;
import entity.Inventory;
import entity.Player;
import entity.enemy.SmallEnemy;
import entity.npc.NPC_Wayfarer;
import main.AssetSetter;
import main.CollisionChecker;
import main.Engine;
import object.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Engine Core System Tests")
class EnhancedEngineTest {
    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }

    @Nested
    @DisplayName("Asset Management Tests")
    class AssetManagementTest {
        private AssetSetter assetSetter;

        @BeforeEach
        void setUp() {
            assetSetter = new AssetSetter(engine);
        }

        @Test
        @DisplayName("Should create and add objects correctly")
        void testObjectCreation() {
            assetSetter.list.clear();

            assetSetter.createObject("key", 100, 100);
            assetSetter.createObject("door", 200, 200);
            assetSetter.createObject("chest", 300, 300);
            assetSetter.createObject("boots", 400, 400);
            assetSetter.createObject("sword", 500, 500);

            assertEquals(5, assetSetter.list.size());
            assertInstanceOf(OBJ_Key.class, assetSetter.list.get(0));
            assertInstanceOf(OBJ_Door.class, assetSetter.list.get(1));
            assertInstanceOf(OBJ_Chest.class, assetSetter.list.get(2));
            assertInstanceOf(OBJ_Boots.class, assetSetter.list.get(3));
            assertInstanceOf(OBJ_Sword.class, assetSetter.list.get(4));
        }

        @Test
        @DisplayName("Should spawn chest items correctly")
        void testChestItemSpawning() {
            for(int i = 0; i < 100; i++) {
                assetSetter.list.clear();
                assetSetter.spawnItemFromChest(100, 100);

                assertFalse(assetSetter.list.isEmpty());
                SuperObject spawnedItem = assetSetter.list.getFirst();
                assertTrue(spawnedItem instanceof OBJ_Key ||
                        spawnedItem instanceof OBJ_Boots ||
                        spawnedItem instanceof OBJ_Sword);
            }
        }

        @Test
        @DisplayName("Should handle weapon rarity correctly")
        void testWeaponRarity() {
            int commonCount = 0, uncommonCount = 0, rareCount = 0, legendaryCount = 0;
            int iterations = 100000;

            for(int i = 0; i < iterations; i++) {
                WeaponRarity rarity = assetSetter.determineWeaponRarity();
                switch(rarity) {
                    case COMMON -> commonCount++;
                    case UNCOMMON -> uncommonCount++;
                    case RARE -> rareCount++;
                    case LEGENDARY -> legendaryCount++;
                }
            }
            assertTrue(commonCount > uncommonCount);
            assertTrue(rareCount > legendaryCount);
        }
    }

    @Nested
    @DisplayName("Collision System Tests")
    class CollisionSystemTest {
        private CollisionChecker collisionChecker;

        @BeforeEach
        void setUp() {
            collisionChecker = new CollisionChecker(engine);
        }

        @Test
        @DisplayName("Should detect tile collisions correctly")
        void testTileCollision() {
            Entity entity = new NPC_Wayfarer(engine);

            String[] directions = {"up", "down", "left", "right"};
            for(String direction : directions) {
                entity.direction = direction;
                entity.collisionOn = false;
                collisionChecker.checkTile(entity);
            }
        }

        @Test
        @DisplayName("Should detect entity collisions")
        void testEntityCollision() {
            Entity entity1 = new NPC_Wayfarer(engine);
            Entity entity2 = new NPC_Wayfarer(engine);

            entity1.setWorldX(100);
            entity1.setWorldY(100);
            entity2.setWorldX(100);
            entity2.setWorldY(100);

            engine.addEntity(entity2);

            collisionChecker.checkEntity(entity1, engine.getEntity());
            assertTrue(entity1.collisionOn);
        }
    }

    @Nested
    @DisplayName("Combat System Tests")
    class CombatSystemTest {

        @Test
        @DisplayName("Should process damage correctly")
        void testDamageSystem() {
            int initialHealth = engine.player.getHealth();
            engine.player.setHealth(initialHealth - 20);
            assertEquals(initialHealth - 20, engine.player.getHealth());

            SmallEnemy enemy = new SmallEnemy(engine, 100, 100);
            int enemyInitialHealth = enemy.getHealth();
            enemy.setHealth(enemyInitialHealth - 30);
            assertEquals(enemyInitialHealth - 30, enemy.getHealth());
        }

        @Test
        @DisplayName("Should handle weapon attacks correctly")
        void testWeaponSystem() {
            OBJ_Sword sword = new OBJ_Sword(engine, 0, 0, 50);
            engine.player.getInventory().addItem(sword);

            Player.isAttacking = false;
            engine.player.attack();
            assertTrue(Player.isAttacking);

            int initialDurability = sword.getDurability();
            sword.use();
            assertTrue(sword.getDurability() < initialDurability);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 50, 100})
        @DisplayName("Should handle different damage amounts")
        void testDamageAmounts(int damage) {
            Entity entity = new SmallEnemy(engine, 100, 100);
            entity.setHealth(100 - damage);
            assertEquals(100 - damage, entity.getHealth());
        }
    }

    @Nested
    @DisplayName("Inventory System Tests")
    class InventorySystemTest {

        @Test
        @DisplayName("Should manage inventory correctly")
        void testInventoryManagement() {
            Inventory inventory = engine.player.getInventory();

            OBJ_Sword sword = new OBJ_Sword(engine, 0, 0, 50);
            OBJ_Key key = new OBJ_Key(engine, 0, 0);
            OBJ_Boots boots = new OBJ_Boots(engine, 0, 0);

            inventory.addItem(sword);
            inventory.addItem(key);
            inventory.addItem(boots);

            assertEquals(3, inventory.getItems().size());

            SuperObject firstItem = inventory.getCurrent();
            inventory.rotate();
            assertNotEquals(firstItem, inventory.getCurrent());

            inventory.removeItem("sword");
            assertEquals(2, inventory.getItems().size());
        }

        @Test
        @DisplayName("Should handle item dropping correctly")
        void testItemDropping() {
            Inventory inventory = engine.player.getInventory();
            OBJ_Sword sword = new OBJ_Sword(engine, 0, 0, 50);
            inventory.addItem(sword);

            int initialSize = inventory.getItems().size();
            inventory.drop();
            assertEquals(initialSize - 1, inventory.getItems().size());
        }
    }
}