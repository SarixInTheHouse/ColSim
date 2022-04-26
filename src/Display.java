package src;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.Panel;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;


// TODO need to fix how tileset is made. currently oddly cut; offset

public class Display extends JFrame implements KeyListener, MouseListener {
	
    private final int squareSize, mapHeight, mapWidth, visHeight, visWidth, tsHeight, tsWidth, tsSquareSize;
    private int offSetX, offSetY, offCordX, offCordY;

    private int[][] map;

    private BufferedImage tileSet;
    private BufferedImage[] tiles;

	private Canvas canvas;
	private Panel mainPanel;
	
	private Game game;
	
	public Display(Game game) {
		
		this.game = game;

        squareSize = game.getSquareSize(); //size of each square in pixel
        mapHeight = game.getMapheight(); // actual height of map; not entirely rendere
        mapWidth = game.getMapWidth(); // ^^ but width
        visHeight = 22; // actual height of game. 2 coordinates are borders !
        visWidth = 32;

        // values for tileset
        tsHeight = 11;
        tsWidth = 29;
        tsSquareSize = 32;
        fillTileSet();

        offSetX = -squareSize; //offset in pixels for rendering
        offSetY = -squareSize; // ^^ 
        offCordX = 0; // coordinate offset for dynamic rendering / i.e. not entire map is rendered but only visible part
        offCordY = 0; // ^^ 
        map = game.getMap();



        setTitle("colony simulator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		mainPanel = new Panel();
		mainPanel.setFocusable(true);
		mainPanel.setVisible(true);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension((visWidth-2)*squareSize, (visHeight-2)*squareSize));
		canvas.setFocusable(false);
		mainPanel.add(canvas);
		mainPanel.addKeyListener(this);
		add(mainPanel);
		pack();

		//no idea where it goes so I put it here, seems to work
		addMouseListener(this);
		mainPanel.addMouseListener(this);
		canvas.addMouseListener(this);
		
		canvas.createBufferStrategy(3);

		mainPanel.requestFocusInWindow();

        setVisible(true);

    }

    public void render() {

        BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		Graphics g = bufferStrategy.getDrawGraphics();
		
		//black background
		g.setColor(Color.black);
		g.fillRect(0,0,canvas.getWidth(), canvas.getHeight());

        //camera movement
        // moves camera with pixel offset. Standard offset is -16; i.e. left most tile is not visible.
        // Once it moved by 16 pixel it will reset and render a new box
        if (Keys.UP.pressed) {
            if (offCordY-1 >= 0) {
                if (offSetY == 0) {
                    offSetY = -squareSize;
                    offCordY--;
                }
                offSetY++;
            }
        }
        if (Keys.DOWN.pressed) {   
            if (offCordY+1 < mapHeight-visHeight){
                if (offSetY == -squareSize) {
                    offSetY = 0;
                    offCordY++;
                }
                offSetY--;
            }
        }
        if (Keys.LEFT.pressed) {
            if (offCordX-1 >= 0){
                if (offSetX == 0) {
                    offSetX = -squareSize;
                    offCordX--;
                }
                offSetX++;
            }
        }
        if (Keys.RIGHT.pressed) {
            if (offCordX+1 < mapWidth-visWidth){
                if (offSetX == -squareSize) {
                    offSetX = 0;
                    offCordX++;
                }
                offSetX--;
            }
        }

        if (game.getDevMode()) {
            setSize(new Dimension((visWidth-2)*squareSize, (visHeight-2+11)*squareSize));
            canvas.setPreferredSize(new Dimension((visWidth-2)*squareSize, (visHeight-2+11)*squareSize));
            g.setColor(Color.black);
            g.fillRect(0, 0, 1000, 1000);
            // render palette

            for (int y = 0; y < tsHeight; y++) {
                for (int x = 0; x < tsWidth; x++) {
                    g.drawImage(tiles[(y*tsWidth+x)], x*squareSize, //(visHeight-2+y)*squareSize 
                    21*squareSize, null);
                }
            }


        } else {
            setSize(new Dimension((visWidth-2)*squareSize, (visHeight-2)*squareSize));
            canvas.setPreferredSize(new Dimension((visWidth-2)*squareSize, (visHeight-2)*squareSize));
        }
        //render map
        for (int x = 0; x < visWidth; x++) {
            for (int y = 0; y < visHeight; y++) {
                BufferedImage img;
                img = tiles[map[x+offCordX][y+offCordY]];
                g.drawImage(img, x*squareSize+offSetX, y*squareSize+offSetY, null);
            }
        }

        g.dispose();
		bufferStrategy.show();

    }

    public void fillTileSet() {
        try {
            tileSet = ImageIO.read(new File("H:/git/colsim/src/sprites/GrassBushLabyrinthTileset.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiles = new BufferedImage[tsHeight*tsWidth];

        for (int y = 0; y < tsHeight; y++) {
            for (int x = 0; x < tsWidth; x++) {
                tiles[(y*tsWidth)+x] = tileSet.getSubimage(x*tsWidth, y*tsHeight, tsSquareSize, tsSquareSize);
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int xPos = (e.getX() - offSetX) / squareSize + offCordX;
        int yPos = (e.getY() - offSetY) / squareSize + offCordY;
        game.mouseClick(e.getButton(), xPos, yPos);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {

            case 37: Keys.LEFT.pressed = true; break;
            case 38: Keys.UP.pressed = true; break;
            case 39: Keys.RIGHT.pressed = true; break;
            case 40: Keys.DOWN.pressed = true; break;
            case 123: Keys.F12.pressed = true; game.setDevMode(!game.getDevMode()); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {

            case 37: Keys.LEFT.pressed = false; break;
            case 38: Keys.UP.pressed = false; break;
            case 39: Keys.RIGHT.pressed = false; break;
            case 40: Keys.DOWN.pressed = false; break;
            case 123: Keys.F12.pressed = false; break;

        }
    }
}