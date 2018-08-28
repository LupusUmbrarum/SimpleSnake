package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

public class SimpleSnake
{
	public static void main(String[] args)
	{
		SnakeGame snakeGame = new SnakeGame();
		snakeGame.start();
	}
}

class SnakeGame extends JFrame implements KeyListener
{
	private ArrayList<Point> snakeBody;
	private Point food;
	private Color backgroundColor, snakeColor, foodColor;
	private Image raster;
	private Graphics rasterGraphics;
	public int time, velocityX = 0, velocityY = 0, gridWidth, gridHeight;
	
	public SnakeGame()
	{
		this(800, 600);
	}
	
	public SnakeGame(int width, int height)
	{
		setSize(width, height);
		addKeyListener(this);
		time = 125;
		backgroundColor = Color.BLACK;
		snakeColor = Color.green;
		foodColor = Color.white;
		setDefaultCloseOperation(3);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width/2-getWidth()/2, screenSize.height/2-getHeight()/2);
		setUndecorated(true);
		setVisible(true);
		gridWidth = getWidth()/25;
		gridHeight = getHeight()/25;
		snakeBody = new ArrayList<>();
		snakeBody.add(new Point(getWidth()/2-gridWidth/2, getHeight()/2-gridHeight/2));
		food = new Point(0, 0);
		moveFood();
		init();
	}
	
	public void init()
	{
		this.raster = createImage(getWidth(), getHeight());
		this.rasterGraphics = this.raster.getGraphics();
	}
	
	public void start()
	{
		while(true)
		{
			drawBackground();
			tick();
			
			getGraphics().drawImage(this.raster, 0, 0, getWidth(), getHeight(), null);
			
			try
			{
				Thread.sleep(time);
			}
			catch(Exception e){}
		}
	}
	
	public void drawBackground()
	{
		this.rasterGraphics.setColor(backgroundColor);
		this.rasterGraphics.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public void tick()
	{
		move();
		draw();
	}
	
	public void draw()
	{
		rasterGraphics.setColor(snakeColor);
		for(int x = 0; x < snakeBody.size(); x++)
		{
			rasterGraphics.fillRect(snakeBody.get(x).x, snakeBody.get(x).y, gridWidth, gridHeight);
		}
		
		rasterGraphics.setColor(foodColor);
		rasterGraphics.fillRect(food.x, food.y, gridWidth, gridHeight);
	}
	
	public void move()
	{
		Point nextPoint = new Point(snakeBody.get(0).x + velocityX, snakeBody.get(0).y + velocityY);
		
		for(int x = 0; x < snakeBody.size(); x++)
		{
			if(snakeBody.get(x).x == nextPoint.x && snakeBody.get(x).y == nextPoint.y)
			{
				snakeBody.clear();
				snakeBody.add(nextPoint);
				return;
			}
		}
		
		//move all of the snake's body except for the head
		for(int x = snakeBody.size()-1; x > 0; x--)
		{
			snakeBody.set(x, snakeBody.get(x-1));
		}
		
		//move the head
		snakeBody.set(0, nextPoint);
		
		//check to see if the snake head is beyond the border of the screens
		if(snakeBody.get(0).x >= getWidth())
		{
			snakeBody.set(0, new Point(0, snakeBody.get(0).y));
		}
		else if(snakeBody.get(0).x < 0)
		{
			snakeBody.set(0, new Point(getWidth()-gridWidth, snakeBody.get(0).y));
		}
		else if(snakeBody.get(0).y >= getHeight())
		{
			snakeBody.set(0, new Point(snakeBody.get(0).x, 0));
		}
		else if(snakeBody.get(0).y < 0)
		{
			snakeBody.set(0, new Point(snakeBody.get(0).x, getHeight() - gridHeight));
		}
		
		//check to see if the snake head is on the food
		if(snakeBody.get(0).x == food.x && snakeBody.get(0).y == food.y)
		{
			snakeBody.add(new Point(snakeBody.get(0).x, snakeBody.get(0).y));
			moveFood();
		}
	}
	
	public void moveFood()
	{
		int rand = ThreadLocalRandom.current().nextInt(0, getWidth()/gridWidth);
		int randY = ThreadLocalRandom.current().nextInt(0, getHeight()/gridHeight);
		
		Point foodPoint = new Point(rand, randY);
		
		for(int x = 0; x < snakeBody.size(); x++)
		{
			if(foodPoint.x == snakeBody.get(x).x && foodPoint.y == snakeBody.get(x).y)
			{
				rand = ThreadLocalRandom.current().nextInt(0, getWidth()/gridWidth);
				randY = ThreadLocalRandom.current().nextInt(0, getHeight()/gridHeight);
				foodPoint.x = rand;
				foodPoint.y = randY;
				x = 0;
			}
		}
		food.x = gridWidth * rand;
		food.y = gridHeight * randY;
	}
	
	private boolean changeDirection(int x, int y)
	{
		if(snakeBody.size() > 1 && (snakeBody.get(0).x+x == snakeBody.get(1).x || snakeBody.get(0).y+y == snakeBody.get(1).y))
			return false;
		return true;
	}
	
	public void keyPressed(KeyEvent arg0)
	{
		switch(arg0.getKeyCode())
		{
		case KeyEvent.VK_ESCAPE:
		{
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			break;
		}
		case KeyEvent.VK_A:
		{
			if(!changeDirection(-gridWidth, 0))
				break;
			velocityX = -gridWidth;
			velocityY = 0;
			break;
		}
		case KeyEvent.VK_D:
		{
			if(!changeDirection(gridWidth, 0))
				break;
			velocityX = gridWidth;
			velocityY = 0;
			break;
		}
		case KeyEvent.VK_S:
		{
			if(!changeDirection(0, gridHeight))
				break;
			velocityX = 0;
			velocityY = gridHeight;
			break;
		}
		case KeyEvent.VK_W:
		{
			if(!changeDirection(0, -gridHeight))
				break;
			velocityX = 0;
			velocityY = -gridHeight;
			break;
		}
		}
	}
	
	public void keyReleased(KeyEvent arg0)
	{
		
	}
	
	public void keyTyped(KeyEvent arg0)
	{
		
	}
}