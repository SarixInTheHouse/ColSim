package src;
public class GameLoop implements Runnable{

	private boolean running;
	
	private final double updateRate = 1.0d/60.0d;
	
	private long nextStatTime;
	private int fps, ups;
	
	private Game game;
	
	public GameLoop(Game game) {
		this.game = game;
	}
	
	
	public void run() {
		running = true;
		
		double accumulator = 0;
		long currentTime, lastUpdate = System.currentTimeMillis();
		nextStatTime = System.currentTimeMillis() + 1000;
		
		while (running) {
			currentTime = System.currentTimeMillis();
			double lastRenderTimeInSeconds = (currentTime - lastUpdate) / 1000.0d;
			accumulator += lastRenderTimeInSeconds;
			lastUpdate = currentTime;
			
			while (accumulator > updateRate) {
				update();
				accumulator -= updateRate;
			}
			render();
			
			printStats();
		}
	}
	
	private void printStats() {
		if (System.currentTimeMillis() > nextStatTime) {
			System.out.println(String.format("FPS: %d, UPS: %d", fps, ups));
			fps = 0;
			ups = 0;
			nextStatTime = System.currentTimeMillis() + 1000;
		}
	}
	
	public void update() {
		game.update();
		ups++;
	}
	
	public void render() {
		game.render();
		fps++;
	}
}