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
            "add", Arrays.asList("boots", "chest", "door", "dragonenemy", "friendlyenemy",
                    "giantenemy", "key", "smallenemy", "sword"),
            "remove", Arrays.asList("all", "dragonenemy", "friendlyenemy", "giantenemy", "smallenemy"),
            "help", Arrays.asList("reset", "remove", "save", "load", "set", "get", "add", "teleport",
                    "script", "make")
    );

    private String lastCompletion = null;
    private final List<String> currentCompletions = new ArrayList<>();
    private int currentIndex = -1;

    public String complete(String input, boolean isNextCompletion) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String[] parts = input.split("\\s+");

        if (parts.length == 1) {
            return completeMainCommand(parts[0], isNextCompletion);
        }
        else {
            String mainCommand = parts[0].toLowerCase();
            String partial = parts[parts.length - 1].toLowerCase();

            if (!isNextCompletion || !Objects.equals(lastCompletion, partial)) {
                initializeCompletions(mainCommand, partial);
            }
            if (currentIndex >= currentCompletions.size() - 1) {
                currentIndex = -1;
            }

            if (!currentCompletions.isEmpty()) {
                currentIndex++;
                lastCompletion = currentCompletions.get(currentIndex);
                return lastCompletion;
            }

            return partial;
        }
    }

    private String completeMainCommand(String partial, boolean isNextCompletion) {
        if (!isNextCompletion || !Objects.equals(lastCompletion, partial)) {
            initializeMainCompletions(partial);
        }
        if (currentIndex >= currentCompletions.size() - 1) {
            currentIndex = -1;
        }

        if (!currentCompletions.isEmpty()) {
            currentIndex++;
            lastCompletion = currentCompletions.get(currentIndex);
            return lastCompletion;
        }
        return partial;
    }

    private void initializeCompletions(String mainCommand, String partial) {
        currentCompletions.clear();
        currentIndex = -1;
        lastCompletion = partial;

        if (subCommands.containsKey(mainCommand)) {
            List<String> subs = subCommands.get(mainCommand);

            if (!partial.isEmpty()) {
                subs.stream()
                        .filter(sub -> sub.startsWith(partial.substring(0, 1)))
                        .sorted().forEach(currentCompletions::add);
            }
            subs.stream()
                    .filter(sub -> !sub.startsWith(partial.substring(0, Math.min(1, partial.length()))))
                    .sorted().forEach(currentCompletions::add);
        }
    }

    private void initializeMainCompletions(String partial) {
        currentCompletions.clear();
        currentIndex = -1;
        lastCompletion = partial;

        if (!partial.isEmpty()) {
            commands.stream()
                    .filter(cmd -> cmd.startsWith(partial.substring(0, 1)))
                    .sorted().forEach(currentCompletions::add);
        }

        commands.stream()
                .filter(cmd -> !cmd.startsWith(partial.substring(0, Math.min(1, partial.length()))))
                .sorted().forEach(currentCompletions::add);
    }
}