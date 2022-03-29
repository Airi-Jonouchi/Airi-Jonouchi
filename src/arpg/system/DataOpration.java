package arpg.system;

import static arpg.main.Common.*;
import static arpg.main.MainPanel.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import arpg.base.event.map.Treasure;
import arpg.base.item.Item;
import arpg.base.magic.Magic;
import arpg.main.MainPanel;
import arpg.main.Common.Direction;
import arpg.main.Common.MapDataPath;

import arpg.personae.Hero;
import arpg.prameter.status.HeroStatus;



public class DataOpration {

	public enum SaveDataPath {
		SAVE_1("save01.dat"), SAVE_2("save02.dat");

		private String path;
		private SaveDataPath(String path) {
			this.path = path;
		}

		public String getPath() {
			return this.path;
		}

	}
	private MainPanel panel;
	private Path path;
	private Hero hero;
	private int mapId;
	private HeroStatus status;
	private Treasure treasure;
	private Secret secret;

	public DataOpration(SaveDataPath save, MainPanel panel) {
		
		this.panel = panel;
		path = Paths.get(save.path);
		secret = new Secret();
	}

	public void save() {

		hero = panel.getHero();
		mapId = panel.getCurrentMap().getSelectMap().getId();
		status = hero.getStatus();
		treasure = new Treasure();
	
		List<Item> itemBag = status.getItemBag();
		List<Item> keyItems = status.getKeyItems();
		List<Magic> masterMagics = status.getMasterMagics();
		boolean[] treasureTable = treasure.getTreasureTable();

		StringBuilder builder = new StringBuilder();
		String data;

		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			
			builder.append("STANDARD").append(",");
			builder.append(hero.getName()).append(",");
			builder.append(hero.getX()).append(",");
			builder.append(hero.getY()).append(",");
			builder.append(hero.getDirection().getId()).append(",");
			builder.append(mapId).append(",");
			data = builder.toString();
			writer.write(secret.encrypt(data));
			builder.delete(0, builder.length());
			writer.newLine();
			
			builder.append("STATUS").append(",");
			builder.append(status.getName()).append(",");
			builder.append(status.getLevel()).append(",");
			builder.append(status.getHp()).append(",");
			builder.append(status.getMp()).append(",");
			builder.append(status.getStr()).append(",");
			builder.append(status.getAgi()).append(",");
			builder.append(status.getVit()).append(",");
			builder.append(status.getMgi()).append(",");
			builder.append(status.getRes()).append(",");
			builder.append(status.getMaxHp()).append(",");
			builder.append(status.getMaxMp()).append(",");
			data = builder.toString();
			writer.write(secret.encrypt(data));
			builder.delete(0, builder.length());
			writer.newLine();
	
			builder.append("ITEMBAG,");
			itemBag.stream().forEach(v -> {
				builder.append(v.getName()).append(",");
				if(v.isNowEquipment()) {
					builder.append(1).append(",");
				}
				else {
					builder.append(0).append(",");
				}
			});
			data = builder.toString();
			writer.write(secret.encrypt(data));
			builder.delete(0, builder.length());
			writer.newLine();

			builder.append("KEYITEM,");
			keyItems.stream().forEach(v -> builder.append(v.getName()).append(","));
			data = builder.toString();
			writer.write(secret.encrypt(data));
			builder.delete(0, builder.length());
			writer.newLine();

			builder.append("MASTERMAGICS,");
			masterMagics.stream().forEach(v -> builder.append(v.getName()).append(","));
			data = builder.toString();
			writer.write(secret.encrypt(data));
			builder.delete(0, builder.length());
			writer.newLine();

			builder.append("TREASURE,");
			IntStream.range(0, TOTAL_TREASURE).forEach(e -> builder.append(treasureTable[e]).append(","));
			data = builder.toString();
			writer.write(secret.encrypt(data));
			builder.delete(0, builder.length());
			writer.newLine();

			builder.append("MONEY,");
			builder.append(money);
			data = builder.toString();
			writer.write(secret.encrypt(data));
			//builder.delete(0, builder.length());
			//writer.newLine();	
		}
		catch (IOException  e) {
			throw new UncheckedIOException(e);
		}
	}

	public void lord() {

		hero = panel.getHero();
		status = hero.getStatus();
		status.empty();
		money = 0;

		try(BufferedReader reader = Files.newBufferedReader(path)) {

			String line;
			while((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(secret.decrypt(line) ,",");
				String data = st.nextToken();
				switch(data) {
					case "STANDARD" -> lordStandard(st);
					case "STATUS" -> lordStatus(st);
					case "ITEMBAG" -> lordItem(st);
					case "KEYITEM" -> lordKeyItem(st);
					case "MASTERMAGICS" -> lordMagic(st);
					case "TREASURE" -> lordTreasure(st);
					case "MONEY" -> lordMoney(st);			
				}
			}	
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void lordStandard(StringTokenizer st) {
		hero.setName(st.nextToken());
		hero.setX(Integer.parseInt(st.nextToken()));
		hero.setY(Integer.parseInt(st.nextToken()));
		hero.setId(0);
		hero.setDirection(Direction.values()[Integer.parseInt(st.nextToken())]);
		panel.lordMap(MapDataPath.values()[Integer.parseInt(st.nextToken())]);
	}

	private void lordStatus(StringTokenizer st) {
		status.setName(st.nextToken());
		status.setLevel(Integer.parseInt(st.nextToken()));
		status.setHp(Integer.parseInt(st.nextToken()));
		status.setMp(Integer.parseInt(st.nextToken()));
		int str = Integer.parseInt(st.nextToken());
		status.setStr(str);
		status.setAgi(Integer.parseInt(st.nextToken()));
		int vit = Integer.parseInt(st.nextToken());
		status.setVit(vit);
		status.setMgi(Integer.parseInt(st.nextToken()));
		status.setRes(Integer.parseInt(st.nextToken()));
		status.setMaxHp(Integer.parseInt(st.nextToken()));
		status.setMaxMp(Integer.parseInt(st.nextToken()));
		status.setAtk(str);
		status.setDef(vit);
	}

	private void lordItem(StringTokenizer st) {

		while(st.hasMoreTokens()) {
			Item item = Item.getReference(st.nextToken());
			status.addItemBag(item);

			int equip = Integer.parseInt(st.nextToken());
			if(equip == 1) {
				status.lordEquipment(item, hero.getAttackOperation());
			}
		}
	}

	private void lordKeyItem(StringTokenizer st) {
		
		while(st.hasMoreTokens()) {
			Item item = Item.getReference(st.nextToken());
			status.addKeyItem(item);
		}
	}

	private void lordMagic(StringTokenizer st) {

		while(st.hasMoreTokens()) {
			Magic magic = Magic.getReference(st.nextToken());
			status.masterMagic(magic);
		}
	}

	private void lordTreasure(StringTokenizer st) {
		treasure = new Treasure();
		IntStream.range(0, TOTAL_TREASURE).forEach(e -> {
			boolean data = Boolean.valueOf(st.nextToken());
			treasure.lordTreasureTable(e, data);
		});
	}

	private void lordMoney(StringTokenizer st) {
		money = Integer.parseInt(st.nextToken());
	}
}
