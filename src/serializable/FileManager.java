package serializable;

import entity.*;
import entity.enemy.DragonEnemy;
import entity.enemy.GiantEnemy;
import entity.enemy.SmallEnemy;
import entity.npc.NPC_Wayfarer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import main.*;
import object.*;

import javax.swing.*;

public class FileManager {

    private FileManager(){}

    private static final Map<String, BiFunction<GamePanel, SerializableEntityState, Entity>> entityCreators = Map.of(
            "NPC_Wayfarer", (gp, state) -> new NPC_Wayfarer(gp),
            "DragonEnemy", (gp, state) -> new DragonEnemy(gp, state.worldX, state.worldY),
            "SmallEnemy", (gp, state) -> new SmallEnemy(gp, state.worldX, state.worldY),
            "GiantEnemy", (gp, state) -> new GiantEnemy(gp, state.worldX, state.worldY)
    );

    private static final Map<String, BiFunction<GamePanel, SerializableObjectState, SuperObject>> objectCreators = Map.of(
            "key", (gp, state) -> new OBJ_Key(gp, state.worldX, state.worldY),
            "door", (gp, state) -> new OBJ_Door(gp, state.worldX, state.worldY),
            "chest", (gp, state) -> new OBJ_Chest(gp, state.worldX, state.worldY),
            "boots", (gp, state) -> new OBJ_Boots(gp, state.worldX, state.worldY)
    );

