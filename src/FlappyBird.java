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

    // game state variables
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    boolean gameOver = false;
    boolean isPaused = false;
    boolean inStartMenu = true; // Track if on the start screen
    double score = 0;
    int lives = 3;

    // pipe properties
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // game loop timers
    Timer gameLoop;
    Timer placePipeTimer;

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

        // Main game loop (60 FPS)
        gameLoop = new Timer(1000 / 60, this);
    }

    // Function to place two pipes (top and bottom) with a gap
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

    // Render everything
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) 
    {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        if (inStartMenu) 
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Flappy Bird", 90, 200);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press ENTER to Start", 80, 300);
            return;
        }

        // draw bird
        if (birdImg != null) 
        {
            g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        } 
        else 
        {
            g.setColor(Color.RED);
            g.fillRect(bird.x, bird.y, bird.width, bird.height);
        }

        // draw pipes
        for (Pipe pipe : pipes) 
        {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // draw score and lives
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 28));
        g.drawString("Score: " + (int) score, 10, 35);
        g.drawString("Lives: " + lives, 10, 65);

        // draw pause or game over message
        if (isPaused) 
        {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Paused", 120, boardHeight / 2);
        } 
        else if (gameOver) 
        {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Game Over!", 100, boardHeight / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press ENTER to Restart", 80, boardHeight / 2 + 40);
        }
    }

    public void move() 
    {
        if (isPaused || inStartMenu || gameOver) return;

        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) 
        {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width)
            {
                score += 0.5; 
                pipe.passed = true;
            }

            if (collision(bird, pipe)) 
            {
                lives--;
                if (lives <= 0) 
                {
                    gameOver = true;
                    placePipeTimer.stop();
                    gameLoop.stop();
                }
                pipes.clear();
                break;
            }
        }

        if (bird.y > boardHeight) 
        {
            lives--;
            if (lives <= 0) 
            {
                gameOver = true;
                placePipeTimer.stop();
                gameLoop.stop();
            }
            pipes.clear();
        }
    }

    boolean collision(Bird a, Pipe b) 
    {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
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
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ENTER) 
        {
            if (inStartMenu || gameOver) 
            {
                // Start or restart game
                inStartMenu = false;
                gameOver = false;
                pipes.clear();
                velocityY = 0;
                bird.y = birdY;
                score = 0;
                lives = 3;
                placePipeTimer.start();
                gameLoop.start();
            }
        }
        else if (key == KeyEvent.VK_SPACE && !inStartMenu && !isPaused && !gameOver) 
        {
            velocityY = -9;
        }
        else if (key == KeyEvent.VK_P && !inStartMenu && !gameOver) 
        {
            isPaused = !isPaused;
            if (isPaused) 
            {
                gameLoop.stop();
                placePipeTimer.stop();
            } 
            else 
            {
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override 
    public void keyTyped(KeyEvent e) {}

    @Override 
    public void keyReleased(KeyEvent e) {}
}