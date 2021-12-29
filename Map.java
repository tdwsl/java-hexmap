import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;

class Map {
	private byte[] arr;
	public int w=0, h=0;
	public BufferedImage tileset;
	private BufferedImage mapDrawn;
	private boolean shouldRender = true;

	public void load(String filename) {
		try {
			InputStream in = new FileInputStream(filename);
			byte hb[] = new byte[2];
			byte wb[] = new byte[2];

			in.read(wb);
			in.read(hb);
			w = wb[0]*256 + wb[1];
			h = hb[0]*256 + hb[1];

			arr = new byte[w*h];
			in.read(arr);

			in.close();
		}
		catch(IOException e) {
			System.out.printf("Failed to load map '%s'\n",
				filename);
			e.printStackTrace();
		}
	}

	public void save(String filename) {
		try {
			OutputStream out = new FileOutputStream(filename);
			byte wb[] = {(byte)(w/256), (byte)(w%256)};
			byte hb[] = {(byte)(h/256), (byte)(h%256)};
			out.write(wb);
			out.write(hb);
			out.write(arr);
			out.close();
		}
		catch(IOException e) {
			System.out.printf("Failed to save map to '%s'\n",
				filename);
			e.printStackTrace();
		}
	}

	public void loadTileset(String filename) {
		try {
			tileset = ImageIO.read(getClass()
				.getResourceAsStream(filename));
		}
		catch(IOException e) {
			System.out.printf("Failed to load tileset '%s'\n",
				filename);
			e.printStackTrace();
		}
	}

	public byte getTile(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h)
			return (byte)0xff;
		return arr[y*w+x];
	}

	public void setTile(int x, int y, byte t) {
		if(x < 0 || y < 0 || x >= w || y >= h)
			return;
		if(t == arr[y*w+x])
			return;
		arr[y*w+x] = t;
		shouldRender = true;
	}

	public boolean tileBlocks(byte t) {
		switch(t) {
		case 1:
		case 2:
		case 3:
			return true;
		default:
			return false;
		}
	}

	public boolean blocks(int x, int y) {
		return tileBlocks(getTile(x, y));
	}

	public void draw(Graphics g, int xo, int yo) {
		if(shouldRender) {
			if(w == 0 || h == 0)
				return;
			if(tileset == null)
				return;
			shouldRender = false;
			render();
		}

		g.drawImage(mapDrawn, xo, yo, null);
	}

	private void render() {
		GraphicsEnvironment ge = GraphicsEnvironment
			.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();

		mapDrawn = gc.createCompatibleImage(w*48+16, h*64+32,
			BufferedImage.TRANSLUCENT);
		Graphics2D g = mapDrawn.createGraphics();

		for(int i = 0; i < w*h; i++) {
			int sx = (arr[i]%4)*64, sy = (arr[i]/4)*64;

			int tx = i%w, ty = i/w;
			int x = tx*48;
			int y = ty*64 + (tx%2)*32;

			g.drawImage(
				tileset.getSubimage(sx, sy, 64, 64),
				x, y, 64, 64,
				null);
		}
	}
}