    public static void saveGameState(GamePanel gp, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new SerializablePlayerState(gp.player, gp.getGameDifficulty()));
            oos.writeObject(new ArrayList<>(gp.getEntity().stream().filter(Objects::nonNull).map(SerializableEntityState::new).collect(Collectors.toList())));
            oos.writeObject(new ArrayList<>(gp.aSetter.list.stream().filter(Objects::nonNull).map(SerializableObjectState::new).collect(Collectors.toList())));
        }
    }

    public static void loadGameState(GamePanel gp, String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            SerializablePlayerState playerState = (SerializablePlayerState) ois.readObject();
            updatePlayerState(gp.player, playerState);
            gp.setGameDifficulty(playerState.difficulty);
            List<SerializableEntityState> entityStates = (List<SerializableEntityState>) ois.readObject();
            gp.setEntities(entityStates.stream().map(state -> createEntityFromState(gp, state)).filter(Objects::nonNull).collect(Collectors.toCollection(CopyOnWriteArrayList::new)));
            List<SerializableObjectState> objectStates = (List<SerializableObjectState>) ois.readObject();
            gp.aSetter.list = objectStates.stream().map(state -> createObjectFromState(gp, state)).filter(Objects::nonNull).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        }
    }

    private static void updatePlayerState(Player player, SerializablePlayerState state) {
        player.setWorldX(state.worldX);
        player.setWorldY(state.worldY);
        player.setSpeed(state.speed);
        player.direction = state.direction;
        player.setHealth(state.health);
    }

    private static Entity createEntityFromState(GamePanel gp, SerializableEntityState state) {
        BiFunction<GamePanel, SerializableEntityState, Entity> creator = entityCreators.get(state.type);
        if (creator == null) {
            return null;
        }
        Entity entity = creator.apply(gp, state);
        if (entity != null) {
            entity.setWorldX(state.worldX);
            entity.setWorldY(state.worldY);
            entity.setSpeed(state.speed);
            entity.direction = state.direction;
            entity.setHealth(state.health);
        }
        return entity;
    }

    private static SuperObject createObjectFromState(GamePanel gp, SerializableObjectState state) {
        BiFunction<GamePanel, SerializableObjectState, SuperObject> creator = objectCreators.get(state.name);
        if (creator == null) {
            return null;
        }
        SuperObject obj = creator.apply(gp, state);
        if (obj != null) {
            obj.worldX = state.worldX;
            obj.worldY = state.worldY;
            obj.collision = state.collision;
        }
        return obj;
    }

    public static void saveGame(GamePanel gp) {
        System.out.println("---------------");
        System.out.println("|Saving pending|");
        System.out.println("---------------");

        gp.setGameState(GamePanel.GameState.SAVE);

        // GETTING FILENAME
        String filename = JOptionPane.showInputDialog(gp, "Enter a name for your save file:");

        if (filename != null && !filename.trim().isEmpty()) {
            try {
                File saveDir = new File("res/save");
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                File saveFile = new File(saveDir, filename + ".dat");

                if (saveFile.exists()) {
                    int choice = JOptionPane.showConfirmDialog(gp,
                            "A save file with this name already exists. Do you want to overwrite it?",
                            "Overwrite Save",
                            JOptionPane.YES_NO_OPTION);
                    if (choice != JOptionPane.YES_OPTION) {
                        System.out.println("Save cancelled.");
                        gp.setGameState(GamePanel.GameState.RUNNING);
                        return;
                    }
                }

                FileManager.saveGameState(gp, saveFile.getPath());
                System.out.println("Game saved successfully.");
                JOptionPane.showMessageDialog(gp, "Game saved successfully.");
            } catch (IOException e) {
                System.err.println("Error saving game: " + e.getMessage());
                JOptionPane.showMessageDialog(gp, "Error saving game: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Save cancelled.");
        }
        gp.setGameState(GamePanel.GameState.RUNNING);
    }

    public static void loadGame(GamePanel gp) {
        System.out.println("-------------");
        System.out.println("|Load pending|");
        System.out.println("-------------");
        gp.setGameState(GamePanel.GameState.LOAD);

        // GETTING FILENAME
        File saveDir = new File("res/save");
        if (!saveDir.exists() || saveDir.list() == null || Objects.requireNonNull(saveDir.list()).length == 0) {
            JOptionPane.showMessageDialog(gp, "No save files found.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
            gp.setGameState(GamePanel.GameState.RUNNING);
        }

        String[] saveFiles = saveDir.list((dir, name) -> name.endsWith(".dat"));
        assert saveFiles != null;
        String selectedFile = (String) JOptionPane.showInputDialog(gp, "Choose a save file to load:", "Load Game", JOptionPane.QUESTION_MESSAGE, null, saveFiles, saveFiles[0]);

        if (selectedFile != null) {
            try {
                FileManager.loadGameState(gp, new File(saveDir, selectedFile).getPath());
                System.out.println("Game loaded successfully.");
                JOptionPane.showMessageDialog(gp, "Game loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading game: " + e.getMessage());
                JOptionPane.showMessageDialog(gp, "Error loading game: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
            System.out.println("Load cancelled.");
        gp.setGameState(GamePanel.GameState.RUNNING);
    }

}



class SerializablePlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    int worldX, worldY, speed, health;
    String direction;
    GamePanel.GameDifficulty difficulty;

    SerializablePlayerState(Player player, GamePanel.GameDifficulty difficulty) {
        this.worldX = player.getWorldX();
        this.worldY = player.getWorldY();
        this.speed = player.getSpeed();
        this.direction = player.direction;
        this.health = player.getHealth();
        this.difficulty = difficulty;
    }
}

class SerializableEntityState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String type;
    int worldX, worldY, speed, health;
    String direction;

    SerializableEntityState(Entity entity) {
        this.type = entity.getClass().getSimpleName();
        this.worldX = entity.getWorldX();
        this.worldY = entity.getWorldY();
        this.speed = entity.getSpeed();
        this.direction = entity.direction;
        this.health = entity.getHealth();
    }
}

class SerializableObjectState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    String name;
    int worldX, worldY;
    boolean collision;

    SerializableObjectState(SuperObject obj) {
        this.name = obj.name;
        this.worldX = obj.worldX;
        this.worldY = obj.worldY;
        this.collision = obj.collision;
    }

}