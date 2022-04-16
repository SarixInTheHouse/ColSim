package src;
public class Game {

    private Display display;

    private final int mapWidth, mapHeight, squareSize;
    private int[][] map;

    public Game(int width, int height) {

        mapWidth = 102;
        mapHeight = 102;
        squareSize = 32;

        //creating basic map
        map = new int[mapWidth][mapHeight]; 
        boolean hInv = false;
        boolean vInv = false;
        for (int x = 0; x < map.length; x++) {
            hInv = !hInv;
            for (int y = 0; y < map[x].length; y++) {
                vInv = !vInv;
                if (hInv) {
                    if (vInv) { 
                        map[x][y] = 1;
                    }
                    else map[x][y] = 2;
                } else {
                    if (vInv) map[x][y] = 3;
                    else map[x][y] = 4;
                }
            }
        }

        System.out.println(map[2][2]);

        //comes last so values get transported properly
        display = new Display(this);
    }

    public void update() {

    }

    public void render() {
		display.render();
	}

    public int getMapWidth() {return mapWidth;}
    public int getMapheight() {return mapHeight;}
    public int getSquareSize() {return squareSize;}
    public int[][] getMap() {return map;}

}
