package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import main.Engine;
import main.InputHandler;
import entity.Player;
import tile.TileManager;

@ExtendWith(MockitoExtension.class)
class PlayerTest {
    @Mock
    private Engine engineMock;

    @Mock
    private InputHandler inputHandlerMock;

    private Player player;

    @BeforeEach
    void setUp() {
        // Mock engine getters
        when(engineMock.getTileSize()).thenReturn(48);
        when(engineMock.getScreenWidth()).thenReturn(768);
        when(engineMock.getScreenHeight()).thenReturn(576);

        player = new Player(engineMock, inputHandlerMock);
    }

    @Test
    void testPlayerInitialization() {
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getMaxHealth());
        assertEquals(3, player.getSpeed());
        assertEquals("down", player.direction);
    }

    @Test
    void testPlayerMovement() {
        // Given
        when(inputHandlerMock.upPressed).thenReturn(true);

        // When
        player.update();

        // Then
        assertEquals("up", player.direction);
        assertEquals(player.getWorldY() - player.getSpeed(), player.getWorldY());
    }

    @Test
    void testPlayerHealthReduction() {
        // When
        player.setHealth(50);

        // Then
        assertEquals(50, player.getHealth());
        assertTrue(player.getHealth() <= player.getMaxHealth());
    }
}

@ExtendWith(MockitoExtension.class)
class WeaponTest {
    @Mock
    private Engine engineMock;

    private OBJ_Sword sword;

    @BeforeEach
    void setUp() {
        when(engineMock.getTileSize()).thenReturn(48);
        sword = new OBJ_Sword(engineMock, 0, 0, 50);
    }

    @Test
    void testWeaponDamageCalculation() {
        assertEquals(50, sword.getDamage());
    }

    @Test
    void testWeaponDurabilityReduction() {
        int initialDurability = sword.getDurability();
        sword.use();
        assertTrue(sword.getDurability() < initialDurability);
    }
}

@ExtendWith(MockitoExtension.class)
class EnemyTest {
    @Mock
    private Engine engineMock;

    private SmallEnemy enemy;

    @BeforeEach
    void setUp() {
        when(engineMock.getTileSize()).thenReturn(48);
        enemy = new SmallEnemy(engineMock, 100, 100);
    }

    @Test
    void testEnemyInitialization() {
        assertEquals(100, enemy.getHealth());
        assertEquals(100, enemy.getMaxHealth());
        assertTrue(enemy.getSpeed() > 0);
    }

    @Test
    void testEnemyTakeDamage() {
        enemy.setHealth(enemy.getHealth() - 20);
        assertEquals(80, enemy.getHealth());
    }
}

@ExtendWith(MockitoExtension.class)
class TileManagerTest {
    @Mock
    private Engine engineMock;

    private TileManager tileManager;

    @BeforeEach
    void setUp() {
        when(engineMock.getTileSize()).thenReturn(48);
        when(engineMock.getMaxWorldCol()).thenReturn(100);
        when(engineMock.getMaxWorldRow()).thenReturn(100);
        tileManager = new TileManager(engineMock);
    }

    @Test
    void testTileInitialization() {
        assertNotNull(tileManager.tile);
        assertEquals(12, tileManager.tile.length);
    }

    @Test
    void testCollisionTiles() {
        assertTrue(tileManager.tile[0].collision); // Wall tile
        assertTrue(tileManager.tile[4].collision); // Water tile
        assertFalse(tileManager.tile[1].collision); // Grass tile
    }
}