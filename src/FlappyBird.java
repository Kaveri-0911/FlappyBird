import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener 
{
    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;

    // bird properties
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird 
    {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) 
        {
            this.img = img;
        }
    }

    // pipe properties
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe 
    {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) 
        {
            this.img = img;
        }
    }

    Bird bird;
    ArrayList<Pipe> pipes;
    Random random = new Random();

    // game state
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    boolean gameOver = false;
    double score = 0;

    Timer gameLoop;
    Timer placePipeTimer;

    public FlappyBird() 
    {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images 
        backgroundImg = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Timer to place new pipes
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        // Main game loop (60 FPS)
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    void placePipes() 
    {

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) 
    {
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        if (birdImg != null) 
        {
            g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        } 
        else 
        {
            g.setColor(Color.RED);
            g.fillRect(bird.x, bird.y, bird.width, bird.height);
        }

        // pipes
        for (Pipe pipe : pipes) 
        {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

    }

    public void move() 
    {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

        }

        if (bird.y > boardHeight) 
        {
            gameOver = true;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) 
    {
        move();
        repaint();
        
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) 
        {
            velocityY = -9;

        }
    }

    @Override 
    public void keyTyped(KeyEvent e) {}
    
    @Override 
    public void keyReleased(KeyEvent e) {}
}
