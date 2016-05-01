import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

/* Extensions Added:
 *
 * User Control Over Bounces - The angle the ball bounces off
 * the paddle is now dependent on both the angle and where it
 * hits the paddle.
 *
 * Total Velocity - The total velocity of the ball now remains
 * constant instead of the vertical component of velocity.
 * This was done because the velocity would change with the
 * angle the ball bounces off the paddle.
 *
 * Kicker - Speed of ball now increases everytime it hits the
 * paddle.  It stops increasing after a while.
 *
 * Stronger Bricks - Some bricks now take two hits to destroy.
 * Their color changes as they are hit and a different hit
 * sound is played.
 *
 * Sound - The ball coming into contact with the paddle or
 * any bricks now makes a sound.  Fixed issue of sounds not
 * playing sometimes although this leads to some choppiness.
 *
 * Counters - The game now displays how many balls and bricks
 * are remaining.
 *
 * Title Screen - Very simple title screen added.
 *
 * Play Again? - Players can now click to play another game
 * after game end.
 *
 * Score - The game now includes a score counter and a score
 * multiplier system so the player can go after bonus points
 * if he or she chooses.
 */



 /** An instance is the game breakout. Start it by executing
 Breakout.main(null);
 */
public class Breakout extends GraphicsProgram {
  /** Width of the game display (al coordinates are in pixels) */
  private static final int GAME_WIDTH= 480;
  /** Height of the game display */
  private static final int GAME_HEIGHT= 620;

  /** Width of the paddle */
  private static final int PADDLE_WIDTH= 100;
  /** Height of the paddle */
  private static final int PADDLE_HEIGHT= 6;
  /**Distance of the (bottom of the) paddle up from the bottom */
  private static final int PADDLE_OFFSET= 30;

  /** Horizontal separation between bricks */
  public static final int BRICK_SEP_H= 5;
  /** Vertical separation between bricks */
  private static final int BRICK_SEP_V= 4;
  /** Height of a brick */
  private static final int BRICK_HEIGHT= 8;
  /** Offset of the top brick row from the top */
  private static final int BRICK_Y_OFFSET= 70;

  /** Number of bricks per row */
  public static  int BRICKS_IN_ROW= 10;
  /** Number of rows of bricks, in range 1..10. */
  public static  int BRICK_ROWS= 10;
  /** Width of a brick */
  public static int BRICK_WIDTH= GAME_WIDTH / BRICKS_IN_ROW - BRICK_SEP_H;

  /** Diameter of the ball in pixels */
  private static final int BALL_DIAMETER= 14;

  /** Number of turns */
  private static final int NTURNS= 3;

  /** Black paddle */
  private GRect PADDLE= new GRect(GAME_WIDTH/2-PADDLE_WIDTH/2, GAME_HEIGHT-PADDLE_OFFSET,
                                  PADDLE_WIDTH, PADDLE_HEIGHT);

  /** The Ball */
  private GOval BALL= new GOval((GAME_WIDTH-BALL_DIAMETER)/2 , (GAME_HEIGHT-BALL_DIAMETER)/2,
                                BALL_DIAMETER, BALL_DIAMETER);
  private double vx; // the horizontal velocity of the ball
  private double vy; // the vertical velocity of the ball
  private double vt; // the total velocity of the ball

  /** Messages */
  private GLabel BREAKOUT= new GLabel("Breakout", 5, 260);
  private GLabel INSTRUCTIONS= new GLabel("Hit the bricks with the ball from an angle or after", 5, 288);
  private GLabel INSTRUCTIONS2= new GLabel("bouncing it off the walls or ceiling for extra points.", 5, 302);
  private GLabel CLICK2PLAY= new GLabel("Click to Play...", 5, 330);
  private GLabel PLAYAGAIN= new GLabel("Click to Play Again...", 5, 55);

  private GLabel NEWBALL= new GLabel("New Ball in 3 Seconds...", 5, 41);
  private GLabel BESTWIN= new GLabel("Perfect!", 5, 41);
  private GLabel REGULARWIN= new GLabel("You Win!", 5, 41);
  private GLabel LOSS= new GLabel("You Lose!", 5, 41);
  //edited in run()
  private GLabel LIVES= new GLabel("Lives Remaining: ", 5, 13);
  private GLabel BRICKS= new GLabel("Bricks Destroyed: ", 5, 27);
  private GLabel SCORE= new GLabel("Score: ", 5, GAME_HEIGHT-6);

