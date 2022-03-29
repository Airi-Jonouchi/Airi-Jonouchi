package arpg.base.item;

import arpg.base.item.Item.ItemGenru;

public class Equipment {

	private static final Item DEFAULT_ITEM = new Item("ダミー", 0, ItemGenru.OTHER, 0, 0, 0, 0, 0);
	private static final Equipment DEFULTE_EQUIPMENT = new Equipment(
		new Item("短剣", 0, ItemGenru.WEAPON, 0, 0, 0, 0, 0),
		new Item("なし", 30, ItemGenru.SHIELD, 0, 0, 0, 0, 0),
		new Item("服", 10, ItemGenru.ARMOR, 0, 0, 0, 0, 0),
		new Item("なし", 30, ItemGenru.HELMET, 0, 0, 0, 0, 0)
	);

	private Item wepon;
	private Item shield;
	private Item armor;
	private Item helmet;

	public Equipment(Item wepon, Item shield, Item armor, Item helmet) {
		this.wepon = wepon;
		this.shield = shield;
		this.armor = armor;
		this.helmet = helmet;
	}

	public Item[] getEquipmentSet() {
		Item[] set = {this.wepon, this.shield, this.armor, this.helmet};
		return set;
	}
	
	public Item getWepon() {
		return this.wepon;
	}

	public void setWepon(Item wepon) {
		this.wepon = wepon;
	}

	public Item getShield() {
		return this.shield;
	}

	public void setShield(Item shield) {
		this.shield = shield;
	}

	public Item getArmor() {
		return this.armor;
	}

	public void setArmor(Item armor) {
		this.armor = armor;
	}

	public Item getHelmet() {
		return this.helmet;
	}

	public void setHelmet(Item helmet) {
		this.helmet = helmet;
	}

	public static Item getDefault(String parts) {

		switch (parts) {
			case "短剣" -> {
				return DEFULTE_EQUIPMENT.wepon;
			}
			case "なし腕" -> {
				return DEFULTE_EQUIPMENT.shield;
			}
			case "服" -> {
				return DEFULTE_EQUIPMENT.armor;
			}
			case "なし頭" -> {
				return DEFULTE_EQUIPMENT.helmet;
			}
			default -> {
				return DEFAULT_ITEM;
			}
		}
	}
}
