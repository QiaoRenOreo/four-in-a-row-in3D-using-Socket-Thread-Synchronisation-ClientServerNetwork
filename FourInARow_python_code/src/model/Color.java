package model;

public enum Color {
    
    EMPTY, Red, Blue;

    public Color other() {
        if (this == Red) {
            return Blue;
        } else if (this == Blue) {
            return Red;
        } else {
            return EMPTY;
        }
    }
}
