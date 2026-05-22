import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
//Runnable allows it to run on a thread
public class GamePanel extends JPanel implements Runnable{
    //resets elements in the game and initializes the graphics
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    boolean gameOver = false;
    String winner = "";
    boolean gameStarted = false;

    GamePanel(){
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH,GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
    }
    public void newPaddles() {
        paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
    }
    public void paint(Graphics g) {
        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }
    public void draw(Graphics g) {
        if(!gameStarted) {

            g.setColor(Color.pink);
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            g.setColor(Color.WHITE);

            g.setFont(new Font("Consolas", Font.BOLD, 70));
            String title = "2-PLAYER PONG GAME";

            FontMetrics titleMetrics = getFontMetrics(g.getFont());

            g.drawString(
                    title,
                    (GAME_WIDTH - titleMetrics.stringWidth(title))/2, 150);

            g.setFont(new Font("Consolas", Font.PLAIN, 40));

            String rules = "First to 10 Wins";
            FontMetrics rulesMetrics = getFontMetrics(g.getFont());

            g.drawString(
                    rules,
                    (GAME_WIDTH - rulesMetrics.stringWidth(rules))/2, 260);

            String start = "Press SPACE to Start";
            FontMetrics startMetrics = getFontMetrics(g.getFont());

            g.drawString(start, (GAME_WIDTH - startMetrics.stringWidth(start))/2, 360);

            g.setFont(new Font("Consolas", Font.PLAIN, 12));

            g.setColor(Color.white);

            String credit = "Concept inspired by Bro Code's Pong tutorial";

            FontMetrics creditMetrics = getFontMetrics(g.getFont());

            g.drawString(credit, (GAME_WIDTH - creditMetrics.stringWidth(credit))/2, GAME_HEIGHT - 20);


            g.setFont(new Font("Consolas", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            String creator = "By: Brooklyn Traczyk";
            FontMetrics creatorMetrics = getFontMetrics(g.getFont());
            g.drawString(creator, (GAME_WIDTH - creatorMetrics.stringWidth(creator))/2, GAME_HEIGHT - 45);
        }

        else if(!gameOver) {

            paddle1.draw(g);
            paddle2.draw(g);
            ball.draw(g);
            score.draw(g);
        }

        else {

            g.setColor(Color.pink);
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.BOLD, 75));

            FontMetrics metrics = getFontMetrics(g.getFont());

            g.drawString(winner, (GAME_WIDTH - metrics.stringWidth(winner))/2, GAME_HEIGHT/2);
        }

        Toolkit.getDefaultToolkit().sync();
    }


    public void move() {
        if(!gameStarted || gameOver)
            return;
        paddle1.move();
        paddle2.move();
        ball.move();
    }
    public void checkCollision() {

        //bounce ball off top & bottom window edges
        if(ball.y <=0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y >= GAME_HEIGHT-BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }
        //bounce ball off paddles
        if(ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; //optional for more difficulty
            if(ball.yVelocity>0)
                ball.yVelocity++; //optional for more difficulty
            else
                ball.yVelocity--;
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if(ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; //optional for more difficulty
            if(ball.yVelocity>0)
                ball.yVelocity++; //optional for more difficulty
            else
                ball.yVelocity--;
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        //stops paddles at window edges
        if(paddle1.y<=0)
            paddle1.y=0;
        if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
            paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
        if(paddle2.y<=0)
            paddle2.y=0;
        if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
            paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
        //give a player 1 point and creates new paddles & ball

        if(ball.x <=0) {
            score.player2++;

            if(score.player2 >= 10) {
                gameOver = true;
                winner = "PLAYER 2 WINS!";
            }
            else {
                newPaddles();
                newBall();
            }
        }

        if(ball.x >= GAME_WIDTH-BALL_DIAMETER) {
            score.player1++;

            if(score.player1 >= 10) {
                gameOver = true;
                winner = "PLAYER 1 WINS!";
            }
            else {
                newPaddles();
                newBall();
            }
        }
    }
    public void run() {
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks =60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while(true) {
            long now = System.nanoTime();
            delta += (now -lastTime)/ns;
            lastTime = now;
            if(delta >=1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }
    public class AL extends KeyAdapter{
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameStarted = true;
            }
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }
        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}








