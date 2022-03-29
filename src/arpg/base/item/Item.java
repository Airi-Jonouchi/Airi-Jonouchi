package arpg.base.item;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class Item {

	private static final int NUMBER_OF_SHEETS = 16;
	private static final int ICON_SIZE = 24;

	public enum ItemGenru {
	
		RECOVERY, STATUS, WEAPON, SHIELD, ARMOR, HELMET, IMPORTANT, WAND, OTHER;
	}

	private String name;
	private int iconNo;
	private ItemGenru genru;
	private int price;
	private int sell;
	private int atk;
	private int def;
	private int point;
	private boolean nowEquipment;

	private static BufferedImage itemIcon;

	public Item(String name, int iconNo, ItemGenru genru, int price, int sell, int atk, int def, int point) {
		this.name = name;
		this.iconNo = iconNo;
		this.genru = genru;
		this.price = price;
		this.sell = sell;
		this.atk = atk;
		this.def = def;
		this.point = point;
		this.nowEquipment = false;

		if(itemIcon == null) {
			lordImage();
		}	
	}

	private void lordImage() {
		try {
			itemIcon = ImageIO.read(getClass().getResourceAsStream("image/item_icon.png"));
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public void drawIcon(int dx, int dy, Graphics g) {
		int sx = (iconNo % NUMBER_OF_SHEETS) * ICON_SIZE;
		int sy = (iconNo / NUMBER_OF_SHEETS) * ICON_SIZE;
		g.drawImage(itemIcon, dx, dy, dx + ICON_SIZE, dy + ICON_SIZE, sx, sy, sx + ICON_SIZE, sy + ICON_SIZE, null);
	}

	public static Item getReference(String name) {

		if(name.matches("^[0-9]+")) {
			return 	new Item("お金", 20, ItemGenru.OTHER, 0, 0, 0, 0, Integer.parseInt(name));
		};

		Item item = null;
		switch(name) {
			case "短剣", "なし腕", "服", "なし頭" -> {
				item = Equipment.getDefault(name);
			}
			default -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(Item.class.getResourceAsStream("data/item.csv")));) {
					String line;
					boolean flag = false;
					while(!flag) {
						line = reader.readLine();
						if(!line.startsWith(name) || line.startsWith("\"") || line.startsWith("#")) {
							continue;
						}
						else {
							StringTokenizer st = new StringTokenizer(line, ",");
							String itemName = st.nextToken();
							int iconNo = Integer.parseInt(st.nextToken());
							ItemGenru genru = ItemGenru.valueOf(st.nextToken());
							int price = Integer.parseInt(st.nextToken());
							int sell = Integer.parseInt(st.nextToken());
							int atk = Integer.parseInt(st.nextToken());
							int def = Integer.parseInt(st.nextToken());
							int point = Integer.parseInt(st.nextToken());

							item = new Item(itemName, iconNo, genru, price, sell, atk, def, point);
							flag = true;
						}
					}
				}
				catch(IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		}
		return item;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconNO() {
		return this.iconNo;
	}

	public void setIconNo(int iconNo) {
		this.iconNo = iconNo;
	}

	public boolean getNowEquipment() {
		return this.nowEquipment;
	}

	public ItemGenru getGenru() {
		return this.genru;
	}

	public void setGenru(ItemGenru genru) {
		this.genru = genru;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getSell() {
		return this.sell;
	}

	public void setSell(int sell) {
		this.sell = sell;
	}

	public int getAtk() {
		return this.atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getDef() {
		return this.def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getPoint() {
		return this.point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public boolean isNowEquipment() {
		return this.nowEquipment;
	}

	public void setNowEquipment(boolean nowEquipment) {
		this.nowEquipment = nowEquipment;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Item)) {
			return false;
		}
		Item item = (Item) o;
		return Objects.equals(name, item.name) && iconNo == item.iconNo && Objects.equals(genru, item.genru) 
			&& price == item.price && sell == item.sell && atk == item.atk && def == item.def && point == item.point
			&& nowEquipment == item.nowEquipment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, iconNo, genru, price, sell, atk, def, point, nowEquipment);
	}
}