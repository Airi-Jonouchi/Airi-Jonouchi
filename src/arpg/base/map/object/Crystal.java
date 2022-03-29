package arpg.base.map.object;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import static arpg.main.Common.*;


public class Crystal {
	
	private static final int RAW = 5;
	private static BufferedImage image;
	private int index;
	private int x;
	private int y;
	private boolean isRotation;

	public Crystal(int x, int y) {
		
		this.x = x;
		this.y = y;
		if(image == null) {
			lordImage();
		}
	}

	private void lordImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("../image/object/crystal.png"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void draw(int offsetX, int offsetY, Graphics g) {
		int sx = (index % RAW) * CHIP_SIZE;
		int sy = (index / RAW) * CHIP_SIZE;
		int dx = x * CHIP_SIZE + offsetX;
		int dy = y * CHIP_SIZE + offsetY;

		g.drawImage(image, dx, dy, dx + CHIP_SIZE, dy + CHIP_SIZE, sx, sy, sx + CHIP_SIZE, sy + CHIP_SIZE, null);
	}

	public void addCrystal() {
		rotation();
		isRotation = true;
	}

	public void close() {
		isRotation = false;
	}

	private void rotation() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			if(index < 19) {
				index++;
			}
			else {
				index = 0;
			}
			if(!isRotation) {
				service.shutdown();
			}
		}, 0, 200, TimeUnit.MILLISECONDS);
	}
}
