import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


public class KikiDelivery extends JPanel implements ActionListener, KeyListener{
    private int boardWidth = 1280;
    private int boardHeight = 720;

    // Game states
    private static final int TITLE_SCREEN = 0;
    private static final int PLAYING = 1;
    private int gameState = TITLE_SCREEN;

    // Scoring
    private int score;
    private int scoreDelay;
    private int highScore;
    private final int SCORING_SPEED = 60;
    private final String[] SCORE_IMAGE_NAMES = {
        "score.png", "highscore.png", "numbers.png"};
    private ArrayList<BufferedImage> scoreImages = new ArrayList<>();

    // Images
    private Image titleScreenImg;
    private Image backgroundImg;
    private BufferedImage kikiImg;
    private String[] shortObstacleNames = {
        "short1.png", "short2.png", "short3.png", "short4.png", "short5.png",
        "short6.png", "short7.png", "short8.png"};
    private String[] tallObstacleNames = {
        "tall1.png", "tall2.png", "tall3.png"};
    private String[] topObstacleNames = {
        "top1.png", "top2.png"};
    private String[] kikiFrameNames = {
        "kiki1.png", "kiki2.png", "kiki3.png", "kiki4.png", "kiki5.png", 
        "kiki6.png"};
    private String[] titleFrameNames = {
        "title1.png", "title2.png"};
    private ArrayList<BufferedImage> topObstacleImg = new ArrayList<>();
    private ArrayList<BufferedImage> shortObstacleImg = new ArrayList<>();
    private ArrayList<BufferedImage> tallObstacleImg = new ArrayList<>();
    private ArrayList<BufferedImage> kikiFrames = new ArrayList<>();
    private ArrayList<BufferedImage> titleFrames = new ArrayList<>();

    // Title screen animation
    private double titleKikiY = boardHeight/3;
    private double titleY = boardHeight/6;
    private int titleX = boardWidth/3;
    private int titleKikiX = boardWidth/4; 
    private int currentKikiFrame = 0;
    private int currentTitleFrame = 0;
    private int animationDelayKiki = 0;
    private int titleDelay = 0; 
    private final int ANIMATION_SPEED_KIKI = 9;
    private final int ANIMATION_SPEED_TITLE = 15;
    private double bob = 0;

    // Kiki
    int kikiX = boardWidth/6;
    int kikiY = boardHeight/2;
    int kikiWidth = 26*4; // Scaled
    int kikiHeight = 22*4;

    Kiki kiki;
    ArrayList<Obstacles> topObstacles;
    ArrayList<Obstacles> shortObstacles;
    ArrayList<Obstacles> tallObstacles;

    double velocityY = 0.0; // Kikis speed going up
    double gravity = 0.7;

    // Obstacles
    int obstacleX = boardWidth;
    int obstacleY = 0;
    int lasObstacleWidth = 0;

    Timer gameLoop;

