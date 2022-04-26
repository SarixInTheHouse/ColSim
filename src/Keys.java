package src;

public enum Keys {

    LEFT(false),
    UP(false),
    RIGHT(false),
    DOWN(false),
    F12(false);

    boolean pressed;

    Keys(boolean pressed) {
        this.pressed = pressed;
    }

}