  /** rowColors[i] is the color of row i of bricks */
  private static final Color[] rowColors= {Color.red, Color.red, Color.orange, Color.orange,
    Color.yellow, Color.yellow, Color.green, Color.green,
    Color.cyan, Color.cyan};

  /** random number generator */
  private RandomGenerator rgen= new RandomGenerator();

  /** Sound to play when the ball bounces off paddle. */
  private static AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
  /** Sounds for when brick is destroyed. */
  private static AudioClip destroyClip1 = MediaTools.loadAudioClip("plate1.wav");
  private static AudioClip destroyClip2 = MediaTools.loadAudioClip("plate2.wav");
  /** Sounds for when brick is damaged. */
  private static AudioClip damageClip1 = MediaTools.loadAudioClip("saucer1.wav");
  private static AudioClip damageClip2 = MediaTools.loadAudioClip("saucer2.wav");
  private static AudioClip damageClip3 = MediaTools.loadAudioClip("cup1.wav");

  /** Run the program as an application. If args contains 2 elements that are positive
    integers, then use the first for the number of bricks per row and the second for
    the number of rows of bricks.
    A hint on how main works. The main program creates an instance of
    the class, giving the constructor the width and height of the graphics
    panel. The system then calls method run() to start the computation.
    */
  public static void main(String[] args) {
    //fixBricks(args);
    fixBricks(new String[] {"10", "10"});
    String[] sizeArgs= {"width=" + GAME_WIDTH, "height=" + GAME_HEIGHT};
    new Breakout().start(sizeArgs);

  }

  /** If b is null, doesn't have exactly two elements, or the elements are not
    positive integers, DON'T DO ANYTHING.
    If b is non-null, has exactly two elements, and they are positive
    integers with no blanks surrounding them, then:
    Store the first int in BRICKS_IN_ROW, store the second int in BRICK_ROWS,
    and recompute BRICK_WIDTH using the formula given in its declaration.
    */
  public static void fixBricks(String[] b) {
    /** Hint. You have to make sure that the two Strings are positive integers.
      The simplest way to do that is to use the calls Integer.valueOf(b[0]) and
      Integer.valueOf(b[1]) within a try-statement in which the catch block is
      empty. Don't store any values in the static fields UNTIL YOU KNOW THAT
      both array elements are positive integers. */
    try{
      Integer.valueOf(b[0]);
      Integer.valueOf(b[1]);
    } catch (NumberFormatException e){}

    BRICKS_IN_ROW= Integer.valueOf(b[0]);
    BRICK_ROWS= Integer.valueOf(b[1]);
    BRICK_WIDTH= GAME_WIDTH / BRICKS_IN_ROW - BRICK_SEP_H;
  }

