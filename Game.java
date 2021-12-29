import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class Game extends JFrame{
	private Map map;
	private int cameraXV = 0, cameraYV = 0;
	private float cameraX = 0, cameraY = 0;
	private byte[] tiles = {0, 1, 2, 3};
	private byte selectedTile = 0;
	static private Game game;

	Game() {
		map = new Map();
		map.loadTileset("img/tileset.png");
	}

	private void initGUI() {
		setSize(640, 480);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Java Hex Map");

		add(new MyPanel());
		addKeyListener(new MyKeyListener());
		addMouseListener(new MyMouseListener());

		setVisible(true);
	}

	public static void main(String args[]) {
		game = new Game();
		game.loadLevel("lvl/0");
		game.initGUI();
		game.run();
	}

	private void run() {
		double lastUpdate = System.currentTimeMillis();

		while(isVisible()) {
			double currentTime = System.currentTimeMillis();
			double diff = currentTime-lastUpdate;
			if(diff > 15) {
				update(diff);
				lastUpdate = currentTime;
			}
		}

		saveLevel("lvl/0");
		System.exit(0);
	}

	private void loadLevel(String filename) {
		map.load(filename.concat("/map.bin"));
	}

	private void saveLevel(String filename) {
		map.save(filename.concat("/map.bin"));
	}

	private void update(double diff) {
		cameraX += cameraXV*0.25*diff;
		cameraY += cameraYV*0.3*diff;
		if(cameraXV != 0 || cameraYV != 0)
			repaint();
	}

	private class MyPanel extends JPanel {
		public void paint(Graphics g) {
			super.paintComponent(g);
			map.draw((Graphics2D)g, -(int)cameraX, -(int)cameraY);

			Dimension d = new Dimension();
			getSize(d);
			int w = d.width, h = d.height;

			for(int i = 0; i < tiles.length; i++) {
				int sx = (tiles[i]%4)*64,
					sy = (tiles[i]/4)*64;
				BufferedImage tile = map.tileset.getSubimage(sx, sy, 64, 64);
				g.drawImage(tile, w-64, i*64, null);
			}
		}
	}

	private class MyKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				cameraYV = -1;
				break;
			case KeyEvent.VK_DOWN:
				cameraYV = 1;
				break;
			case KeyEvent.VK_LEFT:
				cameraXV = -1;
				break;
			case KeyEvent.VK_RIGHT:
				cameraXV = 1;
				break;
			}
		}

		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				if(cameraYV < 0) cameraYV = 0;
				break;
			case KeyEvent.VK_DOWN:
				if(cameraYV > 0) cameraYV = 0;
				break;
			case KeyEvent.VK_LEFT:
				if(cameraXV < 0) cameraXV = 0;
				break;
			case KeyEvent.VK_RIGHT:
				if(cameraXV > 0) cameraXV = 0;
				break;
			}
		}

		public void keyTyped(KeyEvent e) {}
	}

	private class MyMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			int x = e.getX(), y = e.getY();

			Dimension d = new Dimension();
			game.getSize(d);
			int w = d.width, h = d.height;

			if(x >= w-64 && y >= 0 &&
					x < w && y < 64*tiles.length) {
				selectedTile = tiles[(y-16)/64];
				return;
			}

			x += cameraX-16;
			x /= 48;
			y += cameraY - 16 - (x%2)*32;
			y /= 64;

			map.setTile(x, y, (byte)selectedTile);
			repaint();
		}

		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}
}
