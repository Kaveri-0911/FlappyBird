import javax.swing.*;

public class App 
{
    public static void main(String[] args) throws Exception
    {
        
        JFrame f = new JFrame("Flappy Bird Game");
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FlappyBird fb = new FlappyBird();
        f.add(fb);
        f.pack();
        f.setLocationRelativeTo(null);
        f.requestFocus();
        f.setVisible(true);
    }
}
