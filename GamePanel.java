package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements KeyListener, Runnable {
	
	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;
	//Render
	private Graphics2D g2d;
	private BufferedImage image;
	
	//Game Loop
	private Thread thread;
	private boolean running;
	private long targetTime;
	
	//Game Stuff
	public final int SIZE = 10;
	Entity head;
	Entity apple1;
	ArrayList<Entity> snake;
	ArrayList<Entity> apple;
	int score;
	boolean gameover;
	boolean appleEaten = false;
	Entity eatenApple;
	
	//movement
	private int dx,dy;
	// key input
	private boolean up,down,right,left,start;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	public void addNotify() {
		super.addNotify();
		thread = new Thread(this);
		thread.start();
	}
	
	
	private void setFPS(int fps) {
		targetTime = 1000 / fps;
	}
	
	@Override
	public void run() {
		System.out.println("testpunkt2");
		if(running) return;
		init();
		long startTime;
		long elapsed;
		long wait;
		while(running) {
			startTime = System.nanoTime();
			
			update();
			requestRender();
			
			elapsed = System.nanoTime() - startTime;
			wait = targetTime - elapsed / 1000000;
			if( wait > 0 ) {
				try {
					Thread.sleep(wait);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void requestRender() {
		render(g2d);
		Graphics g = getGraphics();
		g.drawImage(image,0,0,null);
		g.dispose();
	}
	
	private void update() {
		if(up && dy == 0) {
			dy = -SIZE;
			dx = 0;
		}
		if(down && dy == 0) {
			dy = SIZE;
			dx = 0;
		}
		if(left && dx == 0) {
			dy = 0;
			dx = -SIZE;
		}
		if(right && dx == 0) {
			dy = 0;
			dx = SIZE;
		}
		if(dx != 0 || dy != 0) {
			for(int i = snake.size() - 1;i > 0;i--) {
				snake.get(i).setPosition(
						snake.get(i - 1).getX(),
						snake.get(i - 1).getY()
						);
			}
		}
		head.move(dx, dy);
		
		for(Entity e : snake) {
			if(e.isCollision(head)) {
				score++;
				running = false;
				gameover = true;
			}
		}
		appleEaten = false;
		
		for(Entity e : apple) {
			if(e.isCollision(head)) {
				score++;
				snake.add(e);
				eatenApple = e;
				appleEaten = true;
				
			}
		}
		
		if( appleEaten == true ) {
		apple.remove(eatenApple);
		setNewApple();
		}
		
		if(head.getX() < 0) {
			head.setX(WIDTH);
		}
		if(head.getY() < 0) {
			head.setY(HEIGHT);
		}
		if(head.getX() > WIDTH) {
			head.setX(0);
		}
		if(head.getY() > HEIGHT) {
			head.setY(0);
		}
	}
	
	private	void setUpLevel() {
		snake = new ArrayList<Entity>();
		head = new Entity(SIZE);
		head.setPosition(WIDTH / 2, HEIGHT / 2);
		snake.add(head);
		
		for(int i = 1; i < 10; i++) {
			Entity e = new Entity(SIZE);
			e.setPosition(head.getX() + (i * SIZE),  head.getY());
			snake.add(e);
		}
	}
		
	private void init() {
		System.out.println("testpunkt3");
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		running = true;
		setApples(10);
		setUpLevel();
		setFPS(10);
		
	}
	
	public void render(Graphics2D g2d) {
		g2d.clearRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(Color.GREEN);
		
		g2d.drawString("Score: " +  score, WIDTH -100,10);
		
		for(Entity e : snake) {
			e.render(g2d);
		}
		
		g2d.setColor(Color.RED);
		for(Entity e : apple) {
			e.render(g2d);
		}
		if(gameover) {
			g2d.drawString("GameOver!" , 150, 200);
		}
	}
	@Override 
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		
		if(k == KeyEvent.VK_UP) { 
			up = true;
		}
		
		if(k == KeyEvent.VK_DOWN) {
			down = true;
		}
		
		if(k == KeyEvent.VK_LEFT) {
			left = true;
		}
		
		if(k == KeyEvent.VK_RIGHT) {
			right = true;
		}
		
		if(k == KeyEvent.VK_ENTER) {
			start = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		
		if(k == KeyEvent.VK_UP) { 
			up = false;
		}
		
		if(k == KeyEvent.VK_DOWN) {
			down = false;
		}
		
		if(k == KeyEvent.VK_LEFT) {
			left = false;
		}
		
		if(k == KeyEvent.VK_RIGHT) {
			right = false;
		}
		
		if(k == KeyEvent.VK_ENTER) {
			start = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	public void setApples(int quantity) {
		
		apple = new ArrayList<Entity>();
		for(int i = 1; i < quantity; i++) {
			setNewApple(); 
		}
	}
	
	public void setNewApple() {
		Entity e = new Entity(SIZE);
		e.setPosition(getRandom(), getRandom());
		apple.add(e);
	}
	
	public int getRandom() {
		Random rand = new Random();
		int random = rand.nextInt(WIDTH);
		return random;
	}

}
