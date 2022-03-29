package arpg.system;

import static arpg.main.Common.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataLord {

	private MapDataPath select;
	private int height;
	private int width;
	private Map<String, int[][]> mapDatas;
	private Map<String, Integer> heightDatas;

	public DataLord(MapDataPath select) {
		this.select = select;
		height = 0;
		width = 0;
		mapDatas = new LinkedHashMap<>();
		heightDatas = new LinkedHashMap<>();
		
		jsonRead();
	}

	private void jsonRead() {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode nodes;
		clear();	
		try {
		     nodes = mapper.readTree(getClass().getResourceAsStream(select.getImagePath())); 
		}
		catch (IOException e){
			throw new UncheckedIOException(e);
		}
		height = nodes.get("height").asInt();
		width = nodes.get("width").asInt();
		
		for(JsonNode node : nodes.get("layers")) {
			int id = node.get("id").asInt();
			String key = node.get("name").asText();
			int[][] map = new int[height][width];
			var data = node.get("data");
			int index = 0;
			
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					map[y][x] = data.get(index).asInt() -1;
					if(index < data.size() - 1) {
						index++;
					}
				}
			}
			heightDatas.put(key, id);
			mapDatas.put(key, map);	
		}
	}

	public void setMapDataPath(MapDataPath select) {
		this.select = select;
		//jsonRead();
	}

	public Map<String, Integer> getHeightDatas() {
		return heightDatas;
	}

	public int getHeightData(String key) {
		return heightDatas.get(key);
	}

	public Map<String, int[][]> getMapDatas() {
		return mapDatas;
	}

	public int[][] getMapData(String key) {
		return mapDatas.get(key);
	}

	public void clear() {
		heightDatas.clear();
		mapDatas.clear();
	}

	public String[] getFieldKeys() {
		List<String> list = new ArrayList<>();
		mapDatas.entrySet().forEach(v -> {
			list.add(v.getKey());
		});
		Collections.reverse(list);
		
		String keys[] = list.toArray(new String[list.size()]);
		return keys;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
}
