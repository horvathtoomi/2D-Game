package entity;

public enum Direction {
    UP, DOWN, LEFT, RIGHT, SHOOT;

    @Override
    public String toString() {
        return switch(this) {
            case UP -> "UP";
            case DOWN -> "DOWN";
            case LEFT -> "LEFT";
            case RIGHT -> "RIGHT";
            case SHOOT -> "SHOOT";
        };
    }

    public static <T> T valueMapper(T[] list, Direction dir){
        if(list.length == 0 || list.length > 5) {
            return null;
        }
        else if(list.length == 4) {
            return switch (dir) {
                case UP -> list[0];
                case DOWN -> list[1];
                case LEFT -> list[2];
                case RIGHT -> list[3];
                default -> null;
            };
        }
        else if(list.length == 5) {
            return switch (dir) {
                case UP -> list[0];
                case DOWN -> list[1];
                case LEFT -> list[2];
                case RIGHT -> list[3];
                case SHOOT -> list[4];
            };
        }
        return null;
    }
}