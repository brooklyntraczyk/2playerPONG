import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
//basically window into panel
public class GameFrame extends JFrame{

    GamePanel panel;

    GameFrame()
    {
        panel = new GamePanel();
        this.add(panel);
        this.setTitle("PONG GAME");
        this.setResizable(false);
        this.setBackground(Color.pink);
        //when you hit the "x" button it'll close for the user
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //pack lets panel and frame fit comfortably together, don't have to set Jframe size this way
        this.pack();
        //lets user see the game
        this.setVisible(true);
        //will allow game to appear in middle of screen
        this.setLocationRelativeTo(null);
    }

}