    KikiDelivery() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("/resources/images/background1.png")).getImage();
        titleScreenImg = new ImageIcon(getClass().getResource("/resources/images/titlebackground.png")).getImage();

        try {
            kikiImg = ImageIO.read(getClass().getResource("/resources/images/kiki.png"));
        } catch (IOException e) {e.printStackTrace();}

        tallObstacleImg = loadBufferedImages("/resources/images/tall/", tallObstacleNames);
        shortObstacleImg = loadBufferedImages("/resources/images/short/", shortObstacleNames);
        topObstacleImg = loadBufferedImages("/resources/images/top/", topObstacleNames);
        kikiFrames = loadBufferedImages("/resources/images/title/", kikiFrameNames);
        titleFrames = loadBufferedImages("/resources/images/title/", titleFrameNames);
        scoreImages = loadBufferedImages("/resources/images/scoring/", SCORE_IMAGE_NAMES);
        
        // Create Kiki and obstacles
        kiki = new Kiki(kikiImg, kikiX, kikiY, kikiWidth, kikiHeight);

        shortObstacles = new ArrayList<>();
        tallObstacles = new ArrayList<>();
        topObstacles = new ArrayList<>();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    
    }

    // Method to load buffered images
    private ArrayList<BufferedImage> loadBufferedImages(String folderPath, String[] fileNames) {
        ArrayList<BufferedImage> images = new ArrayList<>();

        for (String file : fileNames) {
            try {
                InputStream stream = getClass().getResourceAsStream(folderPath + file);     
                if (stream == null) {
                    System.err.println("Resource not found: " + folderPath + file);
                    continue;
                }

                BufferedImage image = ImageIO.read(stream);
                if (image != null){
                    images.add(image);
                } else {
                    System.err.println(folderPath + " failed to load image");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return images;
    }

    public void placeBottomObjects() {

        int lastShortRight = getLastRightEdge(shortObstacles);
        if (lastShortRight <= boardWidth){
            BufferedImage randShort = shortObstacleImg.get(
                    new Random().nextInt(shortObstacleImg.size()));
                int heightOffset = getRandomHeight(randShort);
                shortObstacles.add(
                    new Obstacles(
                        randShort, lastShortRight, boardHeight - heightOffset));
        }

        int lastTallRight = getLastRightEdge(tallObstacles);
        if (lastTallRight <= boardWidth){
            double placeTall = Math.random();
            if (placeTall < 0.03){
                BufferedImage randTall = tallObstacleImg.get(
                    new Random().nextInt(tallObstacleImg.size()));
                int heightOffset = getRandomHeight(randTall);
                tallObstacles.add(
                    new Obstacles(
                        randTall, boardWidth, boardHeight - heightOffset));
            }
        }
    }

    public void placeTopObjects(){
        int lastTopObstacleRight = getLastRightEdge(topObstacles);
        if (lastTopObstacleRight <= boardWidth){
            double placeObject = Math.random();
            if (placeObject < 0.05){
                BufferedImage randTop = topObstacleImg.get(
                    new Random().nextInt(topObstacleImg.size()));
                int randomY = Math.max(0,new Random().nextInt(80)) - 20;
                topObstacles.add(new Obstacles(
                    randTop, boardWidth, randomY));
            }
        }
    }

    private int getLastRightEdge(ArrayList<Obstacles> obstacles){
        if (obstacles.isEmpty()){
            return boardWidth;
        } else {
            Obstacles last = obstacles.get(obstacles.size() - 1);
            return (last.x + last.width);
        }
    }

    private int getRandomHeight(BufferedImage img){
        int height = img.getHeight()*4;
        int random =(int) (Math.random() * height/4);
        return height - random;
    }

    public void move() {
        // Update coordinates
        velocityY += gravity;
        kiki.y += velocityY;
        kiki.y = Math.max(kiki.y, 0);

        // Obstacles
        updateObstacles(shortObstacles, -6);
        updateObstacles(tallObstacles, -5);
        updateObstacles(topObstacles, -11);

        // Spawn new objects
        placeBottomObjects();
        placeTopObjects();

        // Update score
        updateScore();
    }

    public void updateScore(){
        scoreDelay++;
        if (scoreDelay == SCORING_SPEED){
            scoreDelay = 0;
            score += 10;
            if (score > highScore){
                highScore = score;
            }
        }
    }

    public void updateObstacles(ArrayList<Obstacles> obstacles, double velocity){
        for (int i=obstacles.size()-1; i >= 0; i--){
            Obstacles o = obstacles.get(i);
            o.x += velocity;
            if (o.x + o.width < 0){
                obstacles.remove(i);
            }
        }
    }

    public void checkCollision(ArrayList<Obstacles> obstacles){
        for (Obstacles o : obstacles){
            // Check if in line with Kiki
            if ((o.x + o.width < kiki.x) || (o.x > kiki.x + kiki.width)){
                continue;
            }
            // Check vertical height overlap
            if ((o.y + o.height < kiki.y) || (o.y > kiki.y + kiki.height)){
                continue;
            }
            if (CollisionDetection.isCollision(
            kiki.img, kiki.x, kiki.y, o.bufferedImage, o.x, o.y)){
                gameState = TITLE_SCREEN;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw based on state of game
        switch (gameState) {
            case TITLE_SCREEN:
                drawTitleScreen(g);
                break;
            case PLAYING:
                drawGame(g);
                break;
        }  
    }

    public void drawTitleScreen(Graphics g) {
        g.drawImage(titleScreenImg, 0, 0, boardWidth, boardHeight, null);

        BufferedImage k = kikiFrames.get(currentKikiFrame);
        BufferedImage t = titleFrames.get(currentTitleFrame);
        g.drawImage(k, titleKikiX, (int)titleKikiY, k.getWidth()*4, k.getHeight()*4, null);
        g.drawImage(t, titleX, (int)titleY, t.getWidth()*4, t.getHeight()*4, null);
        BufferedImage highscoreImage = scoreImages.get(1);
        g.drawImage(highscoreImage,0,0,highscoreImage.getWidth()*4, highscoreImage.getHeight()*4, null);
        drawDigits(g, highScore, highscoreImage.getWidth()*4, 16);

    }

    public void updateTitleAnimations(){
        bob += 0.095;
        titleKikiY = boardHeight/3 + (Math.sin(bob)*15);
        titleY = boardHeight/6 + (Math.sin(bob)*7);

        animationDelayKiki ++;
        titleDelay ++;
        if (animationDelayKiki > ANIMATION_SPEED_KIKI){
            animationDelayKiki = 0;
            currentKikiFrame = (currentKikiFrame + 1) % kikiFrames.size();
        }
        if (titleDelay > ANIMATION_SPEED_TITLE){
            titleDelay = 0;
            currentTitleFrame = (currentTitleFrame + 1) % titleFrames.size();
        }
        
    }

    public void drawGame(Graphics g) {
        g.drawImage(
            backgroundImg, 0, 0, boardWidth, boardHeight, null);
        
        g.drawImage(
            kiki.img, kiki.x, kiki.y, kiki.width, kiki.height, null);

        drawObstacles(g, tallObstacles);
        drawObstacles(g, shortObstacles);
        drawObstacles(g, topObstacles);

        drawScore(g);
    }

    public void drawScore(Graphics g){
        BufferedImage s = scoreImages.get(0);
        BufferedImage h = scoreImages.get(1);
        g.drawImage(s, 0, 0, s.getWidth()*4, s.getHeight()*4, null);
        g.drawImage(h, 0, s.getHeight()*4, h.getWidth()*4, h.getHeight()*4, null);

        drawDigits(g, score, 150, 12);
        drawDigits(g, highScore, h.getWidth()*4, s.getHeight()*4 + 16);
    }

    public void drawDigits(Graphics g, int s, int startX, int startY){
        String scoreString = String.valueOf(s);
        for (int i=0; i < scoreString.length(); i++){
            int currentDigit = Character.getNumericValue(scoreString.charAt(i));
            BufferedImage d = scoreImages.get(2).getSubimage(currentDigit*3, 0, 3, 5);
            g.drawImage(d, startX + i*12 + i*4, startY, 12, 20, null);
        }
    }

    public void drawObstacles(Graphics g, ArrayList<Obstacles> obstacles){
        for (int i=0; i < obstacles.size(); i++){
            Obstacles obstacle = obstacles.get(i);
            g.drawImage(
                obstacle.bufferedImage, obstacle.x, obstacle.y, 
                obstacle.width, obstacle.height, null);
        }
    }

    public void resetGame() {
        score = 0;

        kiki.y = boardHeight/2;
        velocityY = 0;

        shortObstacles.clear();
        tallObstacles.clear();
        topObstacles.clear();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == PLAYING){
            move();
            checkCollision(topObstacles);
            checkCollision(tallObstacles);
            checkCollision(shortObstacles);
            if (kiki.y > boardHeight){
                gameState = TITLE_SCREEN;
                if (score > highScore){
                    highScore = score;
                }
            }
        } else if (gameState == TITLE_SCREEN){
            updateTitleAnimations();
        }   
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if (gameState == TITLE_SCREEN){
                gameState = PLAYING;
                resetGame();
            } else if (gameState == PLAYING){
                velocityY = -8;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gameState = TITLE_SCREEN;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}