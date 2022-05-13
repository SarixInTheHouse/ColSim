
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
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


// TODO need to fix how tileset is made. currently oddly cut; offset

public class Display extends JFrame implements KeyListener, MouseListener {
	
    private final int squareSize, mapHeight, mapWidth, tsHeight, tsWidth, tsSquareSize;
    private int visHeight, visWidth, offSetX, offSetY, offCordX, offCordY;

    private int[][] map;
    private String[] workspaces;

    private BufferedImage tileSet;
    private BufferedImage[] tiles;

    private Rectangle[] buttons;

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

        fillButtons();

        workspaces = new String[2];
        workspaces[0] = "H:/git";

        setTitle("colony simulator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setMaximizedBounds(env.getMaximumWindowBounds());
        this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);

		mainPanel = new Panel();
		mainPanel.setFocusable(true);
		mainPanel.setVisible(true);
		
		canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(this.getBounds().width, this.getBounds().height));
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
    }

    public void render() {

        visHeight = (int) this.getBounds().height / squareSize + 2;
        visWidth = (int) this.getBounds().width / squareSize + 2;

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
        
        //render map
        for (int x = 0; x < visWidth; x++) {
            for (int y = 0; y < visHeight; y++) {
                BufferedImage img;
                img = tiles[map[x+offCordX][y+offCordY]];
                g.drawImage(img, x*squareSize+offSetX, y*squareSize+offSetY, null);
            }
        }

        if (game.getDevMode()) {
            // render palette
            g.setColor(Color.black);
            g.fillRect((visWidth-tsWidth)*squareSize-4, (visHeight-tsHeight)*squareSize-8, tsWidth*squareSize+8, tsHeight*squareSize+4);
            for (int y = 0; y < tsHeight; y++) {
                for (int x = 0; x < tsWidth; x++) {
                    g.drawImage(tiles[(y*tsWidth+x)], (x + visWidth - tsWidth)*squareSize, (y+visHeight-tsHeight)*squareSize-4, null);
                }
            }


        } else {
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
                tiles[(y*tsWidth)+x] = tileSet.getSubimage(x*tsSquareSize, y*tsSquareSize, tsSquareSize, tsSquareSize);
            }
        }

    }

    public void fillButtons() {
        buttons = new Rectangle[2];

        // game field
        buttons[0] = new Rectangle(0, 0, (visWidth-2)*squareSize, (visHeight-2)*squareSize);
        // pallette 
        buttons[1] = new Rectangle(0, (visHeight-2+1)*squareSize, (visWidth-2)*squareSize, 15*squareSize);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // int xPos = (e.getX() - offSetX) / squareSize + offCordX;
        // int yPos = (e.getY() - offSetY) / squareSize + offCordY;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(new Point(e.getX(), e.getY()))) {
                game.mouseClick(new MouseData(e.getButton(), e.getX(), e.getY(), i, offSetX, offSetY, offCordX, offCordY));
                break;
            }   
        }
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

            case 37: Keys.LEFT.pressed = true;break;
            case 38: Keys.UP.pressed = true; break;
            case 39: Keys.RIGHT.pressed = true; break;
            case 40: Keys.DOWN.pressed = true; break;
            case 66: game.setDevMode(!game.getDevMode()); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {

            case 37: Keys.LEFT.pressed = false; break;
            case 38: Keys.UP.pressed = false; break;
            case 39: Keys.RIGHT.pressed = false; break;
            case 40: Keys.DOWN.pressed = false; break;
            case 66: break;

        }
    }

    public int getVisWidth() {return visWidth;}
    public int getVisheight() {return visHeight;}
    public BufferedImage[] getTiles() {return tiles;}
    public int getTsWidth() {return tsWidth;}
    public int getTsHeight() {return tsHeight;}
}