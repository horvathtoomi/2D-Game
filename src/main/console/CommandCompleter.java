package main.console;

import java.util.*;
import java.io.File;

/**
 * A parancs kiegészítő osztály, amely kezeli a konzol parancsok automatikus kiegészítését.
 * Segíti a felhasználót a parancsok gyorsabb és pontosabb beírásában.
 */
public class CommandCompleter {

    private String lastCompletion = null;
    private final List<String> currentCompletions = new ArrayList<>(getScriptFiles());
    private int currentIndex = -1;

    private final List<String> commands = Arrays.asList(
            "help", "reset", "exit", "exit_game", "remove", "save",
            "load", "set", "get", "add", "teleport", "script", "make");

    private final Map<String, List<String>> subCommands = Map.of(
            "set", Arrays.asList("player", "entity", "speed", "health", "maxhealth"),
            "get", Arrays.asList("player", "health", "speed", "maxhealth"),
            "add", Arrays.asList("boots", "chest", "door", "dragonenemy", "friendlyenemy",
                    "giantenemy", "key", "smallenemy", "sword"),
            "remove", Arrays.asList("all", "dragonenemy", "friendlyenemy", "giantenemy", "smallenemy"),
            "help", Arrays.asList("reset", "remove", "save", "load", "set", "get", "add", "teleport",
                    "script", "make"));

    /**
     * Kiegészíti a megadott bemenetet a következő lehetséges paranccsal.
     * @param input a felhasználó által beírt szöveg
     * @param isNextCompletion jelzi, hogy ez egy következő kiegészítési kísérlet-e
     * @return a kiegészített parancs
     */
    public String complete(String input, boolean isNextCompletion) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] parts = input.trim().split("\\s+");
        boolean hasSpaceAtTheEnd = input.endsWith(" ");
        if (parts.length == 1 && !hasSpaceAtTheEnd) {
            return completeMainCommand(parts[0], isNextCompletion);
        }
        else {
            String mainCommand = parts[0].toLowerCase();
            String partial = hasSpaceAtTheEnd ? "" : parts[parts.length - 1].toLowerCase();

            if ("script".equals(mainCommand)) {
                if (!hasSpaceAtTheEnd) {
                    currentCompletions.clear();
                    currentIndex = -1;
                    lastCompletion = partial;

                    if ("script".startsWith(partial)) {
                        currentCompletions.add("script");
                    }
                } else {
                    currentCompletions.clear();
                    currentIndex = -1;
                    lastCompletion = partial;
                    List<String> scriptFiles = getScriptFiles();
                    if (currentCompletions.isEmpty()) {
                        currentCompletions.addAll(scriptFiles);
                    }
                }
            } else {
                if (!isNextCompletion || !Objects.equals(lastCompletion, partial)) {
                    initializeCompletions(mainCommand, partial, hasSpaceAtTheEnd);
                }
            }
            if (currentIndex >= currentCompletions.size() - 1) {
                currentIndex = -1;
            }
            if (!currentCompletions.isEmpty()) {
                currentIndex++;
                lastCompletion = currentCompletions.get(currentIndex);
                return String.join(" ", Arrays.copyOf(parts, parts.length - (hasSpaceAtTheEnd ? 0 : 1))) + (hasSpaceAtTheEnd ? "" : " ") + lastCompletion;
            }
            return input;
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

    private void initializeCompletions(String mainCommand, String partial, boolean hasTrailingSpace) {
        currentCompletions.clear();
        currentIndex = -1;
        lastCompletion = partial;

        if (subCommands.containsKey(mainCommand)) {
            List<String> subs = subCommands.get(mainCommand);

            if (!hasTrailingSpace && !partial.isEmpty()) {
                subs.stream()
                        .filter(sub -> sub.startsWith(partial))
                        .sorted().forEach(currentCompletions::add);
            }
            subs.stream()
                    .filter(sub -> !currentCompletions.contains(sub))
                    .sorted().forEach(currentCompletions::add);
        }
    }

    public static List<String> getScriptFiles() {
        List<String> scriptFiles = new ArrayList<>();
        File scriptDir = new File("res/scripts");
        if (scriptDir.exists() && scriptDir.isDirectory()) {
            File[] files = scriptDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    scriptFiles.add(file.getName().replace(".txt", ""));
                }
                Collections.sort(scriptFiles);
            }
        }
        return scriptFiles;
    }

    private void initializeMainCompletions(String partial) {
        currentCompletions.clear();
        currentIndex = -1;
        lastCompletion = partial;

        if (!partial.isEmpty()) {
            commands.stream()
                    .filter(cmd -> cmd.startsWith(partial))
                    .sorted().forEach(currentCompletions::add);
        }

        commands.stream()
                .filter(cmd -> !currentCompletions.contains(cmd))
                .sorted().forEach(currentCompletions::add);
    }
}
