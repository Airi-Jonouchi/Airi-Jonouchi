package arpg.base.event.story;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import javax.imageio.ImageIO;

import static arpg.main.Common.*;

public class ImageStatus {

	private static final int IMAGE_X = 150;
	private static final int IMAGE_Y = 332;

	private static final int IMAGE_X_2 = 400;
	private static final int IMAGE_Y_2 = 300;

	private int x;
	private int y; 
	private int id;
	private String keyName;
	private static BufferedImage telopTownImage;
	private static BufferedImage telopCharacterImage;
	private BufferedImage image;
	private String imageTitle;

	private ImageStatus(int x, int y, int id, String keyName, String imageTitle) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.keyName = keyName;
		this.imageTitle = imageTitle;

		if(telopCharacterImage == null && telopTownImage == null) {
			lordImage();
		}
	}

	private static void lordImage() {

		try {
			telopCharacterImage = ImageIO.read(ImageStatus.class.getResourceAsStream("image/eiyuu.png"));
			telopTownImage = ImageIO.read(ImageStatus.class.getResourceAsStream("image/town.png"));
		} catch (IOException e) {	
			throw new UncheckedIOException(e);
		}
	}

	public static ImageStatus getReference(String keyName) {
		
		Map<String, ImageStatus> map = Map.ofEntries(
			Map.entry("アレイストス", new ImageStatus(280, 150, 0, "アレイストス", "chara")),
			Map.entry("ローカA", new ImageStatus(190, 160, 0, "ローカA", "town")),
			Map.entry("ローカB", new ImageStatus(190, 160, 1, "ローカB", "town"))
		);
		return map.getOrDefault(keyName, new ImageStatus(-100, -100, -1, "eroor", "town"));
	}

	public void drawImage(Graphics2D g2) {
		g2.setComposite(GHOST);
		int width, height;
		if(imageTitle.equals("chara")) {
			width = IMAGE_X;
			height = IMAGE_Y;
			image = telopCharacterImage;
		}
		else {
			width = IMAGE_X_2;
			height = IMAGE_Y_2;
			image = telopTownImage;
		}
		int sx = 0;
		int sy = id * height;
		g2.drawImage(image, x, y, x + width, y + height, sx, sy, sx + width, sy + height, null);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public String getTitle() {
		return this.imageTitle;
	}
}
