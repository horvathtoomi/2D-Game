package main.console;

/**
 * A parancs interfész, amely definiálja a konzol parancsok közös struktúráját.
 */

public interface Command {
    void execute(String[] args);
}