  /** Run the Breakout program. */
  public void run() {

    add(BREAKOUT);
    add(INSTRUCTIONS);
    add(INSTRUCTIONS2);
    add(CLICK2PLAY);

    waitForClick();

    while (true) {
      remove(BREAKOUT);
      remove(INSTRUCTIONS);
      remove(INSTRUCTIONS2);
      remove(CLICK2PLAY);

      //Set-up the game
      setUpBricks();

      setUpPaddle();

      //Run the game
      int lives= NTURNS;
      int numberOfBricks= BRICKS_IN_ROW * BRICK_ROWS;
      int score= 0;
      int scoreMult= 1;
      LIVES.setLabel("Balls Remaining: " + (lives+1));
      BRICKS.setLabel("Bricks Remaining: " + numberOfBricks);
      SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
      add(LIVES);
      add(BRICKS);
      add(SCORE);

      int randNum;

      addMouseListeners();

      NEWBALL.setLabel("New Ball in 3 Seconds...");
      add(NEWBALL);
      pause(1000);
      NEWBALL.setLabel("New Ball in 2 Seconds...");
      pause(1000);
      NEWBALL.setLabel("New Ball in 1 Seconds...");
      pause(1000);
      remove(NEWBALL);
      setUpBall();
      scoreMult= (int)(Math.floor(vt/6.0) + Math.ceil(Math.abs(vx)/vt * 3));
      SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
      LIVES.setLabel("Balls Remaining: " + lives);

      //Exits loop when all lives are gone
      while (lives != 0 && numberOfBricks != 0) {

        BALL.move(vx,vy);

        //Store reference points of the ball
        double btop= BALL.getY();
        double bbottom= btop + BALL_DIAMETER;
        double bleft= BALL.getX();
        double bright= bleft + BALL_DIAMETER;

        //Check for walls and reverse direction
        if (vy < 0 && btop <= 0) {
          vy= -vy;
          scoreMult += 1;
          SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
        }
        if (vx < 0 && bleft <= 0) {
          vx= -vx;
          scoreMult += 1;
          SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
        }
        if (vx > 0 && bright >= GAME_WIDTH) {
          vx= -vx;
          scoreMult += 1;
          SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
        }

        GObject coll= getCollidingObject();
        //Check for paddle collision
        if (vy > 0  && coll == PADDLE)  {
          stopAllClips();
          bounceClip.play();
          scoreMult= (int)(Math.floor(vt/6.0) + Math.ceil(Math.abs(vx)/vt * 3));
          SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
          vx= vt * ((BALL.getX() + BALL_DIAMETER/2) - (PADDLE.getX() + PADDLE_WIDTH/2)) / (PADDLE_WIDTH/2) * 0.2
            + vx * 0.8;
          if (vt < 12.0)
            vt += 0.1;
          vy= -Math.sqrt(Math.pow(vt, 2) - Math.pow(vx, 2));
        }

        //Check for Brick collision
        if (coll instanceof Brick) {
          if (coll.getColor().equals(Color.black)) {
            randNum= rgen.nextInt(1, 3);
            stopAllClips();
            if (randNum == 1)
              damageClip1.play();
            if (randNum == 2)
              damageClip2.play();
            else
              damageClip3.play();
            GRect b= (GRect)coll;
            b.setColor(b.getFillColor().brighter());
            b.setFilled(true);
            b.setFillColor(b.getFillColor().brighter());
            score += scoreMult * 200;
            SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
          }
          else {
            stopAllClips();
            if (rgen.nextInt(1, 2) == 1)
              destroyClip1.play();
            else
              destroyClip2.play();
            remove(coll);
            score += scoreMult * 100;
            SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
            BRICKS.setLabel("Bricks Remaining: " + --numberOfBricks);
            if (numberOfBricks == 0) {
              if (lives == NTURNS)
                add(BESTWIN);
              else
                add(REGULARWIN);
            }
          }
          vy= -vy;
        }

        //Check for floor hit
        if (vy > 0 && bbottom >= GAME_HEIGHT) {
          lives--;
          remove(BALL);

          if (lives != 0) {
            NEWBALL.setLabel("New Ball in 3 Seconds...");
            add(NEWBALL);
            pause(1000);
            NEWBALL.setLabel("New Ball in 2 Seconds...");
            pause(1000);
            NEWBALL.setLabel("New Ball in 1 Seconds...");
            pause(1000);
            remove(NEWBALL);
            setUpBall(); //resets ball
            scoreMult= (int)(Math.floor(vt/6.0) + Math.ceil(Math.abs(vx)/vt * 3));
            SCORE.setLabel("Score (x" + scoreMult + ") : " + score);
            LIVES.setLabel("Balls Remaining: " + lives);
          }
          if (lives == 0) {
            add(LOSS);
            LIVES.setLabel("Balls Remaining: " + lives);
          }
        }
        pause(6);
      }
      add(PLAYAGAIN);
      waitForClick();
      removeAll();
    }
  }

  /** Create and draw the bricks of the game while changing colors every 2 rows
    * in the pattern RED, ORANGE, YELLOW, GREEN, CYAN. */
  public void setUpBricks() {
    for(int i= 1; i <= BRICK_ROWS; i++){
      Color color= Color.red;
      if ((i%10) == 1 || (i%10) == 2)
        color= Color.red;
      if ((i%10) == 3 || (i%10) == 4)
        color= Color.orange;
      if ((i%10) == 5 || (i%10) == 6)
        color= Color.yellow;
      if ((i%10) == 7 || (i%10) == 8)
        color= Color.green;
      if ((i%10) == 9 || (i%10) == 0)
        color= Color.cyan;

      for(int k= 1; k <= BRICKS_IN_ROW; k++) {
        Brick b= new Brick(BRICK_SEP_H/2 + (k-1)*(BRICK_SEP_H + BRICK_WIDTH),
                           BRICK_Y_OFFSET + (i-1)*(BRICK_SEP_V + BRICK_HEIGHT), BRICK_WIDTH, BRICK_HEIGHT);
        if (i % 2 == 1) {
          b.setColor(Color.black);
          b.setFilled(true);
          b.setFillColor(color.darker());
        }
        else {
          b.setColor(color);
          b.setFilled(true);
          b.setFillColor(color);
        }
        pause(15);
        add(b);
      }
    }
  }

