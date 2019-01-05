package controllerOfClient;
import java.net.Socket;
import model.Color;

public interface PlayerInterface 
{
      public Color getColor();
      public String getName();
      public Socket getSocket();
      public int getMode();
}