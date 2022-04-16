package src;
public class Launcher {

	public static void main(String[] args) {
		new Thread(new GameLoop(new Game(640, 480))).start();
	}
	
}