  /** Sets up the paddle. */
  public void setUpPaddle() {
    //Set-up Paddle
    PADDLE.setColor(Color.black);
    PADDLE.setFilled(true);
    PADDLE.setFillColor(Color.darkGray);
    add(PADDLE);
  }

  /** Sets up ball and initializes velocities. */
  public void setUpBall() {
    //Set-up Ball
    BALL.setBounds((GAME_WIDTH-BALL_DIAMETER)/2 , (GAME_HEIGHT-BALL_DIAMETER)/2,
                   BALL_DIAMETER, BALL_DIAMETER);
    BALL.setColor(Color.black);
    BALL.setFilled(true);
    BALL.setFillColor(Color.black);
    add(BALL);

    vt= 4.0;
    vx= rgen.nextDouble(3.0, 3.5);
    if (!rgen.nextBoolean(0.5))
      vx= -vx;
    vy= Math.sqrt(Math.pow(vt, 2) - Math.pow(vx, 2));
  }

  /** Check for ball collisions.
    * = the object involved in the collision or null if no object is hit */
  public GObject getCollidingObject(){
    double bx= BALL.getX();
    double by= BALL.getY();
    double r= BALL_DIAMETER/2;

    if(getElementAt(bx, by) != null)
      return getElementAt(bx,by);
    if(getElementAt(bx+2*r, by) != null)
      return getElementAt(bx+2*r, by);
    if(getElementAt(bx, by+2*r) != null)
      return getElementAt(bx, by+2*r);
    if(getElementAt(bx+2*r,by+2*r) != null)
      return getElementAt(bx+2*r, by+2*r);

    return null;
  }

  /** Move the horizontal middle of the paddle to the x-coordinate of the mouse
    -- but keep the paddle completely on the board.
    Called by the system when the mouse is used.
    */
  public void mouseMoved(MouseEvent e) {
    GPoint p= new GPoint(e.getPoint());
    // Set x to the left edge of the paddle so that the middle of the paddle
    // is where the mouse is --except that the mouse must stay completely
    // in the pane if the mouse moves past the left or right edge.
    double px= p.getX();
    double padx= PADDLE.getX() + PADDLE_WIDTH/2;
    if (px > padx)
      PADDLE.move(Math.min(GAME_WIDTH, px) - padx, 0);
    if (px < padx)
      PADDLE.move(Math.max(0, px) - padx, 0);
  }

  /** Stops all clips */
  public void stopAllClips() {
    bounceClip.stop();
    destroyClip1.stop();
    destroyClip2.stop();
    damageClip1.stop();
    damageClip2.stop();
    damageClip3.stop();
  }

  /** = representation of array b: its elements separated by ", " and delimited by [].
    if b == null, return null. */
  public static String toString(String[] b) {
    if (b == null) return null;

    String res= "[";
    // inv res contains "[" + elements of b[0..k-1] separated by ", "
    for (int k= 0; k < b.length; k= k+1) {
      if (k > 0)
        res= res + ", ";
      res= res + b[k];
    }
    return res + "]";
  }

}

/** An instance is a Brick */
/*  Note: This program will not compile until you write the two
 constructors correctly, because GRect does not have a
 constructor with no parameters.  (You know that if a constructor
 does not begin with a call off another constructor, Java inserts

 super();

 */
class Brick extends GRect {

  /** Constructor: a new brick with width w and height h*/
  public Brick(double w, double h) {
    super(w, h);
  }

  /** Constructor: a new brick at (x,y) with width w and height h*/
  public Brick(double x, double y, double w, double h) {
    super(x, y, w, h);
  }
}
