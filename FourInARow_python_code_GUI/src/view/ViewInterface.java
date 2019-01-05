package view;

import java.awt.Color;
import java.util.Observer;

public interface ViewInterface extends Observer
{
    public void start();
    public void playerMove(int field, Color color);
    public void showDraw();
    public void showWon();
    public void showError(String message);
}
