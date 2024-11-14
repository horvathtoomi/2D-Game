package main.console;

import java.util.*;

public class CommandCompleter {

    private final List<String> commands = Arrays.asList(
            "help", "reset", "exit", "exit_game", "remove", "save",
            "load", "set", "get", "add", "teleport", "script", "make"
    );

    private final Map<String, List<String>> subCommands = Map.of(
            "set", Arrays.asList("player", "entity", "speed", "health", "maxhealth"),
            "get", Arrays.asList("player", "health", "speed", "maxhealth"),
            "add", Arrays.asList("smallenemy", "giantenemy", "dragonenemy", "friendlyenemy",
                    "key", "door", "boots", "chest", "sword")
    );

    public String complete(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] parts = input.split("\\s+");
        if (parts.length == 1) {
            return commands.stream()
                    .filter(cmd -> cmd.startsWith(parts[0].toLowerCase()))
                    .findFirst().orElse(parts[0]);
        } else {
            String mainCommand = parts[0].toLowerCase();
            String partial = parts[parts.length - 1].toLowerCase();

            List<String> subs = subCommands.get(mainCommand);
            if (subs != null) {
                return subs.stream()
                        .filter(sub -> sub.startsWith(partial))
                        .findFirst().orElse(partial);
            }
        }
        return input;
    }
}