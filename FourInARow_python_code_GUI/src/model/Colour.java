package model;

public enum Colour {
    
    EMPTY, Red, Blue;

    public Colour other() {
        if (this == Red) {
            return Blue;
        } else if (this == Blue) {
            return Red;
        } else {
            return EMPTY;
        }
    }
}
